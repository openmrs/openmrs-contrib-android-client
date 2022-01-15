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

package com.openmrs.android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * The interface Observation room dao.
 */
@Dao
public interface ObservationRoomDAO {
    /**
     * Add observation.
     *
     * @param observationEntity the observation entity
     */
    @Insert
    void addObservation(ObservationEntity observationEntity);

    /**
     * Update observation.
     *
     * @param observationEntity the observation entity
     */
    @Update
    void updateObservation(ObservationEntity observationEntity);

    /**
     * Delete observation.
     *
     * @param observationEntity the observation entity
     */
    @Delete
    void deleteObservation(ObservationEntity observationEntity);

    /**
     * Find observation by encounter id single.
     *
     * @param encounterID the encounter id
     * @return the single
     */
    @Query("SELECT * FROM observations WHERE encounter_id = :encounterID")
    Single<List<ObservationEntity>> findObservationByEncounterID(long encounterID);

    /**
     * Gets observation by uuid.
     *
     * @param observationUUID the observation uuid
     * @return the observation by uuid
     */
    @Query("SELECT * FROM observations WHERE uuid = :observationUUID")
    Single<ObservationEntity> getObservationByUUID(String observationUUID);

    /**
     * Gets all observations.
     *
     * @return the all observations
     */
    @Query("SELECT * FROM observations")
    Single<List<ObservationEntity>> getAllObservations();

}
