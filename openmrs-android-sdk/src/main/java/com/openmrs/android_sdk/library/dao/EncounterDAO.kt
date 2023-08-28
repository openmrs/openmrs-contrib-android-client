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
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.databases.entities.EncounterEntity
import com.openmrs.android_sdk.library.databases.entities.StandaloneEncounterEntity
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.EncounterType
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The type Encounter dao.
 */
@Singleton
class EncounterDAO @Inject constructor() {

    var observationDAO: ObservationDAO? = null
        @Inject set

    var encounterRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).encounterRoomDAO()
    var observationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).observationRoomDAO()
    var encounterTypeRoomDAO = AppDatabase.getDatabase(
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
     * Save encounter independently.
     *
     * @param encounter the encounter
     * @return the long
     */
    fun saveStandaloneEncounter(encounter: Encounter): Long {
        val standaloneEncounterEntity =
            convertToStandalone(encounter)
        return encounterRoomDAO.addEncounterStandaloneEntity(standaloneEncounterEntity)
    }

    /**
     * Save encounters independently.
     *
     * @param encounterList the encounter
     * @return the long
     */
    fun saveStandaloneEncounters(encounterList: List<Encounter?>): List<Long> {
        val standaloneEncounterEntityList: MutableList<StandaloneEncounterEntity> = ArrayList()
        for (encounter in encounterList) {
            val standaloneEncounterEntity = convertToStandalone(encounter!!)
            standaloneEncounterEntityList.add(standaloneEncounterEntity)
        }
        return encounterRoomDAO.addEncounterStandaloneEntityList(standaloneEncounterEntityList)
    }

    /**
     * Delete Standalone Encounters
     *
     * @param patientUuid the patient uuid for which the standalone encounters should be deleted
     * @return the long
     */
    fun deleteAllStandaloneEncounters(patientUuid: String): Observable<Boolean> {
        return createObservableIO(Callable {
            encounterRoomDAO.deleteStandaloneEncounter(patientUuid)
            true
        })
    }

    /**
     * Gets encounter type by form name.
     *
     * @param formName the form name
     * @return the encounter type by form name
     */
    fun getEncounterTypeByFormName(formName: String): EncounterType {
        return encounterTypeRoomDAO.getEncounterTypeByFormName(formName)
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
            val oldLastVitalsEncounterID: Long
            oldLastVitalsEncounterID = try {
                encounterRoomDAO.getLastVitalsEncounterID(patientUUID).blockingGet()
            } catch (e: Exception) {
                0
            }
            if (0L != oldLastVitalsEncounterID) {
                for (obs in ObservationDAO().findObservationByEncounterID(oldLastVitalsEncounterID)) {
                    val observationEntity = convert(obs!!, 1L)
                    observationRoomDAO.deleteObservation(observationEntity)
                }
                encounterRoomDAO.deleteEncounterByID(oldLastVitalsEncounterID)
            }
            val encounterID = saveEncounter(encounter, null)
            for (obs in encounter.observations) {
                observationDAO?.saveObservation(obs, encounterID)
            }
        }
    }

    /**
     * Gets last vitals encounter.
     *
     * @param patientUUID the patient uuid
     * @return the last vitals encounter
     */
    fun getLastVitalsEncounter(patientUUID: String?): Observable<Encounter> {
        return createObservableIO(Callable {
            val encounterEntity = encounterRoomDAO.getLastVitalsEncounter(patientUUID!!, EncounterType.VITALS).blockingGet()
            convert(encounterEntity)
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
    fun updateEncounter(
        encounterID: Long,
        encounter: Encounter?,
        visitID: Long
    ): Int {
        val encounterEntity = convert(encounter!!, visitID)
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
            val encounterEntities = encounterRoomDAO.findEncountersByVisitID(visitID.toString()).blockingGet()
            for (entity in encounterEntities) {
                encounters.add(convert(entity!!))
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
    fun getAllEncountersByType(patientID: Long?, type: EncounterType?): Observable<List<Encounter>> {
        return createObservableIO<List<Encounter>>(Callable<List<Encounter>> {
            val encounters: MutableList<Encounter> = ArrayList()
            val encounterEntities: List<EncounterEntity>
            try {
                encounterEntities =
                    encounterRoomDAO.getAllEncountersByType(patientID!!, type?.display!!).blockingGet()
                for (entity in encounterEntities) {
                    encounters.add(convert(entity!!))
                }
                return@Callable encounters
            } catch (e: Exception) {
                return@Callable ArrayList<Encounter>()
            }
        })
    }

    /**
     * Gets encounter by uuid.
     *
     * @param encounterUUID the encounter uuid
     * @return the encounter by uuid
     */
    fun getEncounterByUUID(encounterUUID: String?): Long {
        return try {
            encounterRoomDAO.getEncounterByUUID(encounterUUID!!).blockingGet()
        } catch (e: Exception) {
            0
        }
    }
}