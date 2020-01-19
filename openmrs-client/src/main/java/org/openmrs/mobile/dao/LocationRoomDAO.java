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

package org.openmrs.mobile.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;
import org.openmrs.mobile.databases.entities.LocationEntity;

@Dao
public interface LocationRoomDAO {

    @Insert
    Single<Long> saveLocation(LocationEntity entity);

    @Query("DELETE FROM locations")
    void deleteAllLocations();

    @Query("SELECT * FROM locations")
    Single<List<LocationEntity>> getLocations();

    @Query("SELECT * FROM locations WHERE name = :mName")
    Single<LocationEntity> findLocationByName(String mName);
}
