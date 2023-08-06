/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.openmrs.android_sdk.library.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import androidx.room.Dao
import com.openmrs.android_sdk.library.databases.entities.OrderEntity
import io.reactivex.Single

/**
 * The interface Appointment room dao.
 */
@Dao
interface OrderRoomDAO {

    /**
     * Add an Order
     *
     * @param orderEntity the order entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrder(orderEntity: OrderEntity): Long

    /**
     * Add or update long.
     *
     * @param orderEntity the order entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(orderEntity: OrderEntity): Long

    /**
     * Update an Order
     *
     * @param orderEntity the order entity
     * @return the int
     */
    @Update
    fun updateOrder(orderEntity: OrderEntity): Int

    /**
     * Filter orders for a patient
     *
     * @param patientUuid the patient uuid
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid")
    fun getOrdersForPatient(patientUuid: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient with given careSetting
     *
     * @param patientUuid the patient uuid
     * @param careSettingName the careSetting
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND careSettingName = :careSettingName")
    fun getOrdersForCareSetting(patientUuid: String, careSettingName: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient with given orderType
     *
     * @param patientUuid the patient uuid
     * @param orderTypeName the orderType
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND orderType_display = :orderTypeName")
    fun getOrdersForOrderType(patientUuid: String, orderTypeName: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient with given orderType and careSetting
     *
     * @param patientUuid the patient uuid
     * @param orderTypeName the orderType
     * @param careSettingName the careSettingName
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND orderType_display = :orderTypeName AND careSettingName = :careSettingName")
    fun getOrdersForPatient(patientUuid: String, orderTypeName: String, careSettingName: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient after a given date
     *
     * @param patientUuid the patient uuid
     * @param activatedOnOrAfterDate the date
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND SUBSTR(dateActivated, 1, 10) >= :activatedOnOrAfterDate")
    fun getOrdersAfterDate(patientUuid: String, activatedOnOrAfterDate: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient with given orderType and from the given date
     *
     * @param patientUuid the patient uuid
     * @param orderTypeName the orderType
     * @param activatedOnOrAfterDate the date
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND orderType_display = :orderTypeName AND SUBSTR(dateActivated, 1, 10) >= :activatedOnOrAfterDate")
    fun getOrders(patientUuid: String, orderTypeName: String, activatedOnOrAfterDate: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient with given careSetting and from a given date
     *
     * @param patientUuid the patient uuid
     * @param careSettingName the careSetting
     * @param activatedOnOrAfterDate the date
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND careSettingName = :careSettingName AND SUBSTR(dateActivated, 1, 10) >= :activatedOnOrAfterDate")
    fun getOrdersForCareSettingAndFromDate(patientUuid: String, careSettingName: String, activatedOnOrAfterDate: String): Single<List<OrderEntity>>

    /**
     * Filter orders for a patient based on careSetting, orderType and from a given date
     *
     * @param patientUuid the patient uuid
     * @param careSettingName the careSetting
     * @param orderTypeName the orderType
     * @param activatedOnOrAfterDate the date
     *
     * @return the orders
     */
    @Query("SELECT * FROM orders WHERE patientUuid = :patientUuid AND careSettingName = :careSettingName AND orderType_display = :orderTypeName AND SUBSTR(dateActivated, 1, 10) >= :activatedOnOrAfterDate")
    fun getOrders(patientUuid: String, careSettingName: String, orderTypeName: String, activatedOnOrAfterDate: String): Single<List<OrderEntity>>
}