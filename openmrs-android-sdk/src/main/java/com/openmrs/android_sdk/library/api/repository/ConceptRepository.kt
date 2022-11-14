package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.dao.ConceptRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.ConceptAnswers
import com.openmrs.android_sdk.library.models.ConceptMembers
import com.openmrs.android_sdk.library.models.SystemProperty
import com.openmrs.android_sdk.utilities.ApplicationConstants.API.FULL
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

@Singleton
class ConceptRepository @Inject constructor(private val conceptRoomDAO: ConceptRoomDAO) : BaseRepository() {

    /**
     * Gets system property.
     *
     * @param systemProperty the system property
     * @return the system property
     */
    fun getSystemProperty(systemProperty: String): Observable<SystemProperty> {
        return AppDatabaseHelper.createObservableIO(Callable {
            restApi.getSystemProperty(systemProperty, FULL).execute().run {
                if (isSuccessful && body() != null) return@Callable body()!!.results[0]
                else throw Exception("Error fetching concepts: ${message()}")
            }
        })
    }

    /**
     * Get concept answers by UUID
     *
     * @param uuid UUID of the concept
     * @return Observable ConceptAnswers
     */
    fun getConceptByUuid(uuid: String): Observable<ConceptAnswers> {
        return AppDatabaseHelper.createObservableIO(Callable {
            restApi.getConceptFromUUID(uuid).execute().run {
                if (isSuccessful && body() != null) return@Callable body()!!
                else throw Exception("Error fetching concepts by uuid: ${message()}")
            }
        })
    }

    /**
     * Gets concept members.
     *
     * @param uuid the uuid of the concept
     * @return the concept members
     */
    fun getConceptMembers(uuid: String): Observable<ConceptMembers> {
        return AppDatabaseHelper.createObservableIO(Callable {
            restApi.getConceptMembersFromUUID(uuid).execute().run {
                if (isSuccessful && body() != null) return@Callable body()!!
                else throw Exception("Error fetching concept members: ${message()}")
            }
        })
    }


    fun getConceptCountFromDb() = conceptRoomDAO.conceptsCount

}
