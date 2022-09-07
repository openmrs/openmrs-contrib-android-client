package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.models.ConceptAnswers
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

@Singleton
class ConceptRepository @Inject constructor() : BaseRepository() {

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
}
