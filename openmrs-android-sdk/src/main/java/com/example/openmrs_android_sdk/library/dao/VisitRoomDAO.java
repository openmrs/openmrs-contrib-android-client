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
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.databases.entities.VisitEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface VisitRoomDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addOrUpdate(VisitEntity visitEntity);

    @Insert
    long addVisit(VisitEntity visitEntity);

    @Update
    int updateVisit(VisitEntity visitEntity);

    @Query("SELECT * FROM visits WHERE stop_date IS NULL OR stop_date = '' ORDER BY start_date DESC")
    Single<List<VisitEntity>> getActiveVisits();

    @Query("SELECT * FROM visits WHERE patient_id = :patientID ORDER BY start_date DESC")
    Single<List<VisitEntity>> getVisitsByPatientID(final Long patientID);

    @Query("SELECT * FROM visits WHERE patient_id = :patientId AND (stop_date IS NULL OR stop_date = '')  ORDER BY start_date DESC")
    Single<VisitEntity> getActiveVisitByPatientId(Long patientId);

    @Query("SELECT * FROM visits WHERE _id = :visitID")
    Single<VisitEntity> getVisitByID(final Long visitID);

    @Query("SELECT _id FROM visits WHERE uuid = :visitUUID")
    long getVisitsIDByUUID(final String visitUUID);

    @Query("SELECT * FROM visits WHERE uuid = :uuid")
    Single<VisitEntity> getVisitByUuid(String uuid);

    @Query("DELETE FROM visits WHERE patient_id = :patientID")
    int deleteVisitsByPatientId(long patientID);
}