package org.openmrs.mobile.utilities

import com.example.openmrs_android_sdk.utilities.ApplicationConstants
import org.openmrs.mobile.application.OpenMRS

object ThemeUtils {

    @JvmStatic
    fun isDarkModeActivated(): Boolean {
        val defaultSharedPref = OpenMRS.getInstance().getOpenMRSSharedPreferences()
        return defaultSharedPref.getBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, false)
    }

    @JvmStatic
    fun setDarkMode(darkMode: Boolean) {
        val editor = OpenMRS.getInstance().openMRSSharedPreferences.edit()
        editor.putBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, darkMode)
        editor.apply()
    }
}