package org.openmrs.mobile.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import org.openmrs.mobile.application.OpenMRS


object NetworkUtils {
    fun hasNetwork(): Boolean {
        val connectivityManager = OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun isOnline(): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance())
            val toggle = prefs.getBoolean("sync", true)
            return if (toggle) {
                val connectivityManager = OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
