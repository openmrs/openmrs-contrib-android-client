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
package org.openmrs.mobile.activities.login

import org.mindrot.jbcrypt.BCrypt
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.api.UserService
import org.openmrs.mobile.api.repository.VisitRepository
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.application.OpenMRSLogger
import org.openmrs.mobile.dao.LocationDAO
import org.openmrs.mobile.databases.entities.LocationEntity
import org.openmrs.mobile.listeners.retrofitcallbacks.GetVisitTypeCallback
import org.openmrs.mobile.models.Results
import org.openmrs.mobile.models.Session
import org.openmrs.mobile.models.VisitType
import org.openmrs.mobile.net.AuthorizationManager
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.NetworkUtils.hasNetwork
import org.openmrs.mobile.utilities.NetworkUtils.isOnline
import org.openmrs.mobile.utilities.StringUtils.notEmpty
import org.openmrs.mobile.utilities.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginPresenter : BasePresenter, LoginContract.Presenter {
    private var restApi: RestApi
    private var visitRepository: VisitRepository
    private var userService: UserService
    private var loginView: LoginContract.View
    private var mOpenMRS: OpenMRS
    private var mLogger: OpenMRSLogger
    private var authorizationManager: AuthorizationManager
    private var locationDAO: LocationDAO
    private var mWipeRequired = false

    constructor(loginView: LoginContract.View, openMRS: OpenMRS) {
        this.loginView = loginView
        mOpenMRS = openMRS
        mLogger = openMRS.openMRSLogger
        this.loginView.setPresenter(this)
        authorizationManager = AuthorizationManager()
        locationDAO = LocationDAO()
        restApi = RestServiceBuilder.createService(RestApi::class.java)
        visitRepository = VisitRepository()
        userService = UserService()
    }

    constructor(restApi: RestApi, visitRepository: VisitRepository, locationDAO: LocationDAO,
                userService: UserService, loginView: LoginContract.View, mOpenMRS: OpenMRS,
                mLogger: OpenMRSLogger, authorizationManager: AuthorizationManager) {
        this.restApi = restApi
        this.visitRepository = visitRepository
        this.locationDAO = locationDAO
        this.userService = userService
        this.loginView = loginView
        this.mOpenMRS = mOpenMRS
        this.mLogger = mLogger
        this.authorizationManager = authorizationManager
        this.loginView.setPresenter(this)
    }

    override fun subscribe() {
        // This method is intentionally empty
    }

    override fun login(username: String?, password: String?, url: String?, oldUrl: String?) {
        if (validateLoginFields(username!!, password!!, url!!)) {
            loginView.hideSoftKeys()
            if (mOpenMRS.username != ApplicationConstants.EMPTY_STRING &&
                    mOpenMRS.username != username ||
                    mOpenMRS.serverUrl != ApplicationConstants.EMPTY_STRING &&
                    mOpenMRS.serverUrl != oldUrl ||
                    mOpenMRS.hashedPassword != ApplicationConstants.EMPTY_STRING &&
                    !BCrypt.checkpw(password, mOpenMRS.hashedPassword) ||
                    mWipeRequired) {
                loginView.showWarningDialog()
            } else {
                authenticateUser(username, password, url)
            }
        }
    }

    override fun authenticateUser(username: String?, password: String?, url: String?) {
        authenticateUser(username, password, url, mWipeRequired)
    }

    override fun authenticateUser(username: String?, password: String?, url: String?, wipeDatabase: Boolean) {
        loginView.showLoadingAnimation()
        if (isOnline()) {
            mWipeRequired = wipeDatabase
            val restApi = RestServiceBuilder.createService(RestApi::class.java, username, password)
            val call = restApi.session
            call.enqueue(object : Callback<Session> {
                override fun onResponse(call: Call<Session>, response: Response<Session>) {
                    if (response.isSuccessful) {
                        mLogger.d(response.body().toString())
                        val session = response.body()
                        if (session!!.isAuthenticated) {
                            mOpenMRS.deleteSecretKey()
                            if (wipeDatabase) {
                                mOpenMRS.deleteDatabase(ApplicationConstants.DB_NAME)
                                setData(session.sessionId, url!!, username!!, password!!)
                                mWipeRequired = false
                            }
                            if (authorizationManager.isUserNameOrServerEmpty) {
                                setData(session.sessionId, url!!, username!!, password!!)
                            } else {
                                mOpenMRS.sessionToken = session.sessionId
                                mOpenMRS.setPasswordAndHashedPassword(password)
                            }
                            visitRepository.getVisitType(object : GetVisitTypeCallback {
                                override fun onGetVisitTypeResponse(visitType: VisitType?) {
                                    OpenMRS.getInstance().visitTypeUUID = visitType!!.uuid
                                }

                                override fun onResponse() {
                                    // This method is intentionally empty
                                }

                                override fun onErrorResponse(errorMessage: String) {
                                    OpenMRS.getInstance().visitTypeUUID = ApplicationConstants.DEFAULT_VISIT_TYPE_UUID
                                    loginView.showToast(R.string.failed_fetching_visit_type_error_message, ToastUtil.ToastType.ERROR)
                                }
                            })
                            setLogin(true, url!!)
                            userService.updateUserInformation(username)
                            loginView.userAuthenticated()
                            loginView.finishLoginActivity()
                        } else {
                            loginView.hideLoadingAnimation()
                            loginView.showInvalidLoginOrPasswordSnackbar()
                        }
                    } else {
                        loginView.hideLoadingAnimation()
                        loginView.showToast(response.message(), ToastUtil.ToastType.ERROR)
                    }
                }

                override fun onFailure(call: Call<Session>, t: Throwable) {
                    loginView.hideLoadingAnimation()
                    loginView.showToast(t.message, ToastUtil.ToastType.ERROR)
                }
            })
        } else {
            if (mOpenMRS.isUserLoggedOnline && url == mOpenMRS.lastLoginServerUrl) {
                if (mOpenMRS.username == username && BCrypt.checkpw(password, mOpenMRS.hashedPassword)) {
                    mOpenMRS.deleteSecretKey()
                    mOpenMRS.setPasswordAndHashedPassword(password)
                    mOpenMRS.sessionToken = mOpenMRS.lastSessionToken
                    loginView.showToast(R.string.login_offline_toast_message,
                            ToastUtil.ToastType.NOTICE)
                    loginView.userAuthenticated()
                    loginView.finishLoginActivity()
                } else {
                    loginView.hideLoadingAnimation()
                    loginView.showToast(R.string.auth_failed_dialog_message,
                            ToastUtil.ToastType.ERROR)
                }
            } else if (hasNetwork()) {
                loginView.showToast(R.string.offline_mode_unsupported_in_first_login,
                        ToastUtil.ToastType.ERROR)
                loginView.hideLoadingAnimation()
            } else {
                loginView.showToast(R.string.no_internet_conn_dialog_message,
                        ToastUtil.ToastType.ERROR)
                loginView.hideLoadingAnimation()
            }
        }
    }

    override fun saveLocationsToDatabase(locationList: List<LocationEntity?>?, selectedLocation: String?) {
        mOpenMRS.location = selectedLocation
        locationDAO.deleteAllLocations()
        for (i in locationList!!.indices) {
            locationDAO.saveLocation(locationList[i])
                    .observeOn(Schedulers.io())
                    .subscribe()
        }
    }

    override fun loadLocations(url: String?) {
        loginView.showLocationLoadingAnimation()
        if (hasNetwork()) {
            val locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location"
            val call = restApi.getLocations(locationEndPoint, "Login Location", "full")
            call.enqueue(object : Callback<Results<LocationEntity?>> {
                override fun onResponse(call: Call<Results<LocationEntity?>>, response: Response<Results<LocationEntity?>>) {
                    if (response.isSuccessful) {
                        RestServiceBuilder.changeBaseUrl(url?.trim { it <= ' ' })
                        mOpenMRS.serverUrl = url
                        loginView.initLoginForm(response.body()!!.results, url)
                        loginView.startFormListService()
                        loginView.setLocationErrorOccurred(false)
                    } else {
                        loginView.showInvalidURLSnackbar(R.string.snackbar_server_error)
                        loginView.setLocationErrorOccurred(true)
                        loginView.initLoginForm(ArrayList(), url)
                    }
                    loginView.hideUrlLoadingAnimation()
                }

                override fun onFailure(call: Call<Results<LocationEntity?>>, t: Throwable) {
                    loginView.hideUrlLoadingAnimation()
                    loginView.showInvalidURLSnackbar(t.message)
                    loginView.initLoginForm(ArrayList(), url)
                    loginView.setLocationErrorOccurred(true)
                }
            })
        } else {
            addSubscription(locationDAO.locations
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { locationEntities: List<LocationEntity?> ->
                        if (locationEntities.size > 0) {
                            loginView.initLoginForm(locationEntities, url)
                            loginView.setLocationErrorOccurred(false)
                        } else {
                            loginView.showToast(R.string.no_internet_connection_message, ToastUtil.ToastType.ERROR)
                            loginView.setLocationErrorOccurred(true)
                        }
                        loginView.hideLoadingAnimation()
                    })
        }
    }

    private fun validateLoginFields(username: String, password: String, url: String): Boolean {
        return notEmpty(username) || notEmpty(password) || notEmpty(url)
    }

    private fun setData(sessionToken: String?, url: String, username: String, password: String) {
        mOpenMRS.sessionToken = sessionToken
        mOpenMRS.serverUrl = url
        mOpenMRS.username = username
        mOpenMRS.setPasswordAndHashedPassword(password)
    }

    private fun setLogin(isLogin: Boolean, serverUrl: String) {
        mOpenMRS.isUserLoggedOnline = isLogin
        mOpenMRS.lastLoginServerUrl = serverUrl
    }
}