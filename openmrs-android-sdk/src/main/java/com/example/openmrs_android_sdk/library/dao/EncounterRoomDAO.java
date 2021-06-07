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

import com.example.openmrs_android_sdk.library.databases.entities.EncounterEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface EncounterRoomDAO {
    @Query("SELECT * FROM encounters WHERE display = :formname")
    Single<List<EncounterEntity>> getEncounterTypeByFormName(String formname);

    @Query("SELECT _id from encounters WHERE visit_id IS NULL AND patient_uuid = :patientUUID")
    Single<Long> getLastVitalsEncounterID(String patientUUID);

    @Query("SELECT * FROM encounters WHERE patient_uuid = :patientUUID AND type = :type ORDER BY encounterDatetime DESC LIMIT 1")
    Single<EncounterEntity> getLastVitalsEncounter(String patientUUID, String type);

    @Query("SELECT _id FROM encounters WHERE uuid = :encounterUUID")
    Single<Long> getEncounterByUUID(String encounterUUID);

    @Query("SELECT * FROM encounters WHERE visit_id = :visitID")
    Single<List<EncounterEntity>> findEncountersByVisitID(String visitID);

    @Insert
    long addEncounter(EncounterEntity encounterEntity);

    @Query("DELETE FROM encounters WHERE uuid = :uuid")
    void deleteEncounter(String uuid);

    @Query("DELETE FROM encounters WHERE _id = :id")
    void deleteEncounterByID(long id);

    @Query("SELECT * FROM encounters")
    Single<List<EncounterEntity>> getAllEncounters();

    @Update
    int updateEncounter(EncounterEntity encounterEntity);

    /**
     * To Update encounter
     * 1. Delete that encounter with that UUID
     * 2. Create new Encounter with that UUID
     * <p>
     * To Insert encounter
     * 1. Delete that encounter with that UUID
     * 2. Create new Encounter with that UUID
     */
    @Query("SELECT e.* FROM observations AS o JOIN encounters AS e ON o.encounter_id = e._id " +
            "JOIN visits AS v on e.visit_id = v._id WHERE v.patient_id = :patientID AND e.type = :encounterType ORDER BY e.encounterDatetime DESC")
    Single<List<EncounterEntity>> getAllEncountersByType(Long patientID, String encounterType);
}
