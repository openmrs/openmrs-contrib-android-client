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
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.databases.entities.EncounterEntity
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.EncounterType
import rx.Observable
import java.util.*
import java.util.concurrent.Callable

/**
 * The type Encounter dao.
 */
class EncounterDAO {
    private var encounterRoomDAO: EncounterRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).encounterRoomDAO()
    private var observationRoomDAO: ObservationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).observationRoomDAO()
    private var encounterTypeRoomDAO: EncounterTypeRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).encounterTypeRoomDAO()

    /**
     * Save encounter long.
     *
     * @param encounter the encounter
     * @param visitID   the visit id
     * @return the long
     */
    fun saveEncounter(encounter: Encounter, visitID: Long?): Long {
        val encounterEntity: EncounterEntity = convert(encounter, visitID)
        return encounterRoomDAO.addEncounter(encounterEntity)
    }

    /**
     * Gets encounter type by form name.
     *
     * @param formname the formname
     * @return the encounter type by form name
     */
    fun getEncounterTypeByFormName(formname: String): EncounterType {
        return encounterTypeRoomDAO.getEncounterTypeByFormName(formname)
    }

    /**
     * Save last vitals encounter.
     *
     * @param encounter   the encounter
     * @param patientUUID the patient uuid
     */
    fun saveLastVitalsEncounter(encounter: Encounter?, patientUUID: String) {
        if (null != encounter) {
            encounter.patientUUID = patientUUID
            val oldLastVitalsEncounterID: Long = try {
                encounterRoomDAO.getLastVitalsEncounterID(patientUUID).blockingGet()
            } catch (e: Exception) {
                0
            }
            if (0L != oldLastVitalsEncounterID) {
                for (obs in ObservationDAO().findObservationByEncounterID(oldLastVitalsEncounterID)) {
                    val observationEntity = convert(obs, 1L)
                    observationRoomDAO.deleteObservation(observationEntity)
                }
                encounterRoomDAO.deleteEncounterByID(oldLastVitalsEncounterID)
            }
            val encounterID = saveEncounter(encounter, null)
            for (obs in encounter.observations) {
                val observationEntity = convert(obs, encounterID)
                observationRoomDAO.addObservation(observationEntity)
            }
        }
    }

    /**
     * Gets last vitals encounter.
     *
     * @param patientUUID the patient uuid
     * @return the last vitals encounter
     */
    fun getLastVitalsEncounter(patientUUID: String): Observable<Encounter> {
        return createObservableIO(Callable<Encounter> {
            try {
                val encounterEntity =
                    encounterRoomDAO.getLastVitalsEncounter(patientUUID, EncounterType.VITALS)
                        .blockingGet()
                convert(encounterEntity)
            } catch (e: Exception) {
                null
            }
        })
    }

    /**
     * Update encounter int.
     *
     * @param encounterID the encounter id
     * @param encounter   the encounter
     * @param visitID     the visit id
     * @return the int
     */
    fun updateEncounter(encounterID: Long, encounter: Encounter, visitID: Long): Int {
        val encounterEntity = convert(encounter, visitID)
        encounterEntity.id = encounterID
        return encounterRoomDAO.updateEncounter(encounterEntity)
    }

    /**
     * Find encounters by visit id list.
     *
     * @param visitID the visit id
     * @return the list
     */
    fun findEncountersByVisitID(visitID: Long?): List<Encounter> {
        val encounters: MutableList<Encounter> = ArrayList()
        return try {
            val encounterEntities =
                encounterRoomDAO.findEncountersByVisitID(visitID.toString()).blockingGet()
            for (entity in encounterEntities) {
                encounters.add(convert(entity))
            }
            encounters
        } catch (e: Exception) {
            encounters
        }
    }

    /**
     * Gets all encounters by type.
     *
     * @param patientID the patient id
     * @param type      the type
     * @return the all encounters by type
     */
    fun getAllEncountersByType(patientID: Long, type: EncounterType): Observable<List<Encounter>> {
        return createObservableIO(Callable<List<Encounter>> {
            val encounters: MutableList<Encounter> = ArrayList()
            val encounterEntities: List<EncounterEntity>
            try {
                encounterEntities =
                    encounterRoomDAO.getAllEncountersByType(patientID, type.display)
                        .blockingGet()
                for (entity in encounterEntities) {
                    encounters.add(convert(entity))
                }
                encounters
            } catch (e: Exception) {
                ArrayList()
            }
        })
    }

    /**
     * Gets encounter by uuid.
     *
     * @param encounterUUID the encounter uuid
     * @return the encounter by uuid
     */
    fun getEncounterByUUID(encounterUUID: String): Long {
        return try {
            encounterRoomDAO.getEncounterByUUID(encounterUUID).blockingGet()
        } catch (e: Exception) {
            0
        }
    }
}