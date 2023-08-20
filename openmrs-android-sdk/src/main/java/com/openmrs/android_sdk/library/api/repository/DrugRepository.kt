/* The contents of this file are subject to the OpenMRS Public License
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

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.Drug
import com.openmrs.android_sdk.library.models.DrugCreate
import com.openmrs.android_sdk.utilities.NetworkUtils
import retrofit2.Call
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject

class DrugRepository @Inject constructor() : BaseRepository(){

    val representation =  "full"
    var drugRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance()!!.applicationContext).drugRoomDAO()

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
     * Creates a drug on the server
     *
     * @param drug the DrugCreate type
     *
     * @return the Drug type
     */
    fun createDrug(drug: DrugCreate): Observable<Drug> {
        return AppDatabaseHelper.createObservableIO<Drug>(Callable {
            val call = restApi.createDrug(drug)
            executeRequest(call, "Error creating Drug: ")
        })
    }

    /**
     * Get all drugs from the server
     *
     * @return the List of Drug type
     */
    fun getAllDrugs(): Observable<List<Drug>> {
        return AppDatabaseHelper.createObservableIO<List<Drug>>(Callable {
            val call = restApi.getAllDrugs(representation)
            executeRequest(call, "Error getting all Drugs: ").results
        })
    }

    /**
     * Get a drug by UUID
     *
     * @return the Drug with given UUID
     */
    fun getDrugByUuid(uuid: String): Observable<Drug> {
        return AppDatabaseHelper.createObservableIO<Drug>(Callable {
            val call = restApi.getDrugByUuid(uuid, representation)
            executeRequest(call, "Error fetching the drug ")
        })
    }

    /**
     * Update a drug by UUID
     *
     * @param uuid the uuid of the drug to update
     * @param drug the DrugCreate type
     *
     * @return the updated Drug
     */
    fun updateDrug(uuid: String, drug: DrugCreate): Observable<Drug> {
        return AppDatabaseHelper.createObservableIO<Drug>(Callable {
            val call = restApi.updateDrug(uuid, drug)
            executeRequest(call, "Error updating the drug ")
        })
    }

    /**
     * Delete a drug by UUID
     *
     * @param uuid the uuid of the drug to update
     *
     * @return the deleted Drug
     */
    fun deleteDrug(uuid: String): Observable<Drug> {
        return AppDatabaseHelper.createObservableIO<Drug>(Callable {
            val call = restApi.deleteDrug(uuid)
            executeRequest(call, "Error deleting the drug ")
        })
    }

    /**
     * Get all drugs from the server and save to local database
     *
     * @return Observable<List<Drug>>
     */

    fun getAllDrugsAndSaveLocally(): Observable<List<Drug>> {
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch drugs")
        restApi.getAllDrugs("full").execute().run {

            if (isSuccessful && this.body() != null) {
                val drugs: List<Drug> = this.body()!!.results
                val convertedList = AppDatabaseHelper.convertDrugListToEntityList(drugs)
                drugRoomDAO.insertOrUpdateDrugs(convertedList)
                return Observable.just(drugs)
            } else {
                throw Exception("getAllDrugsAndSaveLocally error: ${message()}")
            }
        }
    }

    /**
     * Get a Drug by UUID and save to local database
     *
     * @param uuid the uuid of the Drug to fetch and save
     *
     * @return the Drug fetched from the server
     */
    fun getDrugByUuidAndSaveLocally(uuid: String): Observable<Drug> {
        if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch drug")
        restApi.getDrugByUuid(uuid, "full").execute().run {

            if (isSuccessful && this.body() != null) {
                val drug: Drug = this.body()!!
                val drugEntity = AppDatabaseHelper.convert(drug)
                drugRoomDAO.createDrug(drugEntity)
                return Observable.just(drug)
            } else {
                throw Exception("getDrugByUuidAndSaveLocally error: ${message()}")
            }
        }
    }
}