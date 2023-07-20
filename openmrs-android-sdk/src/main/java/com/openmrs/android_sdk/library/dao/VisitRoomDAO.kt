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

import androidx.room.*
import com.openmrs.android_sdk.library.databases.entities.VisitEntity
import io.reactivex.Single

/**
 * The interface Visit room dao.
 */
@Dao
interface VisitRoomDAO {
    /**
     * Add or update long.
     *
     * @param visitEntity the visit entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(visitEntity: VisitEntity): Long

    /**
     * Add visit long.
     *
     * @param visitEntity the visit entity
     * @return the long
     */
    @Insert
    fun addVisit(visitEntity: VisitEntity): Long

    /**
     * Update visit int.
     *
     * @param visitEntity the visit entity
     * @return the int
     */
    @Update
    fun updateVisit(visitEntity: VisitEntity): Int

    /**
     * Gets active visits.
     *
     * @return the active visits
     */
    @get:Query("SELECT * FROM visits WHERE stop_date IS NULL OR stop_date = '' ORDER BY start_date DESC")
    val activeVisits: Single<List<VisitEntity>>

    /**
     * Gets visits by patient id and visit location.
     *
     * @param patientID the patient id
     * @param visitPlace the visit place
     * @return the visits by patient id
     */
    @Query("SELECT * FROM visits WHERE patient_id = :patientID AND visit_place = :visitPlace ORDER BY start_date DESC")
    fun getVisitsByPatientIDAndVisitPlace(
        patientID: Long,
        visitPlace: String
    ): Single<List<VisitEntity>>

    /**
     * Gets active visits by patient id filtered by visit place.
     *
     * @param patientId the patient id
     * @param visitPlace the visit place
     * @return the active visit by patient id
     */
    @Query(
        "SELECT * FROM visits WHERE patient_id = :patientId " +
                "AND visit_place = :visitPlace " +
                "AND (stop_date IS NULL OR stop_date = '') " +
                "ORDER BY start_date DESC"
    )
    fun getActiveVisitByPatientIdAndVisitPlace(
        patientId: Long,
        visitPlace: String
    ): Single<VisitEntity>

    /**
     * Gets all visit places (which are associated to atleast one visit saved)
     *
     * @param patientID the patient id
     * @return the visit_place list
     */
    @Query("SELECT visit_place FROM visits WHERE patient_id = :patientID")
    fun getVisitPlacesByPatientID(patientID: Long): Single<List<String>>

    /**
     * Gets visits by patient id.
     *
     * @param patientID the patient id
     * @return the visits by patient id
     */
    @Query("SELECT * FROM visits WHERE patient_id = :patientID ORDER BY start_date DESC")
    fun getVisitsByPatientID(patientID: Long): Single<List<VisitEntity>>

    /**
     * Gets active visit by patient id.
     *
     * @param patientId the patient id
     * @return the active visit by patient id
     */
    @Query("SELECT * FROM visits WHERE patient_id = :patientId AND (stop_date IS NULL OR stop_date = '')  ORDER BY start_date DESC")
    fun getActiveVisitByPatientId(patientId: Long): Single<VisitEntity>?

    /**
     * Gets visit by id.
     *
     * @param visitID the visit id
     * @return the visit by id
     */
    @Query("SELECT * FROM visits WHERE _id = :visitID")
    fun getVisitByID(visitID: Long): Single<VisitEntity>?

    /**
     * Gets visits id by uuid.
     *
     * @param visitUUID the visit uuid
     * @return the visits id by uuid
     */
    @Query("SELECT _id FROM visits WHERE uuid = :visitUUID")
    fun getVisitsIDByUUID(visitUUID: String): Long

    /**
     * Gets visit by uuid.
     *
     * @param uuid the uuid
     * @return the visit by uuid
     */
    @Query("SELECT * FROM visits WHERE uuid = :uuid")
    fun getVisitByUuid(uuid: String): Single<VisitEntity>?

    /**
     * Delete visits by patient id int.
     *
     * @param patientID the patient id
     * @return the int
     */
    @Query("DELETE FROM visits WHERE patient_id = :patientID")
    fun deleteVisitsByPatientId(patientID: Long): Int
}