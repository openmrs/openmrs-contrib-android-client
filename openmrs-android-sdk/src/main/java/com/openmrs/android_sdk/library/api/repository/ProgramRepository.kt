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

import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.ProgramCreate
import com.openmrs.android_sdk.library.models.ProgramGet
import retrofit2.Call
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject

class ProgramRepository @Inject constructor() : BaseRepository(){

    val representation =  "full"
    var programRoomDAO = AppDatabase.getDatabase(context).programRoomDAO()

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
     * Creates a program on the server
     *
     * @param program the ProgramCreate type
     *
     * @return the ProgramGet type Observable
     */
    fun createProgram(program: ProgramCreate): Observable<ProgramGet> {
        return AppDatabaseHelper.createObservableIO<ProgramGet>(Callable {
            val call = restApi.createProgram(program)
            executeRequest(call, "Error creating Program: ")
        })
    }

    /**
     * Get all Programs from the server
     *
     * @return the List of ProgramGet type
     */
    fun getAllPrograms(): Observable<List<ProgramGet>> {
        return AppDatabaseHelper.createObservableIO<List<ProgramGet>>(Callable {
            val call = restApi.getAllPrograms(representation)
            executeRequest(call, "Error getting all the programs: ").results
        })
    }

    /**
     * Get a Program by UUID
     *
     * @return the Program with given UUID
     */
    fun getProgramByUuid(uuid: String): Observable<ProgramGet> {
        return AppDatabaseHelper.createObservableIO<ProgramGet>(Callable {
            val call = restApi.getProgramByUuid(uuid, representation)
            executeRequest(call, "Error fetching the program ")
        })
    }

    /**
     * Update a Program by UUID
     *
     * @param uuid the uuid of the program to update
     * @param program the ProgramCreate type
     *
     * @return the updated Program
     */
    fun updateProgram(uuid: String, program: ProgramCreate): Observable<ProgramGet> {
        return AppDatabaseHelper.createObservableIO<ProgramGet>(Callable {
            val call = restApi.updateProgram(uuid, program)
            executeRequest(call, "Error updating the program ")
        })
    }

    /**
     * Delete a Program by UUID
     *
     * @param uuid the uuid of the Program to update
     *
     * @return the deleted Program
     */
    fun deleteProgram(uuid: String): Observable<ProgramGet> {
        return AppDatabaseHelper.createObservableIO<ProgramGet>(Callable {
            val call = restApi.deleteProgram(uuid)
            executeRequest(call, "Error deleting the program ")
        })
    }

}