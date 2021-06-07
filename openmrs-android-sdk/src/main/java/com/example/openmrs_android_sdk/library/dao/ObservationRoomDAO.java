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
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.databases.entities.ObservationEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ObservationRoomDAO {
    @Insert
    void addObservation(ObservationEntity observationEntity);

    @Update
    void updateObservation(ObservationEntity observationEntity);

    @Delete
    void deleteObservation(ObservationEntity observationEntity);

    @Query("SELECT * FROM observations WHERE encounter_id = :encounterID")
    Single<List<ObservationEntity>> findObservationByEncounterID(long encounterID);

    @Query("SELECT * FROM observations WHERE uuid = :observationUUID")
    Single<ObservationEntity> getObservationByUUID(String observationUUID);

    @Query("SELECT * FROM observations")
    Single<List<ObservationEntity>> getAllObservations();

}
