/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.api.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.workers.provider.AddProviderWorker;
import org.openmrs.mobile.api.workers.provider.DeleteProviderWorker;
import org.openmrs.mobile.api.workers.provider.UpdateProviderWorker;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.listeners.retrofitcallbacks.DefaultResponseCallback;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderRepository extends BaseRepository {
    ProviderRoomDAO providerRoomDao;

    public ProviderRepository() {
        providerRoomDao = db.providerRoomDAO();
    }

    public ProviderRepository(OpenMRS openMrs, RestApi restApi, OpenMRSLogger logger) {
        super(openMrs, restApi, logger);
    }

    public void setProviderRoomDao(ProviderRoomDAO providerRoomDao) {
        this.providerRoomDao = providerRoomDao;
    }

    public LiveData<List<Provider>> getProviders() {

        MutableLiveData<List<Provider>> providerLiveData = new MutableLiveData<>();
        if (NetworkUtils.isOnline()) {
            restApi.getProviderList().enqueue(new Callback<Results<Provider>>() {
                @Override
                public void onResponse(@NotNull Call<Results<Provider>> call, @NotNull Response<Results<Provider>> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {
                            List<Provider> serversList = response.body().getResults();
                            providerRoomDao.insertAllOrders(serversList);
                            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                        } else {
                            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                        }
                    } else {
                        logger.e("Reading providers failed. Response: " + response.errorBody());
                        ToastUtil.error(openMrs.getString(R.string.unable_to_fetch_providers));
                        providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Results<Provider>> call, @NotNull Throwable t) {
                    logger.e("Reading providers failed.", t);
                    ToastUtil.error(openMrs.getString(R.string.unable_to_fetch_providers));
                    providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                }
            });
        } else {

            // offline data synced
            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());

            //offline notify
            ToastUtil.notify(openMrs.getString(R.string.offline_provider_fetch));
            logger.e("offline providers fetched couldnt sync with the database device offline");
        }
        return providerLiveData;
    }

    public void addProvider(Provider provider, DefaultResponseCallback callback) {

        if (NetworkUtils.isOnline()) {
            restApi.addProvider(provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {
                        //offline adding provider
                        provider.setId(providerRoomDao.addProvider(provider));

                        //editing the provider
                        providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(), response.body().getPerson(), response.body().getUuid(),
                            response.body().getIdentifier());

                        ToastUtil.success(openMrs.getString(R.string.add_provider_success_msg));
                        logger.e("Adding Provider Successful " + response.raw());
                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    logger.e("Failed to add provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(openMrs.getString(R.string.add_provider_failure_msg));
                }
            });
        } else {

            //offline addition operation
            Long providerId = providerRoomDao.addProvider(provider);

            //delegate to the workManager
            Data data = new Data.Builder().putString("first_name", provider.getPerson().getName().getGivenName())
                .putString("last_name", provider.getPerson().getName().getGivenName())
                .putString("identifier", provider.getIdentifier())
                .putLong("id", providerId).build();

            callback.onResponse();

            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(AddProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider creation
            ToastUtil.notify(openMrs.getString(R.string.offline_provider_add));
            logger.e("provider will be synced to the server when device gets connected to network");
        }
    }

    public void updateProvider(Provider provider, DefaultResponseCallback callback) {

        if (NetworkUtils.isOnline()) {
            restApi.UpdateProvider(provider.getUuid(), provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {

                        //offline update operation
                        providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(), response.body().getPerson(), response.body().getUuid(),
                            response.body().getIdentifier());

                        ToastUtil.success(openMrs.getString(R.string.edit_provider_success_msg));
                        logger.e("Editing Provider Successful " + response.raw());
                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    logger.e("Failed to edit provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(openMrs.getString(R.string.edit_provider_failure_msg));
                }
            });
        } else {

            //offline update operation
            providerRoomDao.updateProviderByUuid(provider.getDisplay(), provider.getId(), provider.getPerson(), provider.getUuid(), provider.getIdentifier());
            callback.onResponse();

            //delegate to the WorkManager for this work
            Data data = new Data.Builder().putString("uuid", provider.getUuid()).build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(UpdateProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider updation
            ToastUtil.success(openMrs.getString(R.string.offline_provider_edit));
            logger.e("updated provider will be synced to the server when device gets connected to network");
        }
    }

    public void deleteProviders(String providerUuid, DefaultResponseCallback callback) {

        //when callback would call onResponse successfull the UI will refresh automatically
        if (NetworkUtils.isOnline()) {
            restApi.deleteProvider(providerUuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                        // offline deletion
                        providerRoomDao.deleteByUuid(providerUuid);

                        ToastUtil.success(openMrs.getString(R.string.delete_provider_success_msg));
                        logger.e("Deleting Provider Successful " + response.raw());

                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    logger.e("Failed to delete provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(openMrs.getString(R.string.delete_provider_failure_msg));
                }
            });
        } else {

            // offline deletion
            Data data = new Data.Builder().putString("uuid", providerUuid).build();
            providerRoomDao.deleteByUuid(providerUuid);
            callback.onResponse();

            // enqueue the work to workManager
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(DeleteProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider deletion
            ToastUtil.success(openMrs.getString(R.string.offline_provider_delete));
            logger.e("Provider will be removed from the server when you're back online");
        }
    }

    public LiveData<List<LocationEntity>> getLocation(String url) {
        MutableLiveData<List<LocationEntity>> locations = new MutableLiveData<>();
        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            Call<Results<LocationEntity>> call =
                restApi.getLocations(locationEndPoint, ApplicationConstants.API.TAG_ADMISSION_LOCATION, ApplicationConstants.API.FULL);
            call.enqueue(new Callback<Results<LocationEntity>>() {
                @Override
                public void onResponse(Call<Results<LocationEntity>> call, Response<Results<LocationEntity>> response) {
                    if (response.isSuccessful()) {
                        locations.setValue(response.body().getResults());
                    } else {
                        locations.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Results<LocationEntity>> call, Throwable t) {
                    locations.setValue(null);
                }
            });
        } else {
            locations.setValue(null);
        }
        return locations;
    }

    public LiveData<List<Resource>> getEncounterRoles() {
        MutableLiveData<List<Resource>> encounterRoles = new MutableLiveData<>();
        restApi.getEncounterRoles().enqueue(new Callback<Results<Resource>>() {
            @Override
            public void onResponse(Call<Results<Resource>> call, Response<Results<Resource>> response) {
                if (response.isSuccessful()) {
                    encounterRoles.setValue(response.body().getResults());
                } else {
                    encounterRoles.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Results<Resource>> call, Throwable t) {
                encounterRoles.setValue(null);
            }
        });
        return encounterRoles;
    }
}
