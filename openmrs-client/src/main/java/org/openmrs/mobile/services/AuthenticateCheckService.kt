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
package org.openmrs.mobile.services

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.openmrs.mobile.R
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.models.Session
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.NetworkUtils.hasNetwork
import org.openmrs.mobile.utilities.ToastUtil.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AuthenticateCheckService : Service() {
    private val mBinder: IBinder = SocketServerBinder()
    private var mRunning = false
    private val mOpenMRS = OpenMRS.getInstance()
    override fun onCreate() {
        super.onCreate()
        val mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                if (mRunning) {
                    val username = mOpenMRS.username
                    val password = mOpenMRS.password
                    if (username != ApplicationConstants.EMPTY_STRING &&
                            password != ApplicationConstants.EMPTY_STRING) {
                        Log.e("Service Task ", "Running")
                        authenticateCheck(username, password)
                    }
                }
            }
        }, 10000, 100000)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mRunning = true
        return START_NOT_STICKY
    }

    override fun onBind(arg0: Intent): IBinder {
        mRunning = true
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        mRunning = false
        return super.onUnbind(intent)
    }

    private fun authenticateCheck(username: String, password: String) {
        if (hasNetwork()) {
            val restApi = RestServiceBuilder.createService(RestApi::class.java, username, password)
            val call = restApi.session
            call.enqueue(object : Callback<Session?> {
                override fun onResponse(call: Call<Session?>, response: Response<Session?>) {
                    if (response.isSuccessful) {
                        val session = response.body()
                        if (session!!.isAuthenticated) {
                            Log.e("Service Task ", "user authenticated")
                        } else {
                            Log.e("Service Task ", "User Credentials Changed")
                            if (isForeground(OpenMRS.getInstance().packageName)) {
                                val broadcastIntent = Intent()
                                broadcastIntent.action = ApplicationConstants.BroadcastActions.AUTHENTICATION_CHECK_BROADCAST_ACTION
                                sendBroadcast(broadcastIntent)
                            } else {
                                AppDatabase.getDatabase(applicationContext).close()
                                mOpenMRS.clearUserPreferencesData()
                                mOpenMRS.clearCurrentLoggedInUserInfo()
                            }
                        }
                    } else {
                        error(getString(R.string.authenticate_check_service_error_response_message))
                    }
                }

                override fun onFailure(call: Call<Session?>, t: Throwable) {
                    error(getString(R.string.authenticate_service_error_message))
                }
            })
        } else {
            Log.e("Service Task ", "No Network")
        }
    }

    fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return componentInfo!!.packageName == myPackage
    }

    inner class SocketServerBinder : Binder() {
        val service: AuthenticateCheckService
            get() = this@AuthenticateCheckService
    }
}