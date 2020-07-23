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

import androidx.annotation.NonNull;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository extends RetrofitRepository {
    public SimplePromise<LocationEntity> getLocationUuid() {
        final SimpleDeferredObject<LocationEntity> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
            RestServiceBuilder.createService(RestApi.class);
        Call<Results<LocationEntity>> call = apiService.getLocations(null);
        call.enqueue(new Callback<Results<LocationEntity>>() {
            @Override
            public void onResponse(@NonNull Call<Results<LocationEntity>> call, @NonNull Response<Results<LocationEntity>> response) {
                Results<LocationEntity> locationList = response.body();
                for (LocationEntity result : locationList.getResults()) {
                    if ((result.getDisplay().trim()).equalsIgnoreCase((openMrs.getLocation().trim()))) {
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
