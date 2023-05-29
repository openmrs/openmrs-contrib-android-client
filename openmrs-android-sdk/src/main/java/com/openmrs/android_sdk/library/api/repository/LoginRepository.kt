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
            val response = restApi.getSession().execute()
            if (response.isSuccessful && response.body() != null) {
                val sessionId = response.headers().get("Set-Cookie").toString().split("=")[1]
                response.body()?.sessionId = sessionId
                return@Callable response.body()!!
            } else {
                throw Exception("Error fetching session: ${response.message()}")
            }
        })
    }
}
