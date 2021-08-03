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
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openmrs.android_sdk.library.databases.entities.VisitEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * The interface Visit room dao.
 */
@Dao
public interface VisitRoomDAO {
    /**
     * Add or update long.
     *
     * @param visitEntity the visit entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addOrUpdate(VisitEntity visitEntity);

    /**
     * Add visit long.
     *
     * @param visitEntity the visit entity
     * @return the long
     */
    @Insert
    long addVisit(VisitEntity visitEntity);

    /**
     * Update visit int.
     *
     * @param visitEntity the visit entity
     * @return the int
     */
    @Update
    int updateVisit(VisitEntity visitEntity);

    /**
     * Gets active visits.
     *
     * @return the active visits
     */
    @Query("SELECT * FROM visits WHERE stop_date IS NULL OR stop_date = '' ORDER BY start_date DESC")
    Single<List<VisitEntity>> getActiveVisits();

    /**
     * Gets visits by patient id.
     *
     * @param patientID the patient id
     * @return the visits by patient id
     */
    @Query("SELECT * FROM visits WHERE patient_id = :patientID ORDER BY start_date DESC")
    Single<List<VisitEntity>> getVisitsByPatientID(final Long patientID);

    /**
     * Gets active visit by patient id.
     *
     * @param patientId the patient id
     * @return the active visit by patient id
     */
    @Query("SELECT * FROM visits WHERE patient_id = :patientId AND (stop_date IS NULL OR stop_date = '')  ORDER BY start_date DESC")
    Single<VisitEntity> getActiveVisitByPatientId(Long patientId);

    /**
     * Gets visit by id.
     *
     * @param visitID the visit id
     * @return the visit by id
     */
    @Query("SELECT * FROM visits WHERE _id = :visitID")
    Single<VisitEntity> getVisitByID(final Long visitID);

    /**
     * Gets visits id by uuid.
     *
     * @param visitUUID the visit uuid
     * @return the visits id by uuid
     */
    @Query("SELECT _id FROM visits WHERE uuid = :visitUUID")
    long getVisitsIDByUUID(final String visitUUID);

    /**
     * Gets visit by uuid.
     *
     * @param uuid the uuid
     * @return the visit by uuid
     */
    @Query("SELECT * FROM visits WHERE uuid = :uuid")
    Single<VisitEntity> getVisitByUuid(String uuid);

    /**
     * Delete visits by patient id int.
     *
     * @param patientID the patient id
     * @return the int
     */
    @Query("DELETE FROM visits WHERE patient_id = :patientID")
    int deleteVisitsByPatientId(long patientID);
}