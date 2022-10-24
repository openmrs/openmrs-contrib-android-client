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

package com.openmrs.android_sdk.library.api.repository;

import static com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.API.FULL;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.API.REST_ENDPOINT;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.API.TAG_ADMISSION_LOCATION;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.workers.provider.AddProviderWorker;
import com.openmrs.android_sdk.library.api.workers.provider.DeleteProviderWorker;
import com.openmrs.android_sdk.library.api.workers.provider.UpdateProviderWorker;
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;

/**
 * The type Provider repository.
 */
@Singleton
public class ProviderRepository extends BaseRepository {

    ProviderRoomDAO providerRoomDao;

    /**
     * Instantiates a new Provider repository.
     */
    @Inject
    public ProviderRepository() {
        providerRoomDao = db.providerRoomDAO();
    }

    /**
     * Instantiates a new Provider repository.
     *
     * @param restApi the rest api
     * @param logger  the logger
     */
    public ProviderRepository(RestApi restApi, OpenMRSLogger logger) {
        super(restApi, logger);
    }

    /**
     * Sets provider room dao.
     *
     * @param providerRoomDao the provider room dao
     */
    public void setProviderRoomDao(ProviderRoomDAO providerRoomDao) {
        this.providerRoomDao = providerRoomDao;
    }

    /**
     * Gets providers.
     *
     * @return a list of providers
     */
    public Observable<List<Provider>> getProviders() {
        return createObservableIO(() -> {
            // If not online, fetch providers locally
            if (!NetworkUtils.isOnline()) {
                logger.e("Offline providers fetched, couldn't sync with the database while offline");
                return providerRoomDao.getProviderList().blockingGet();
            }
            providerRoomDao.deleteAll();
            // Otherwise (online), fetch remote providers
            Response<Results<Provider>> response = restApi.getProviderList().execute();
            if (response.isSuccessful()) {
                List<Provider> serverList = response.body().getResults();
                if (!serverList.isEmpty()) {
                    // Sync local DB with server's providers
                    List<String> providerUuids = providerRoomDao.getCurrentUUIDs().blockingGet();
                    HashSet<String> checkUuids = new HashSet<>(providerUuids);

                    for (Provider provider : serverList) {
                        if (!checkUuids.contains(provider.getUuid())) {
                            providerRoomDao.addProvider(provider);
                        }
                        checkUuids.remove(provider.getUuid());
                    }
                    // Remove local providers that are not present in server now
                    for (String uuid : checkUuids) {
                        providerRoomDao.deleteByUuid(uuid);
                    }
                }
            } else {
                logger.e("Error fetching providers from the server: " + response.errorBody().string());
            }

            return providerRoomDao.getProviderList().blockingGet();
        });
    }

    /**
     * Add provider.
     *
     * @param provider the provider
     * @param callback the callback
     */
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

    /**
     * Update provider.
     *
     * @param provider the provider
     * @param callback the callback
     */
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

    /**
     * Delete providers.
     *
     * @param providerUuid the provider uuid
     * @param callback     the callback
     */
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

    /**
     * Gets location.
     *
     * @param url the url
     * @return a list of location entities
     */
    public Observable<List<LocationEntity>> getLocations(String url) {
        return createObservableIO(() -> {
            String locationEndPoint = url + REST_ENDPOINT + "location";
            Response<Results<LocationEntity>> response =
                    restApi.getLocations(locationEndPoint, TAG_ADMISSION_LOCATION, FULL).execute();
            if (response.isSuccessful()) return response.body().getResults();
            else throw new Exception("fetch provider location error: " + response.message());
        });
    }

    /**
     * Gets encounter roles.
     *
     * @return a list of resources of encounter roles
     */
    public Observable<List<Resource>> getEncounterRoles() {
        return createObservableIO(() -> {
            Response<Results<Resource>> response = restApi.getEncounterRoles().execute();
            if (response.isSuccessful()) return response.body().getResults();
            else throw new Exception("fetch encounter roles error: " + response.message());
        });
    }
}
