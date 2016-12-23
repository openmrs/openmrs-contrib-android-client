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

package org.openmrs.mobile.api.retrofit;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationApi extends RetrofitApi{


    public SimplePromise<Location> getLocationUuid() {
        final SimpleDeferredObject<Location> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
                RestServiceBuilder.createService(RestApi.class);
        Call<Results<Location>> call = apiService.getLocations(null);
        call.enqueue(new Callback<Results<Location>>() {
            @Override
            public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                Results<Location> locationList = response.body();
                for (Location result : locationList.getResults()) {
                    if ((result.getDisplay().trim()).equalsIgnoreCase((openMrs.getLocation().trim()))) {
                        deferred.resolve(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<Location>> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });

        return deferred.promise();
    }
}
