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

import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;

import java.util.List;

/**
 * The interface Allergy room dao.
 */
@Dao
public interface AllergyRoomDAO {

    /**
     * Save allergy long.
     *
     * @param allergyEntity the allergy entity
     * @return the long
     */
    @Insert
    long saveAllergy(AllergyEntity allergyEntity);

    /**
     * Update allergy int.
     *
     * @param allergyEntity the allergy entity
     * @return the int
     */
    @Update
    int updateAllergy(AllergyEntity allergyEntity);

    /**
     * Gets all allergies by patient id.
     *
     * @param patientId the patient id
     * @return the all allergies by patient id
     */
    @Query("SELECT * FROM allergy WHERE patientId = :patientId")
    List<AllergyEntity> getAllAllergiesByPatientID(String patientId);

    /**
     * Gets allergy by uuid.
     *
     * @param uuid the uuid
     * @return the allergy by uuid
     */
    @Query("SELECT * FROM allergy WHERE uuid = :uuid")
    AllergyEntity getAllergyByUUID(String uuid);

    /**
     * Delete all patient allergy.
     *
     * @param patientId the patient id
     */
    @Query("DELETE FROM allergy WHERE patientId = :patientId")
    void deleteAllPatientAllergy(String patientId);

    /**
     * Delete allergy by uuid.
     *
     * @param uuid the uuid
     */
    @Query("DELETE FROM allergy WHERE uuid = :uuid")
    void deleteAllergyByUUID(String uuid);
}
