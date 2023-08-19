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
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.convert
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.convertToStandalone
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity
import com.openmrs.android_sdk.library.databases.entities.StandaloneObservationEntity
import com.openmrs.android_sdk.library.models.Observation
import javax.inject.Inject

/**
 * The type Observation dao.
 */
class ObservationDAO @Inject constructor() {
    /**
     * The Observation room dao.
     */
    var observationRoomDAO = AppDatabase.getDatabase(
            OpenmrsAndroid.getInstance()!!.applicationContext
    ).observationRoomDAO()

    /**
     * Saves an observation entity to db
     *
     * @param obs the observation model
     * @param encounterID the encounterId
     *
     * @return id (primary key)
     */
    fun saveObservation(obs: Observation, encounterID: Long): Long {
        val observationEntity = convert(obs, encounterID)
        return observationRoomDAO.addObservation(observationEntity)
    }

    /**
     * Update observation in db (ROOM will match the primary keys for updating)
     *
     * @param obs the observation
     * @param encounterId the observation (needed to verify that the observation already exists)
     *
     * @return count of updated values
     */
    fun updateObservation(obs: Observation, encounterId: Long): Int {
        val observationEntity = convert(obs, encounterId)
        observationEntity.id = encounterId
        return observationRoomDAO.updateObservation(observationEntity)
    }

    /**
     * Find observation by encounter id list.
     *
     * @param encounterID the encounter id
     * @return the list
     */
    fun findObservationByEncounterID(encounterID: Long): List<Observation> {
        val observationList: List<Observation>
        val observationEntityList: List<ObservationEntity>
        return try {
            observationEntityList = observationRoomDAO.findObservationByEncounterID(encounterID)
                    .blockingGet()
            observationList = convert(observationEntityList)
            observationList
        } catch (e: Exception) {
            ArrayList<Observation>()
        }
    }

    /**
     * Save observations independently to the offline database
     *
     * @param observationList the observation list to be saved
     * @return the list of primary keys
     */
    fun saveStandaloneObservations(observationList: List<Observation>): List<Long> {
        val standaloneObservationEntityList: MutableList<StandaloneObservationEntity> = ArrayList()
        for (observation in observationList) {
            val standaloneObservationEntity = convertToStandalone(observation)
            standaloneObservationEntityList.add(standaloneObservationEntity)
        }
        return observationRoomDAO.addStandaloneObservationList(standaloneObservationEntityList)
    }

    /**
     * Delete all standalone observations in the database
     *
     * @param patientUuid the patient uuid for which the standalone encounters should be deleted
     */
    fun deleteAllStandaloneObservations(patientUuid: String) {
        observationRoomDAO.deleteAllStandaloneObservationsByPatientUuid(patientUuid)
    }
}