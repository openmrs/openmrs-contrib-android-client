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

package org.openmrs.mobile.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.openmrs.mobile.databases.entities.VisitEntity;

import java.util.List;
import io.reactivex.Flowable;

@Dao
public interface VisitRoomDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveOrUpdate(VisitEntity visitEntity);

    @Insert
    long saveVisit (VisitEntity visitEntity);

    @Update
    int updateVisit(VisitEntity visitEntity);

    @Query("SELECT * FROM visits")
    Flowable<List<VisitEntity>> getActiveVisits();

    @Query("SELECT * FROM visits WHERE patient_id = :patientID")
    Flowable<List<VisitEntity>> getVisitsByPatientID(final Long patientID);

    @Query("SELECT * FROM visits WHERE patient_id = :patientId LIMIT 1")
    Flowable<VisitEntity> getFirstActiveVisitByPatientId(Long patientId);

    @Query("SELECT * FROM visits WHERE _id = :visitID")
    Flowable<VisitEntity> getVisitByID(final Long visitID);

    @Query("SELECT _id FROM visits WHERE uuid = :visitUUID")
    long getVisitsIDByUUID(final String visitUUID);

    @Query("SELECT * FROM visits WHERE uuid = :uuid")
    Flowable<VisitEntity> getVisitByUuid(String uuid);

    @Delete
    int deleteVisitsByPatientId(VisitEntity visitEntity);

}