package org.openmrs.mobile.activities.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import com.openmrs.android_sdk.library.api.repository.LocationRepository
import com.openmrs.android_sdk.library.api.repository.LoginRepository
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.LocationDAO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.DEFAULT_VISIT_TYPE_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.EMPTY_STRING
import com.openmrs.android_sdk.utilities.NetworkUtils.hasNetwork
import com.openmrs.android_sdk.utilities.NetworkUtils.isOnline
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mindrot.jbcrypt.BCrypt
import org.openmrs.mobile.activities.BaseViewModel
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.net.AuthorizationManager
import org.openmrs.mobile.services.UserService
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
        private val loginRepository: LoginRepository,
        private val visitRepository: VisitRepository,
        private val locationRepository: LocationRepository,
        private val locationDAO: LocationDAO,
        private val userService: UserService
) : BaseViewModel<ResultType>() {

    private val _warningDialogLiveData = MutableLiveData<Boolean>()
    val warningDialogLiveData: LiveData<Boolean> get() = _warningDialogLiveData

    private val initialUrl = OpenmrsAndroid.getServerUrl()
    var lastCorrectUrl: String = initialUrl

    var locations = emptyList<LocationEntity>()

    fun showWarningOrLogin(username: String, password: String, url: String, wipeDatabase: Boolean) {
        if (validateLoginInputs(username, password, url)) {
            if (OpenmrsAndroid.getUsername() != EMPTY_STRING && OpenmrsAndroid.getUsername() != username ||
                    OpenmrsAndroid.getServerUrl() != EMPTY_STRING && OpenmrsAndroid.getServerUrl() != initialUrl ||
                    OpenmrsAndroid.getHashedPassword() != EMPTY_STRING && !BCrypt.checkpw(password, OpenmrsAndroid.getHashedPassword()) ||
                    wipeDatabase) {
                _warningDialogLiveData.value = true
            } else {
                _warningDialogLiveData.value = false
                login(username, password, url, wipeDatabase)
            }
        }
    }

    fun login(username: String, password: String, url: String, wipeDatabase: Boolean) {
        setLoading(OperationType.Login)

        // Offline login
        if (!isOnline()) {
            if (OpenmrsAndroid.isUserLoggedOnline() && url == OpenmrsAndroid.getLastLoginServerUrl()) {
                if (OpenmrsAndroid.getUsername() == username && BCrypt.checkpw(password, OpenmrsAndroid.getHashedPassword())) {
                    OpenmrsAndroid.deleteSecretKey()
                    OpenmrsAndroid.setPasswordAndHashedPassword(password)
                    OpenmrsAndroid.setSessionToken(OpenmrsAndroid.getLastSessionToken())
                    setContent(ResultType.LoginOfflineSuccess)
                } else {
                    setContent(ResultType.LoginInvalidCredentials)
                }
            } else if (hasNetwork()) {
                setContent(ResultType.LoginOfflineUnsupported)
            } else {
                setContent(ResultType.LoginNoInternetConnection)
            }

            return
        }

        // Online login
        addSubscription(loginRepository.getSession(username, password)
                .map { session ->
                    if (!session.isAuthenticated) return@map ResultType.LoginInvalidCredentials

                    OpenmrsAndroid.deleteSecretKey()
                    if (wipeDatabase) {
                        OpenMRS.getInstance().deleteDatabase(ApplicationConstants.DB_NAME)
                        setData(session.sessionId!!, url, username, password)
                    }
                    if (AuthorizationManager().isUserNameOrServerEmpty()) {
                        setData(session.sessionId!!, url, username, password)
                    } else {
                        OpenmrsAndroid.setSessionToken(session.sessionId)
                        OpenmrsAndroid.setPasswordAndHashedPassword(password)
                    }
                    val visitType = visitRepository.getVisitType().execute()
                    val visitTypeUuid = visitType?.uuid ?: DEFAULT_VISIT_TYPE_UUID
                    OpenmrsAndroid.setVisitTypeUUID(visitTypeUuid)

                    setLogin(true, url)
                    userService.updateUserInformation(username)

                    return@map ResultType.LoginSuccess
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(it) },
                        { setError(it, OperationType.Login) }
                )
        )
    }

    fun saveLocationsToDatabase(locationList: List<LocationEntity>, selectedLocation: String) {
        OpenmrsAndroid.setLocation(selectedLocation)
        addSubscription(locationDAO.deleteAllLocations()
                .map {
                    locationList.forEach {
                        locationDAO.saveLocation(it).execute()
                    }
                    return@map true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {})
        )
    }

    fun fetchLocations(url: String) {
        setLoading(OperationType.LocationsFetching)
        if (hasNetwork()) {
            lastCorrectUrl = url
            addSubscription(locationRepository.getLocations(url)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { locationsList ->
                                RestServiceBuilder.changeBaseUrl(url.trim { it <= ' ' })
                                OpenmrsAndroid.setServerUrl(url)
                                locations = locationsList
                                setContent(ResultType.LocationsFetchingSuccess)
                            },
                            { setError(it, OperationType.LocationsFetching) }
                    )
            )
        } else {
            addSubscription(locationDAO.getLocations()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { locationsList ->
                        locations = locationsList
                        if (locationsList.isNotEmpty()) {
                            lastCorrectUrl = url
                            setContent(ResultType.LocationsFetchingLocalSuccess)
                        } else {
                            setContent(ResultType.LocationsFetchingNoInternetConnection)
                        }
                    })
        }
    }

    private fun validateLoginInputs(username: String, password: String, url: String): Boolean {
        return username.isNotEmpty() && password.isNotEmpty() && url.isNotEmpty()
    }

    /* Use this method to populate the OpenMRS username, password and everything else. */
    private fun setData(sessionToken: String, url: String, username: String, password: String) {
        OpenmrsAndroid.setSessionToken(sessionToken)
        OpenmrsAndroid.setServerUrl(url)
        OpenmrsAndroid.setUsername(username)
        OpenmrsAndroid.setPasswordAndHashedPassword(password)
    }

    private fun setLogin(isLogin: Boolean, serverUrl: String) {
        OpenmrsAndroid.setUserLoggedOnline(isLogin)
        OpenmrsAndroid.setLastLoginServerUrl(serverUrl)
    }
}
