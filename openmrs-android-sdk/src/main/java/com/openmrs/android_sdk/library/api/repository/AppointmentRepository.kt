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
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.models.Appointment
import rx.Observable
import java.io.IOException
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor() : BaseRepository(){

    val representation =  context.resources.getString(R.string.appointment_resource_representation)
    var appointmentRoomDAO = AppDatabase.getDatabase(context).appointmentRoomDAO()

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
                throw IOException("Error with fetching visits by patient uuid: " + response.message())
            }
        })
    }
}