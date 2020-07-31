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

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.listeners.retrofit.CustomApiCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.workers.provider.AddProviderWorker;
import org.openmrs.mobile.api.workers.provider.DeleteProviderWorker;
import org.openmrs.mobile.api.workers.provider.UpdateProviderWorker;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.listeners.retrofit.EncounterResponseCallback;
import org.openmrs.mobile.listeners.retrofit.LocationResponseCallback;
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

public class ProviderRepository {
    ProviderRoomDAO providerRoomDao;
    WorkManager workManager;

    public ProviderRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        providerRoomDao = db.providerRoomDAO();
        workManager = WorkManager.getInstance(context);
    }

    public ProviderRepository() {
    }

    public void setProviderRoomDao(ProviderRoomDAO providerRoomDao) {
        this.providerRoomDao = providerRoomDao;
    }

    public LiveData<List<Provider>> getProviders(RestApi restApi) {

        MutableLiveData<List<Provider>> providerLiveData = new MutableLiveData<>();
        if (NetworkUtils.isOnline()) {
            restApi.getProviderList().enqueue(new Callback<Results<Provider>>() {
                @Override
                public void onResponse(@NotNull Call<Results<Provider>> call, @NotNull Response<Results<Provider>> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {
                            List<Provider> offlineList = providerRoomDao.getProviderList().blockingGet();
                            List<Provider> serversList = response.body().getResults();

                            if (offlineList.isEmpty()) {
                                providerRoomDao.insertAllOrders(serversList);
                                providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());
                            } else {
                                providerLiveData.setValue(offlineList);
                            }
                        } else {
                            providerLiveData.setValue(null);
                        }
                    } else {
                        OpenMRS.getInstance().getOpenMRSLogger().e("Reading providers failed. Response: " + response.errorBody());
                        ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                        providerLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Results<Provider>> call, @NotNull Throwable t) {
                    OpenMRS.getInstance().getOpenMRSLogger().e("Reading providers failed.", t);
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                    providerLiveData.setValue(null);
                }
            });
        } else {

            // offline data synced
            providerLiveData.setValue(providerRoomDao.getProviderList().blockingGet());

            //offline notify
            ToastUtil.notify(OpenMRS.getInstance().getString(R.string.offline_provider_fetch));
            OpenMRS.getInstance().getOpenMRSLogger().e("offline providers fetched couldnt sync with the database device offline");
        }
        return providerLiveData;
    }

    public void addProvider(RestApi restApi, Provider provider, CustomApiCallback callback) {

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

                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.add_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Adding Provider Successful " + response.raw());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.add_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to add provider. Error:  " + t.getMessage());
                    callback.onFailure();
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

            callback.onSuccess();

            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(AddProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider creation
            ToastUtil.notify(OpenMRS.getInstance().getString(R.string.offline_provider_add));
            OpenMRS.getInstance().getOpenMRSLogger().e("provider will be synced to the server when device gets connected to network");
        }
    }

    public void updateProvider(RestApi restApi, Provider provider, CustomApiCallback callback) {

        if (NetworkUtils.isOnline()) {
            restApi.UpdateProvider(provider.getUuid(), provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {

                        //offline update operation
                        providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(), response.body().getPerson(), response.body().getUuid(),
                            response.body().getIdentifier());

                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.edit_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Editing Provider Successful " + response.raw());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.edit_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to edit provider. Error:  " + t.getMessage());
                    callback.onFailure();
                }
            });
        } else {

            //offline update operation
            providerRoomDao.updateProviderByUuid(provider.getDisplay(), provider.getId(), provider.getPerson(), provider.getUuid(), provider.getIdentifier());
            callback.onSuccess();

            //delegate to the WorkManager for this work
            Data data = new Data.Builder().putString("uuid", provider.getUuid()).build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(UpdateProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider updation
            ToastUtil.success(OpenMRS.getInstance().getString(R.string.offline_provider_edit));
            OpenMRS.getInstance().getOpenMRSLogger().e("updated provider will be synced to the server when device gets connected to network");
        }
    }

    public void deleteProviders(RestApi restApi, String providerUuid, CustomApiCallback callback) {

        //when callback would call onResponse successfull the UI will refresh automatically
        if (NetworkUtils.isOnline()) {
            restApi.deleteProvider(providerUuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                        // offline deletion
                        providerRoomDao.deleteByUuid(providerUuid);

                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.delete_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Deleting Provider Successful " + response.raw());

                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.delete_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to delete provider. Error:  " + t.getMessage());
                    callback.onFailure();
                }
            });
        } else {

            // offline deletion
            Data data = new Data.Builder().putString("uuid", providerUuid).build();
            providerRoomDao.deleteByUuid(providerUuid);
            callback.onSuccess();

            // enqueue the work to workManager
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(DeleteProviderWorker.class).setConstraints(constraints).setInputData(data).build());

            //toast about deferred provider deletion
            ToastUtil.success(OpenMRS.getInstance().getString(R.string.offline_provider_delete));
            OpenMRS.getInstance().getOpenMRSLogger().e("Provider will be removed from the server when you're back online");
        }
    }

    public void getLocation(RestApi restApi, String url, LocationResponseCallback callback) {
        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            Call<Results<LocationEntity>> call =
                restApi.getLocations(locationEndPoint, ApplicationConstants.API.TAG_ADMISSION_LOCATION, ApplicationConstants.API.FULL);
            call.enqueue(new Callback<Results<LocationEntity>>() {
                @Override
                public void onResponse(Call<Results<LocationEntity>> call, Response<Results<LocationEntity>> response) {
                    if (callback != null) {
                        if (response.isSuccessful()) {
                            callback.onResponse(response.body().getResults());
                        } else {
                            callback.onErrorResponse(OpenMRS.getInstance().getString(R.string.error_occurred));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Results<LocationEntity>> call, Throwable t) {
                    callback.onErrorResponse(t.getMessage());
                }
            });
        } else {
            if (callback != null) {
                callback.onErrorResponse(OpenMRS.getInstance().getString(R.string.error_occurred));
            }
        }
    }

    public void getEncounterRoles(RestApi restApi, EncounterResponseCallback callback) {
        restApi.getEncounterRoles().enqueue(new Callback<Results<Resource>>() {
            @Override
            public void onResponse(Call<Results<Resource>> call, Response<Results<Resource>> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body().getResults());
                    } else {
                        callback.onErrorResponse(OpenMRS.getInstance().getString(R.string.error_occurred));
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<Resource>> call, Throwable t) {
                if (callback != null) {
                    callback.onErrorResponse(t.getMessage());
                }
            }
        });
    }
}



