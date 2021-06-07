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

import com.example.openmrs_android_sdk.library.databases.entities.AllergyEntity;

import java.util.List;

@Dao
public interface AllergyRoomDAO {

    @Insert
    long saveAllergy(AllergyEntity allergyEntity);

    @Update
    int updateAllergy(AllergyEntity allergyEntity);

    @Query("SELECT * FROM allergy WHERE patientId = :patientId")
    List<AllergyEntity> getAllAllergiesByPatientID(String patientId);

    @Query("SELECT * FROM allergy WHERE uuid = :uuid")
    AllergyEntity getAllergyByUUID(String uuid);

    @Query("DELETE FROM allergy WHERE patientId = :patientId")
    void deleteAllPatientAllergy(String patientId);

    @Query("DELETE FROM allergy WHERE uuid = :uuid")
    void deleteAllergyByUUID(String uuid);
}
