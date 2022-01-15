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
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openmrs.android_sdk.library.databases.entities.PatientEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * The interface Patient room dao.
 */
@Dao
public interface PatientRoomDAO {
    /**
     * Add patient long.
     *
     * @param patientEntity the patient entity
     * @return the long
     */
    @Insert
    long addPatient(PatientEntity patientEntity);

    /**
     * Delete patient.
     *
     * @param id the id
     */
    @Query("DELETE FROM patients WHERE _id = :id")
    void deletePatient(long id);

    /**
     * Update patient int.
     *
     * @param patientEntity the patient entity
     * @return the int
     */
    @Update
    int updatePatient(PatientEntity patientEntity);

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    @Query("SELECT * FROM patients")
    Single<List<PatientEntity>> getAllPatients();

    /**
     * Find patient by uuid single.
     *
     * @param uuid the uuid
     * @return the single
     */
    @Query("SELECT * FROM patients WHERE uuid = :uuid")
    Single<PatientEntity> findPatientByUUID(String uuid);

    /**
     * Gets unsynced patients.
     *
     * @return the unsynced patients
     */
    @Query("SELECT * FROM patients WHERE synced = 0")
    Single<List<PatientEntity>> getUnsyncedPatients();

    /**
     * Find patient by id single.
     *
     * @param id the id
     * @return the single
     */
    @Query("SELECT * FROM patients WHERE _id = :id")
    Single<PatientEntity> findPatientByID(String id);
}
