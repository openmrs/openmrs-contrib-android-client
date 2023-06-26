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

import com.openmrs.android_sdk.library.databases.entities.EncounterEntity;
import com.openmrs.android_sdk.library.databases.entities.StandaloneEncounterEntity;
import com.openmrs.android_sdk.library.models.Encounter;

import java.util.List;

import io.reactivex.Single;

/**
 * The interface Encounter room dao.
 */
@Dao
public interface EncounterRoomDAO {
    /**
     * Gets encounter type by form name.
     *
     * @param formname the formname
     * @return the encounter type by form name
     */
    @Query("SELECT * FROM encounters WHERE display = :formname")
    Single<List<EncounterEntity>> getEncounterTypeByFormName(String formname);

    /**
     * Gets last vitals encounter id.
     *
     * @param patientUUID the patient uuid
     * @return the last vitals encounter id
     */
    @Query("SELECT _id from encounters WHERE visit_id IS NULL AND patient_uuid = :patientUUID")
    Single<Long> getLastVitalsEncounterID(String patientUUID);

    /**
     * Gets last vitals encounter.
     *
     * @param patientUUID the patient uuid
     * @param type        the type
     * @return the last vitals encounter
     */
    @Query("SELECT * FROM encounters WHERE patient_uuid = :patientUUID AND type = :type ORDER BY encounterDatetime DESC LIMIT 1")
    Single<EncounterEntity> getLastVitalsEncounter(String patientUUID, String type);

    /**
     * Gets encounter by uuid.
     *
     * @param encounterUUID the encounter uuid
     * @return the encounter by uuid
     */
    @Query("SELECT _id FROM encounters WHERE uuid = :encounterUUID")
    Single<Long> getEncounterByUUID(String encounterUUID);

    /**
     * Gets encounter uuid by encounter id.
     *
     * @param encounterId the encounter id
     * @return the encounter uuid
     */
    @Query("SELECT uuid FROM encounters WHERE _id = :encounterId")
    Single<String> getEncounterUuidByID(Long encounterId);

    /**
     * Filter encounters by encounter type.
     *
     * @param encounterTypeUUID the encounter uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE type = :encounterTypeUUID")
    Single<List<EncounterEntity>> getEncountersByEncounterType(String encounterTypeUUID);

    /**
     * Filter encounters for a patient by encounter type.
     *
     * @param patientUuid the patient uuid
     * @param encounterTypeUUID the encounter uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE patient_uuid = :patientUuid AND type = :encounterTypeUUID")
    Single<List<EncounterEntity>> getEncountersByEncounterType(String patientUuid, String encounterTypeUUID);

    /**
     * Filter encounters starting from a particular date.
     *
     * @param dateValue the filter date value
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE SUBSTR(encounterDatetime, 1, 10) >= :dateValue")
    Single<List<EncounterEntity>> getEncountersSinceDate(String dateValue);

    /**
     * Filter encounters for a patient starting from a particular date.
     *
     * @param patientUuid the patient uuid
     * @param dateValue the filter date value
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE patient_uuid = :patientUuid AND SUBSTR(encounterDatetime, 1, 10) >= :dateValue")
    Single<List<EncounterEntity>> getEncountersSinceDate(String patientUuid, String dateValue);

    /**
     * Filter encounters by visit id
     *
     * @param visitID the visit id
     * @return the single
     */
    @Query("SELECT * FROM encounters WHERE visit_id = :visitID")
    Single<List<EncounterEntity>> findEncountersByVisitID(String visitID);

    /**
     * Filter encounters for a patient for a particular visit
     *
     * @param patientUuid the patient uuid
     * @param visitUuid the visit uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE patient_uuid = :patientUuid AND visit_uuid = :visitUuid")
    Single<List<EncounterEntity>> getEncountersByVisitUuid(String patientUuid, String visitUuid);

    /**
     * Filter encounters by location
     *
     * @param  location_uuid location uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE location_uuid = :location_uuid")
    Single<List<EncounterEntity>> getEncountersByLocationUuid(String location_uuid);

    /**
     * Filter encounters for a patient by location
     *
     * @param patientUuid the patient uuid
     * @param location_uuid the location uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE patient_uuid = :patientUuid AND location_uuid = :location_uuid")
    Single<List<EncounterEntity>> getEncountersByLocationUuid(String patientUuid, String location_uuid);

    /**
     * Filter encounters by encounter provider
     *
     * @param  encounter_provider_uuid the encounter provider uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE encounter_provider_uuid = :encounter_provider_uuid")
    Single<List<EncounterEntity>> getEncountersByEncounterProvider(String encounter_provider_uuid);

    /**
     * Filter encounters for a patient by encounter provider
     *
     * @param patientUuid the patient uuid
     * @param encounter_provider_uuid the encounter provider uuid
     * @return the list of encounters
     */
    @Query("SELECT * FROM standaloneEncounters WHERE patient_uuid = :patientUuid AND encounter_provider_uuid = :encounter_provider_uuid")
    Single<List<EncounterEntity>> getEncountersByEncounterProvider(String patientUuid, String encounter_provider_uuid);


    /**
     * Add encounter long.
     *
     * @param encounterEntity the encounter entity
     * @return the long
     */
    @Insert
    long addEncounter(EncounterEntity encounterEntity);

    /**
     * Add encounter standalone entity.
     *
     * @param standaloneEncounterEntity the encounterStandalone entity
     * @return the long
     */

    @Insert
    long addEncounterStandaloneEntity(StandaloneEncounterEntity standaloneEncounterEntity);

    /**
     * Add encounter standalone entity.
     *
     * @param standaloneEncounterEntity the encounterStandalone entity
     * @return the long
     */

    @Insert
    List<Long> addEncounterStandaloneEntityList(List<StandaloneEncounterEntity> standaloneEncounterEntity);

    /**
     * Get all standalone Encounters
     *
     * @return the Standalone Encounter Entity List
     */

    @Query("SELECT * FROM standaloneEncounters")
    Single<List<StandaloneEncounterEntity>> getAllStandAloneEncounters();

    /**
     * Delete encounter.
     *
     * @param uuid the uuid
     */
    @Query("DELETE FROM encounters WHERE uuid = :uuid")
    void deleteEncounter(String uuid);

    /**
     * Delete Standalone Encounter.
     *
     * @param patientUuid the uuid of the patient
     */
    @Query("DELETE FROM standaloneEncounters WHERE uuid = :patientUuid")
    void deleteStandaloneEncounter(String patientUuid);

    /**
     * Delete encounter by id.
     *
     * @param id the id
     */
    @Query("DELETE FROM encounters WHERE _id = :id")
    void deleteEncounterByID(long id);

    /**
     * Gets all encounters.
     *
     * @return the all encounters
     */
    @Query("SELECT * FROM encounters")
    Single<List<EncounterEntity>> getAllEncounters();

    /**
     * Update encounter int.
     *
     * @param encounterEntity the encounter entity
     * @return the int
     */
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
     *
     * @param patientID     the patient id
     * @param encounterType the encounter type
     * @return the all encounters by type
     */
    @Query("SELECT e.* FROM observations AS o JOIN encounters AS e ON o.encounter_id = e._id " +
            "JOIN visits AS v on e.visit_id = v._id WHERE v.patient_id = :patientID AND e.type = :encounterType ORDER BY e.encounterDatetime DESC")
    Single<List<EncounterEntity>> getAllEncountersByType(Long patientID, String encounterType);
}
