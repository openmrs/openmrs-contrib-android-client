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
import retrofit2.Response;
import rx.Observable;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.library.api.workers.provider.AddProviderWorker;
import com.openmrs.android_sdk.library.api.workers.provider.DeleteProviderWorker;
import com.openmrs.android_sdk.library.api.workers.provider.UpdateProviderWorker;
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.library.models.ResultType;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.NetworkUtils;

/**
 * The type Provider repository.
 */
@Singleton
public class ProviderRepository extends BaseRepository {

    private final ProviderRoomDAO providerRoomDao;

    /**
     * Instantiates a new Provider repository.
     */
    @Inject
    public ProviderRepository(ProviderRoomDAO providerRoomDao) {
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
                getLogger().e("Offline providers fetched, couldn't sync with the database while offline");
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
                getLogger().e("Error fetching providers from the server: " + response.errorBody().string());
            }

            return providerRoomDao.getProviderList().blockingGet();
        });
    }

    /**
     * Add new provider to the database.
     *
     * @param provider the provider to be added
     * @return Observable ResultType of the operation being locally success, all success, or fail
     */
    public Observable<ResultType> addProvider(Provider provider) {
        return createObservableIO(() -> {
            if (!NetworkUtils.isOnline()) {
                // If not online, add provider locally.
                long providerId = providerRoomDao.addProvider(provider);

                // Delegate to the WorkManager.
                Data data = new Data.Builder()
                        .putLong("id", providerId)
                        .build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                getWorkManager().enqueue(new OneTimeWorkRequest.Builder(AddProviderWorker.class)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()
                );

                getLogger().i("Provider will be synced to the server when device gets connected to network");
                return ResultType.AddProviderLocalSuccess;
            }

            // Otherwise (online), add provider remotely.
            Response<Provider> response = restApi.addProvider(provider).execute();
            if (response.isSuccessful()) {
                // Add provider to the database.
                providerRoomDao.addProvider(response.body());
                getLogger().i("Adding provider succeeded " + response.raw());
                return ResultType.AddProviderSuccess;
            } else {
                getLogger().e("Failed to add provider. Error:  " + response.message());
                throw new Exception("Failed to add provider. Error:  " + response.message());
            }
        });
    }

    /**
     * Update existing provider in the database.
     *
     * @param provider the provider
     * @return Observable ResultType of the operation being locally success, all success, or fail
     */
    public Observable<ResultType> updateProvider(Provider provider) {
        return createObservableIO(() -> {
            if (!NetworkUtils.isOnline()) {
                // If not online, update provider locally.
                providerRoomDao.updateProviderByUuid(provider.getDisplay(), provider.getId(),
                        provider.getPerson(), provider.getUuid(), provider.getIdentifier());

                // Delegate to the WorkManager.
                Data data = new Data.Builder()
                        .putString("uuid", provider.getUuid())
                        .build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                getWorkManager().enqueue(new OneTimeWorkRequest.Builder(UpdateProviderWorker.class)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()
                );

                getLogger().i("Updated provider will be synced to the server when device gets connected to network");
                return ResultType.UpdateProviderLocalSuccess;
            }

            // Otherwise (online), update provider remotely.
            Response<Provider> response = restApi.updateProvider(provider.getUuid(), provider).execute();
            if (response.isSuccessful()) {
                // Update provider in the database.
                providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(),
                        response.body().getPerson(), response.body().getUuid(),
                        response.body().getIdentifier());
                getLogger().i("Updating provider succeeded " + response.raw());
                return ResultType.UpdateProviderSuccess;
            } else {
                getLogger().e("Failed to update provider. Error:  " + response.message());
                throw new Exception("Failed to update provider. Error:  " + response.message());
            }
        });
    }

    /**
     * Delete an existing provider from the database.
     *
     * @param providerUuid the UUID of the provider to be deleted
     * @return Observable ResultType of the operation being locally success, all success, or fail
     */
    public Observable<ResultType> deleteProviders(String providerUuid) {
        return createObservableIO(() -> {
            if (!NetworkUtils.isOnline()) {
                // If not online, delete provider locally.
                providerRoomDao.deleteByUuid(providerUuid);

                // Delegate to the WorkManager.
                Data data = new Data.Builder()
                        .putString("uuid", providerUuid)
                        .build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                getWorkManager().enqueue(new OneTimeWorkRequest.Builder(DeleteProviderWorker.class)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()
                );

                getLogger().i("Provider will be removed from the server when you're back online");
                return ResultType.ProviderDeletionLocalSuccess;
            }

            // Otherwise (online), delete provider remotely.
            Response<ResponseBody> response = restApi.deleteProvider(providerUuid).execute();
            if (response.isSuccessful()) {
                // Delete provider from the database.
                providerRoomDao.deleteByUuid(providerUuid);
                getLogger().i("Deleting Provider Successful " + response.raw());
                return ResultType.ProviderDeletionSuccess;
            } else {
                getLogger().e("Failed to delete provider. Error: " + response.message());
                throw new Exception("Failed to delete provider. Error: " + response.message());
            }
        });
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
