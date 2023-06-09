package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.dao.ObservationDAO
import com.openmrs.android_sdk.library.dao.ObservationRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.NetworkUtils
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservationRepository @Inject constructor(
    private val observationDAO: ObservationDAO
) : BaseRepository() {

    val observationRoomDAO: ObservationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).observationRoomDAO()

    /**
     * Get an Observation from Observation Uuid from the server
     *
     * @param uuid the UUID of the observation
     * @return Observable<Observation>
     */
    fun getEncounterByUuid(uuid: String): Observable<Observation> {
        return AppDatabaseHelper.createObservableIO(Callable{
            if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

            restApi.getObservationByUuid(uuid).execute().run{
                if (isSuccessful && body() != null) {
                    return@Callable this.body()!!
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all observation resources of a patient from the server.
     *
     * @param uuid the UUID of the patient
     * @return Observable<List<Resource>> the resource list of observations
     */
    fun getAllEncounterResourcesByPatientUuid(uuid: String): Observable<List<Resource>> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline()) throw Exception("Must be online to fetch encounters")

            restApi.getObservationsByPatientUuid(uuid).execute().run {
                if (isSuccessful) {
                    return@Callable this.body()?.results!!
                } else {
                    throw Exception("Get Encounters error: ${message()}")
                }
            }
        })
    }


}