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

package com.example.openmrs_android_sdk.library.api.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.example.openmrs_android_sdk.R;
import com.example.openmrs_android_sdk.library.OpenMRSLogger;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.api.workers.provider.AddProviderWorker;
import com.example.openmrs_android_sdk.library.api.workers.provider.DeleteProviderWorker;
import com.example.openmrs_android_sdk.library.api.workers.provider.UpdateProviderWorker;
import com.example.openmrs_android_sdk.library.dao.ProviderRoomDAO;
import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.example.openmrs_android_sdk.library.models.Provider;
import com.example.openmrs_android_sdk.library.models.Resource;
import com.example.openmrs_android_sdk.library.models.Results;
import com.example.openmrs_android_sdk.utilities.ApplicationConstants;
import com.example.openmrs_android_sdk.utilities.NetworkUtils;
import com.example.openmrs_android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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

    public ProviderRepository(RestApi restApi, OpenMRSLogger logger) {
        super(restApi, logger);
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

                            List<String> providerUuids = providerRoomDao.getCurrentUUIDs().blockingGet();
                            HashSet<String> checkUuids = new HashSet<>();

                            for (String element : providerUuids) {
                                if (element != null)
                                    checkUuids.add(element);
                            }

                            providerUuids.clear();
                            providerUuids = null;

                            for (Provider provider : serversList) {
                                if (checkUuids.contains(provider.getUuid()) == false) {
                                    providerRoomDao.addProvider(provider);
                                }

                                checkUuids.remove(provider.getUuid());
                            }

                            for (String uuid : checkUuids) {
                                providerRoomDao.deleteByUuid(uuid);
                            }

                            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                        } else {
                            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                        }
                    } else {
                        logger.e("Reading providers failed. Response: " + response.errorBody());
                        ToastUtil.error(OpenmrsAndroid.getInstance().getString(R.string.unable_to_fetch_providers));
                        providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Results<Provider>> call, @NotNull Throwable t) {
                    logger.e("Reading providers failed.", t);
                    ToastUtil.error(OpenmrsAndroid.getInstance().getString(R.string.unable_to_fetch_providers));
                    providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                }
            });
        } else {

            // offline data synced
            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());

            //offline notify
            ToastUtil.notify(context.getString(R.string.offline_provider_fetch));
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
                        providerRoomDao.addProvider(response.body());

                        ToastUtil.success(context.getString(R.string.add_provider_success_msg));
                        logger.e("Adding Provider Successful " + response.raw());
                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    logger.e("Failed to add provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(context.getString(R.string.add_provider_failure_msg));
                }
            });
        } else {

            //offline addition operation
            long providerId = providerRoomDao.addProvider(provider);

            //delegate to the workManager
            Data data = new Data.Builder().putLong("id", providerId).build();

            callback.onResponse();

            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(AddProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider creation
            ToastUtil.notify(context.getString(R.string.offline_provider_add));
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

                        ToastUtil.success(context.getString(R.string.edit_provider_success_msg));
                        logger.e("Editing Provider Successful " + response.raw());
                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    logger.e("Failed to edit provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(context.getString(R.string.edit_provider_failure_msg));
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
            ToastUtil.success(context.getString(R.string.offline_provider_edit));
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

                        ToastUtil.success(context.getString(R.string.delete_provider_success_msg));
                        logger.e("Deleting Provider Successful " + response.raw());

                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    logger.e("Failed to delete provider. Error:  " + t.getMessage());
                    callback.onErrorResponse(context.getString(R.string.delete_provider_failure_msg));
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
            ToastUtil.success(context.getString(R.string.offline_provider_delete));
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
