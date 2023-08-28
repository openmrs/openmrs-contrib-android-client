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
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * The type Location dao.
 */
@Singleton
class LocationDAO @Inject constructor() {
    /**
     * The Location room dao.
     */
    var locationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).locationRoomDAO()

    /**
     * Save location observable.
     *
     * @param location the location
     * @return the observable
     */
    fun saveLocation(location: LocationEntity): Observable<Long> {
        return createObservableIO(Callable { locationRoomDAO.addLocation(location) })
    }

    /**
     * Delete all locations.
     * @return boolean true if successful
     */
    fun deleteAllLocations(): Observable<Boolean> {
        return createObservableIO(Callable {
            locationRoomDAO.deleteAllLocations()
            true
        })
    }

    /**
     * Gets locations.
     *
     * @return the locations
     */
    fun getLocations(): Observable<List<LocationEntity>>{
        return createObservableIO(Callable<List<LocationEntity>> {
            try {
                return@Callable locationRoomDAO.getLocations().blockingGet()
            } catch (e: java.lang.Exception) {
                return@Callable ArrayList<LocationEntity>()
            }
        })
    }


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