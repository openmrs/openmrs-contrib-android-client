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
import com.openmrs.android_sdk.library.databases.entities.VisitEntity
import com.openmrs.android_sdk.library.models.Visit
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The type Visit dao.
 */
@Singleton
class VisitDAO @Inject constructor() {
    /**
     * The Context.
     */
    var context = OpenmrsAndroid.getInstance()!!.applicationContext

    /**
     * The Observation room dao.
     */
    var observationRoomDAO = AppDatabase.getDatabase(context).observationRoomDAO()

    /**
     * The Visit room dao.
     */
    var visitRoomDAO = AppDatabase.getDatabase(context).visitRoomDAO()

    /**
     * Save or update observable.
     *
     * @param visit     the visit
     * @param patientId the patient id
     * @return the observable
     */
    fun saveOrUpdate(visit: Visit, patientId: Long): Observable<Long?> {
        return createObservableIO(Callable {
            var visitId = visit.id
            if (visitId == null) {
                visitId = getVisitsIDByUUID(visit.uuid!!).toBlocking().first()
            }
            if (visitId!! > 0) {
                updateVisit(visit, visitId, patientId)
            } else {
                visitId = saveVisit(visit, patientId)
            }
            visitId
        })
    }

    private fun saveVisit(visit: Visit, patientID: Long): Long {
        val encounterDAO = EncounterDAO()
        visit.patient = PatientDAO().findPatientByID(patientID.toString())
        val visitEntity = convert(visit)
        val visitID = visitRoomDAO.addVisit(visitEntity)
        if (visit.encounters != null) {
            for (encounter in visit.encounters) {
                val encounterID = encounterDAO.saveEncounter(encounter, visitID)
                for (obs in encounter.observations) {
                    val observationEntity = convert(obs, encounterID)
                    observationRoomDAO.addObservation(observationEntity)
                }
            }
        }
        return visitID
    }

    private fun updateVisit(visit: Visit, visitID: Long, patientID: Long): Boolean {
        val encounterDAO = EncounterDAO()
        val observationDAO = ObservationDAO()
        visit.patient = PatientDAO().findPatientByID(patientID.toString())
        if (visit.encounters != null) {
            for (encounter in visit.encounters) {
                var encounterID = encounterDAO.getEncounterByUUID(encounter.uuid)
                if (encounterID > 0) {
                    encounterDAO.updateEncounter(encounterID, encounter, visitID)
                } else {
                    encounterID = encounterDAO.saveEncounter(encounter, visitID)
                }
                val oldObs = observationDAO.findObservationByEncounterID(encounterID)
                for (obs in oldObs) {
                    val observationEntity = convert(obs, encounterID)
                    observationRoomDAO.deleteObservation(observationEntity)
                }
                for (obs in encounter.observations) {
                    val observationEntity = convert(obs, encounterID)
                    observationRoomDAO.addObservation(observationEntity)
                }
            }
        }
        return visitRoomDAO.updateVisit(convert(visit)) > 0
    }

    /**
     * Gets active visits.
     *
     * @return the active visits
     */
    fun getActiveVisits(): Observable<List<Visit>> {
        return createObservableIO<List<Visit>>(Callable<List<Visit>> {
            val visits: MutableList<Visit> = ArrayList()
            val visitEntities: List<VisitEntity>
            try {
                visitEntities = visitRoomDAO.activeVisits.blockingGet()
                for (entity in visitEntities) {
                    visits.add(convert(entity))
                }
                return@Callable visits
            } catch (e: Exception) {
                return@Callable ArrayList<Visit>()
            }
        })
    }

    /**
     * Gets visits by patient id.
     *
     * @param patientID the patient id
     * @return the visits by patient id
     */
    fun getVisitsByPatientID(patientID: Long?): Observable<List<Visit>> {
        return createObservableIO<List<Visit>>(Callable<List<Visit>> {
            val visits: MutableList<Visit> = ArrayList()
            val visitEntities: List<VisitEntity>
            try {
                visitEntities = visitRoomDAO.getVisitsByPatientID(patientID!!).blockingGet()
                for (entity in visitEntities) {
                    visits.add(convert(entity))
                }
                return@Callable visits
            } catch (e: Exception) {
                return@Callable visits
            }
        })
    }

    /**
     * Gets active visit by patient id.
     *
     * @param patientId the patient id
     * @return the active visit by patient id
     */
    fun getActiveVisitByPatientId(patientId: Long): Observable<Visit> {
        return createObservableIO<Visit>(Callable<Visit> {
            try {
                val visitEntity = visitRoomDAO.getActiveVisitByPatientId(patientId)!!.blockingGet()
                return@Callable convert(visitEntity)
            } catch (e: Exception) {
                return@Callable null
            }
        })
    }

    /**
     * Gets visit by id.
     *
     * @param visitID the visit id
     * @return the visit by id
     */
    fun getVisitByID(visitID: Long): Observable<Visit> {
        return createObservableIO<Visit>(Callable<Visit> {
            try {
                val visitEntity = visitRoomDAO.getVisitByID(visitID)!!.blockingGet()
                return@Callable convert(visitEntity)
            } catch (e: Exception) {
                return@Callable null
            }
        })
    }

    /**
     * Gets visits id by uuid.
     *
     * @param visitUUID the visit uuid
     * @return the visits id by uuid
     */
    fun getVisitsIDByUUID(visitUUID: String): Observable<Long> {
        return createObservableIO(Callable {
            visitRoomDAO.getVisitsIDByUUID(visitUUID)
        })
    }

    /**
     * Gets visit by uuid.
     *
     * @param uuid the uuid
     * @return the visit by uuid
     */
    fun getVisitByUuid(uuid: String?): Observable<Visit> {
        return createObservableIO<Visit>(Callable<Visit> {
            try {
                val visitEntity = visitRoomDAO.getVisitByUuid(uuid!!)!!.blockingGet()
                return@Callable convert(visitEntity)
            } catch (e: Exception) {
                return@Callable null
            }
        })
    }

    /**
     * Delete visits by patient id observable.
     *
     * @param id the id
     * @return the observable
     */
    fun deleteVisitsByPatientId(id: Long): Observable<Boolean> {
        return createObservableIO(Callable {
            visitRoomDAO.deleteVisitsByPatientId(id)
            true
        })
    }
}