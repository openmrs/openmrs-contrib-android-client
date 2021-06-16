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

import androidx.annotation.NonNull;

import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;
import com.example.openmrs_android_sdk.library.models.Results;
import com.example.openmrs_android_sdk.utilities.ToastUtil;

import com.example.openmrs_android_sdk.library.api.promise.SimpleDeferredObject;
import com.example.openmrs_android_sdk.library.api.promise.SimplePromise;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository extends BaseRepository {
    public SimplePromise<LocationEntity> getLocationUuid() {
        final SimpleDeferredObject<LocationEntity> deferred = new SimpleDeferredObject<>();

        Call<Results<LocationEntity>> call = restApi.getLocations(null);
        call.enqueue(new Callback<Results<LocationEntity>>() {
            @Override
            public void onResponse(@NonNull Call<Results<LocationEntity>> call, @NonNull Response<Results<LocationEntity>> response) {
                Results<LocationEntity> locationList = response.body();
                for (LocationEntity result : locationList.getResults()) {
                    if ((result.getDisplay().trim()).equalsIgnoreCase((OpenmrsAndroid.getLocation().trim()))) {
                        deferred.resolve(result);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<LocationEntity>> call, @NonNull Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }
        });

        return deferred.promise();
    }
}
