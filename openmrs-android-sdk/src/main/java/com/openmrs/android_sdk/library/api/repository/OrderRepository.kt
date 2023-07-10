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
package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.R
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.*
import retrofit2.Call
import rx.Observable
import java.io.IOException
import java.util.concurrent.Callable
import javax.inject.Inject

class OrderRepository @Inject constructor() : BaseRepository(){

    val representation =  context.resources.getString(R.string.orderGet_resource_representation).trim()
    var orderRoomDAO = AppDatabase.getDatabase(context).orderRoomDAO()

    /**
     * Executes a retrofit request
     *
     * @param call the interface call
     * @param message the error message to display
     *
     * @return T
     */
    fun <T> executeRequest(call: Call<T>, message: String): T {
        val response = call.execute()

        if (response.isSuccessful && response != null) {
            return response.body()!!
        } else {
            logger.e(message + response.message())
            throw Exception(response.message())
        }
    }

    /**
     * Creates an Order remotely
     *
     * @param startDate the start date of the block
     * @param endDate the end date of the block
     * @param locationUUID the location name of the block
     * @param types the list of appointment types
     *
     * @return the AppointmentBlock object
     */
    fun createOrder(orderCreate: OrderCreate): Observable<OrderGet> {
        return AppDatabaseHelper.createObservableIO<OrderGet>(Callable {
            val call = restApi.createOrder(orderCreate)
            executeRequest(call, "Error creating the Order: ")
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersAndSave(patientUUID: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatient(patientUUID, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersAndSave(patientUUID: String, careSetting: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatient(patientUUID, careSetting, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersAndSave(patientUUID: String, careSetting: String, orderType: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatient(patientUUID, orderType, careSetting,representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersAndSave(patientUUID: String, careSetting: String, orderType: String, activatedOnOrAfterDate: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatient(patientUUID, orderType, careSetting, activatedOnOrAfterDate, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersWithOrderTypeAndSave(patientUUID: String, orderType: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatientWithOrderType(patientUUID, orderType, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

    /**
     * Fetch orders of a patient from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getOrdersFromDateAndSave(patientUUID: String, activatedOnOrAfterDate: String): Observable<List<OrderGet>> {
        return AppDatabaseHelper.createObservableIO<List<OrderGet>>(Callable {
            val call = restApi.getOrdersForPatientFromDate(patientUUID, activatedOnOrAfterDate, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val orders = response.body()!!.results
                for (order in orders) {
                    val orderEntity = AppDatabaseHelper.convert(order)
                    orderRoomDAO.addOrder(orderEntity)
                }
                return@Callable orders
            } else {
                throw IOException("Error getting and saving orders from server: " + response.message())
            }
        })
    }

}