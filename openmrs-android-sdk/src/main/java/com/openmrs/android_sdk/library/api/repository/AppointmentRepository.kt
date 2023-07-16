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

import android.content.Context
import com.openmrs.android_sdk.R
import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.dao.AppointmentRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.models.*
import retrofit2.Call
import rx.Observable
import java.io.IOException
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor(
    val context: Context,
    val logger: OpenMRSLogger,
    var restApi: RestApi,
    val appointmentRoomDAO: AppointmentRoomDAO
) {

    val representation =  context.resources.getString(R.string.appointment_resource_representation).trim()

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
     * Creates an appointment block
     *
     * @param startDate the start date of the block
     * @param endDate the end date of the block
     * @param locationUUID the location name of the block
     * @param types the list of appointment types
     *
     * @return the AppointmentBlock object
     */
    fun createAppointmentBlock(
        startDate: String,
        endDate: String,
        locationUUID: String,
        types: List<AppointmentType>
    ): Observable<AppointmentBlock> {
        return createObservableIO<AppointmentBlock>(Callable {
            val call = restApi.createAppointmentBlock(startDate, endDate, locationUUID, types)
            executeRequest(call, "Error creating an appointment block: ")
        })
    }

    /**
     * Creates a time slot
     *
     * @param timeSlot the TimeSlot object
     *
     * @return the TimeSlot object
     */
    fun createTimeSlot(timeSlot: TimeSlot): Observable<TimeSlot> {
        return createObservableIO<TimeSlot>(Callable {
            val call = restApi.createTimeslot(timeSlot)
            executeRequest(call, "Error creating Time Slot: ")
        })
    }

    /**
     * Creates a time slot
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param appointmentBlock the Appointment Block object
     *
     * @return the TimeSlot object
     */
    fun createTimeSlot(
        startDate: String,
        endDate: String,
        appointmentBlock: AppointmentBlock
    ): Observable<TimeSlot> {
        return createObservableIO<TimeSlot>(Callable {
            val call = restApi.createTimeslot(startDate, endDate, appointmentBlock)
            executeRequest(call, "Error creating Time Slot: ")
        })
    }

    /**
     * Creates an appointment if appointment block is available
     *
     * @param patientUUID the patient uuid
     * @param appointmentStatus the appointment status
     * @param appointmentTypeUUID the Appointment type uuid
     * @param timeSlotStartDate the timeslot start
     * @param timeSlotEndDate the timeslot end
     * @param appointmentBlock the Appointment Block object
     *
     * @return the Appointment object
     */
    fun createAppointment(
        patientUUID: String,
        appointmentStatus: String,
        appointmentTypeUUID: String,
        timeSlotStartDate: String,
        timeSlotEndDate: String,
        appointmentBlock: AppointmentBlock
    ): Observable<Appointment> {
        val timeSlot = createTimeSlot(timeSlotStartDate, timeSlotEndDate, appointmentBlock).toBlocking().first()
        return createObservableIO<Appointment>(Callable {
            val call = restApi.createAppointment(patientUUID, appointmentStatus, appointmentTypeUUID, timeSlot)
            executeRequest(call, "Error creating Appointment: ")
        })

    }

    /**
     * Creates an appointment if appointment block is NOT available
     *
     * @param patientUUID the patient uuid
     * @param appointmentStatus the appointment status
     * @param appointmentTypeUUID the Appointment type uuid
     * @param timeSlotStartDate the timeslot start
     * @param timeSlotEndDate the timeslot end
     * @param blockStartDate the start date of the block
     * @param blockEndDate the end date of the block
     * @param blockLocationUUID the uuid of the location of the block
     * @param blockTypes the list of appointment types
     *
     * @return the Appointment object
     */
    fun createAppointment(
        patientUUID: String,
        appointmentStatus: String,
        appointmentTypeUUID: String,
        timeSlotStartDate: String,
        timeSlotEndDate: String,
        blockStartDate: String,
        blockEndDate: String,
        blockLocationUUID: String,
        blockTypes: List<AppointmentType>
    ) {
        val mAppointmentBlock = createAppointmentBlock(blockStartDate, blockEndDate,
            blockLocationUUID, blockTypes).toBlocking().first()

        createAppointment(patientUUID, appointmentStatus, appointmentTypeUUID, timeSlotStartDate, timeSlotEndDate, mAppointmentBlock)
    }

    /**
     * Fetch appointments from server and save to local db
     *
     * @param patientUUID the patient uuid
     */
    fun getAppointmentsAndSave(patientUUID: String): Observable<List<Appointment>> {
        return createObservableIO<List<Appointment>>(Callable {
            val call = restApi.getAppointmentsForPatient(patientUUID, representation)
            val response = call.execute()
            if (response.isSuccessful && response != null) {
                val appointments = response.body()!!.results
                for (appointment in appointments) {
                    val appointmentEntity = AppDatabaseHelper.convert(appointment)
                    appointmentRoomDAO.addAppointment(appointmentEntity)
                }
                return@Callable appointments
            } else {
                throw IOException("Error getting and saving appointments from server: " + response.message())
            }
        })
    }
}