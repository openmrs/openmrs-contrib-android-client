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
package com.openmrs.android_sdk.library.dao

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.utilities.StringUtils.notNull
import rx.Observable
import java.util.*
import java.util.concurrent.Callable

/**
 * The type Location dao.
 */
class LocationDAO {
    /**
     * The Location room dao.
     */
    private var locationRoomDAO: LocationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).locationRoomDAO()

    /**
     * Save location observable.
     *
     * @param location the location
     * @return the observable
     */
    fun saveLocation(location: LocationEntity?): Observable<Long> {
        return createObservableIO(Callable {
            locationRoomDAO.addLocation(location!!)
        })
    }

    /**
     * Delete all locations.
     */
    fun deleteAllLocations() {
        locationRoomDAO.deleteAllLocations()
    }

    /**
     * Gets locations.
     *
     * @return the locations
     */
    val locations: Observable<List<LocationEntity>>
        get() = createObservableIO(Callable<List<LocationEntity>> {
            try {
                locationRoomDAO.getLocations().blockingGet()
            } catch (e: Exception) {
                ArrayList()
            }
        })

    /**
     * Find location by name location entity.
     *
     * @param name the name
     * @return the location entity
     */
    fun findLocationByName(name: String?): LocationEntity? {
        return if (!notNull(name)) {
            null
        } else try {
            locationRoomDAO.findLocationByName(name!!).blockingGet()
        } catch (e: Exception) {
            LocationEntity(name!!)
        }
    }

    /**
     * Find location by uuid location entity.
     *
     * @param uuid the uuid
     * @return the location entity
     */
    fun findLocationByUUID(uuid: String?): LocationEntity? {
        return if (!notNull(uuid)) {
            null
        } else try {
            locationRoomDAO.findLocationByUUID(uuid!!).blockingGet()
        } catch (e: Exception) {
            LocationEntity("")
        }
    }
}