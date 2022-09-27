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
package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.utilities.ApplicationConstants
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

/**
 * The type Location repository.
 */
@Singleton
class LocationRepository @Inject constructor() : BaseRepository() {
    /**
     * Gets location (only has uuid).
     *
     * @return the location LocationEntity
     */
    val location: LocationEntity?
        get() {
            val response = restApi.getLocations(null).execute()
            if (response.isSuccessful) {
                for (result in response.body()!!.results) {
                    if (result.display?.trim().equals(OpenmrsAndroid.getLocation().trim(), ignoreCase = true)) {
                        return result
                    }
                }
            }
            return null
        }

    /**
     * Fetches all locations registered in a server.
     *
     * @param url the URL of the server to fetch the locations from
     * @return observable list of LocationEntity
     */
    fun getLocations(url: String): Observable<List<LocationEntity>> {
        return createObservableIO(Callable {
            val locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location"
            restApi.getLocations(locationEndPoint, "Login Location", "full").execute().run {
                if (isSuccessful && body() != null) return@Callable body()!!.results
                else throw Exception("Error fetching concepts: ${message()}")
            }
        })
    }
}
