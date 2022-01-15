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

import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.convert
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.dao.PatientRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.databases.entities.PatientEntity
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import rx.Observable
import java.lang.Exception
import java.util.*
import java.util.concurrent.Callable

/**
 * The type Patient dao.
 */
class PatientDAO {
    /**
     * The Patient room dao.
     */
    private var patientRoomDAO: PatientRoomDAO =
        AppDatabase.getDatabase(OpenmrsAndroid.getInstance()!!.applicationContext).patientRoomDAO()

    /**
     * Save patient observable.
     *
     * @param patient the patient
     * @return the observable
     */
    fun savePatient(patient: Patient?): Observable<Long> {
        val entity = convert(patient!!)
        return createObservableIO(Callable { patientRoomDAO.addPatient(entity) })
    }

    /**
     * Update patient boolean.
     *
     * @param patientID the patient id
     * @param patient   the patient
     * @return the boolean
     */
    fun updatePatient(patientID: Long, patient: Patient): Boolean {
        val entity = convert(patient)
        entity.id = patientID
        return patientRoomDAO.updatePatient(entity) > 0
    }

    /**
     * Delete patient.
     *
     * @param id the id
     */
    fun deletePatient(id: Long) {
        patientRoomDAO.deletePatient(id)
    }

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    fun getAllPatients(): Observable<List<Patient>> {
        return createObservableIO(Callable<List<Patient>> {
            val patients: MutableList<Patient> = ArrayList()
            var patientEntities: List<PatientEntity> = ArrayList()
            try {
                patientEntities = patientRoomDAO.getAllPatients().blockingGet()
                for (entity in patientEntities) {
                    patients.add(convert(entity))
                }
            } catch (e: Exception) {
                ArrayList<Patient>()
            }
            patients
        })
    }

    /**
     * Is user already saved boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    fun isUserAlreadySaved(uuid: String): Boolean {
        return try {
            val patientEntity = patientRoomDAO.findPatientByUUID(uuid).blockingGet()
            uuid.equals(patientEntity.uuid, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * User does not exist boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    fun userDoesNotExist(uuid: String): Boolean {
        return !isUserAlreadySaved(uuid)
    }

    /**
     * Find patient by uuid patient.
     *
     * @param uuid the uuid
     * @return the patient
     */
    fun findPatientByUUID(uuid: String?): Patient? {
        return try {
            val patient = patientRoomDAO.findPatientByUUID(uuid!!).blockingGet()
            convert(patient)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gets un synced patients.
     *
     * @return the un synced patients
     */
    fun getUnSyncedPatients(): List<Patient> {
        val patientList: MutableList<Patient> = LinkedList()
        val unSyncedPatientList: List<PatientEntity>
        return try {
            unSyncedPatientList = patientRoomDAO.getUnsyncedPatients().blockingGet()
            for (entity in unSyncedPatientList) {
                patientList.add(convert(entity))
            }
            patientList
        } catch (e: Exception) {
            ArrayList()
        }
    }

    /**
     * Find patient by id patient.
     *
     * @param id the id
     * @return the patient
     */
    fun findPatientByID(id: String?): Patient? {
        return try {
            val patientEntity = patientRoomDAO.findPatientByID(id!!).blockingGet()
            convert(patientEntity)
        } catch (e: Exception) {
            return null
        }
    }

}