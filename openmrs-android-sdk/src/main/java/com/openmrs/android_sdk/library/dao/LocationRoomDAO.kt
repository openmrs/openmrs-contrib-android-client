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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import io.reactivex.Single

/**
 * The interface Location room dao.
 */
@Dao
interface LocationRoomDAO {
    /**
     * Add location long.
     *
     * @param entity the entity
     * @return the long
     */
    @Insert
    fun addLocation(entity: LocationEntity): Long

    /**
     * Delete all locations.
     */
    @Query("DELETE FROM locations")
    fun deleteAllLocations()

    /**
     * Gets locations.
     *
     * @return the locations
     */
    @Query("SELECT * FROM locations")
    fun getLocations(): Single<List<LocationEntity>>

    /**
     * Find location by name single.
     *
     * @param mName the m name
     * @return the single
     */
    @Query("SELECT * FROM locations WHERE display = :mName")
    fun findLocationByName(mName: String): Single<LocationEntity>

    /**
     * Find location by uuid single.
     *
     * @param uuid the uuid
     * @return the single
     */
    @Query("SELECT * FROM locations WHERE uuid = :uuid")
    fun findLocationByUUID(uuid: String): Single<LocationEntity>
}