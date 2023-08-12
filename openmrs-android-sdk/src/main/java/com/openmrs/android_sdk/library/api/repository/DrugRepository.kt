package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.Drug
import com.openmrs.android_sdk.library.models.DrugCreate
import retrofit2.Call
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject

class DrugRepository @Inject constructor() : BaseRepository(){

    val representation =  "full"
    var drugRoomDAO = AppDatabase.getDatabase(context).drugRoomDAO()

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
            executeRequest(call, "Error creating Drug: ").results
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
            executeRequest(call, "Error updating the drug ")
        })
    }
}