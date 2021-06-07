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

package com.example.openmrs_android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface LocationRoomDAO {
    @Insert
    long addLocation(LocationEntity entity);

    @Query("DELETE FROM locations")
    void deleteAllLocations();

    @Query("SELECT * FROM locations")
    Single<List<LocationEntity>> getLocations();

    @Query("SELECT * FROM locations WHERE display = :mName")
    Single<LocationEntity> findLocationByName(String mName);

    @Query("SELECT * FROM locations WHERE uuid = :uuid")
    Single<LocationEntity> findLocationByUUID(String uuid);
}
