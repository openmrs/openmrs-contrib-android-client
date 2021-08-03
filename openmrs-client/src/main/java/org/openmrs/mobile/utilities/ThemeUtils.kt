package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.utilities.ApplicationConstants

object ThemeUtils {

    @JvmStatic
    fun isDarkModeActivated(): Boolean {
        val defaultSharedPref = OpenmrsAndroid.getOpenMRSSharedPreferences()
        return defaultSharedPref.getBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, false)
    }

    @JvmStatic
    fun setDarkMode(darkMode: Boolean) {
        val editor = OpenmrsAndroid.getOpenMRSSharedPreferences().edit()
        editor.putBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, darkMode)
        editor.apply()
    }
}