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
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.databases.entities.PatientEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PatientRoomDAO {
    @Insert
    long addPatient(PatientEntity patientEntity);

    @Query("DELETE FROM patients WHERE _id = :id")
    void deletePatient(long id);

    @Update
    int updatePatient(PatientEntity patientEntity);

    @Query("SELECT * FROM patients")
    Single<List<PatientEntity>> getAllPatients();

    @Query("SELECT * FROM patients WHERE uuid = :uuid")
    Single<PatientEntity> findPatientByUUID(String uuid);

    @Query("SELECT * FROM patients WHERE synced = 0")
    Single<List<PatientEntity>> getUnsyncedPatients();

    @Query("SELECT * FROM patients WHERE _id = :id")
    Single<PatientEntity> findPatientByID(String id);
}
