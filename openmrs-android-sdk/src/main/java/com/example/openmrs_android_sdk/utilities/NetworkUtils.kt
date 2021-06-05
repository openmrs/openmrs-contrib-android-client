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
package com.example.openmrs_android_sdk.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.example.openmrs_android_sdk.library.OpenmrsAndroid

object NetworkUtils {
    @JvmStatic
    fun hasNetwork(): Boolean {
        val connectivityManager = OpenmrsAndroid.getInstance()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    @JvmStatic
    fun isOnline(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(OpenmrsAndroid.getInstance())
        val toggle = prefs.getBoolean("sync", true)
        return if (toggle) {
            val connectivityManager = OpenmrsAndroid.getInstance()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
            return if (isConnected) true
            else {
                val editor = prefs.edit()
                editor.putBoolean("sync", false)
                editor.apply()
                false
            }
        } else false
    }
}