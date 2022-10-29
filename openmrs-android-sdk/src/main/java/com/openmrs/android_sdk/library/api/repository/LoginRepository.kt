package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper.createObservableIO
import com.openmrs.android_sdk.library.models.Session
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable

@Singleton
class LoginRepository @Inject constructor() : BaseRepository() {

    /**
     * Gets an authenticated session by username and password.
     *
     * @param username authenticated username
     * @param password authenticated password
     * @return observable session
     */
    fun getSession(username: String, password: String): Observable<Session> {
        return createObservableIO(Callable {
            restApi = RestServiceBuilder.createService(RestApi::class.java, username, password)
            restApi.getSession().execute().run {
                if (isSuccessful && body() != null) return@Callable body()!!
                else throw Exception("Error fetching session: ${message()}")
            }
        })
    }
}
