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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.openmrs.android_sdk.library.databases.entities.ObservationEntity
import io.reactivex.Single

/**
 * The interface Observation room dao.
 */
@Dao
interface ObservationRoomDAO {
    /**
     * Add observation.
     *
     * @param observationEntity the observation entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addObservation(observationEntity: ObservationEntity)

    /**
     * Update observation.
     *
     * @param observationEntity the observation entity
     */
    @Update
    fun updateObservation(observationEntity: ObservationEntity)

    /**
     * Delete observation.
     *
     * @param observationEntity the observation entity
     */
    @Delete
    fun deleteObservation(observationEntity: ObservationEntity)

    /**
     * Find observation by encounter id single.
     *
     * @param encounterID the encounter id
     * @return the single
     */
    @Query("SELECT * FROM observations WHERE encounter_id = :encounterID")
    fun findObservationByEncounterID(encounterID: Long): Single<List<ObservationEntity?>?>?

    /**
     * Gets observation by uuid.
     *
     * @param observationUUID the observation uuid
     * @return the observation by uuid
     */
    @Query("SELECT * FROM observations WHERE uuid = :observationUUID")
    fun getObservationByUUID(observationUUID: String): Single<ObservationEntity?>?

    /**
     * Gets observation by patient uuid
     *
     * @param patient_uuid the observation uuid
     * @return the observation
     */
    @Query("SELECT * FROM observations WHERE patient_uuid = :patient_uuid")
    fun getObservationForPatientByPatientUuid(patient_uuid: String): Single<ObservationEntity?>?

    /**
     * Gets observation by patient uuid and concept uuid.
     *
     * @param patient_uuid the observation uuid
     * @param concept_uuid the observation uuid
     * @return the observation
     */
    @Query("SELECT * FROM observations WHERE patient_uuid = :patient_uuid AND conceptUuid = :concept_uuid")
    fun getObservationForPatientByConceptUuid(patient_uuid: String, concept_uuid: String): Single<ObservationEntity?>?

    /**
     * Gets all observations.
     *
     * @return the all observations
     */
    @get:Query("SELECT * FROM observations")
    val allObservations: Single<List<ObservationEntity?>?>?
}