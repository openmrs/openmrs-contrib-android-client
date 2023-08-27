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
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openmrs.android_sdk.library.databases.entities.PatientEntity
import io.reactivex.Single

/**
 * The interface Patient room dao.
 */
@Dao
interface PatientRoomDAO {
    /**
     * Add patient long.
     *
     * @param patientEntity the patient entity
     * @return the long
     */
    @Insert
    fun addPatient(patientEntity: PatientEntity): Long

    /**
     * Delete patient.
     *
     * @param id the id
     */
    @Query("DELETE FROM patients WHERE _id = :id")
    fun deletePatient(id: Long)

    /**
     * Update patient int.
     *
     * @param patientEntity the patient entity
     * @return the int
     */
    @Update
    fun updatePatient(patientEntity: PatientEntity): Int

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Single<List<PatientEntity>>

    /**
     * Find patient by uuid single.
     *
     * @param uuid the uuid
     * @return the single
     */
    @Query("SELECT * FROM patients WHERE uuid = :uuid")
    fun findPatientByUUID(uuid: String): Single<PatientEntity>

    /**
     * Gets unsynced patients.
     *
     * @return the unsynced patients
     */
    @Query("SELECT * FROM patients WHERE synced = 0")
    fun getUnsyncedPatients(): Single<List<PatientEntity>>

    /**
     * Find patient by id single.
     *
     * @param id the id
     * @return the single
     */
    @Query("SELECT * FROM patients WHERE _id = :id")
    fun findPatientByID(id: String): Single<PatientEntity>
}