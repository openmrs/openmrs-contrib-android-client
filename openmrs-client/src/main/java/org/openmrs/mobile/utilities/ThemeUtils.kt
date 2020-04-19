package org.openmrs.mobile.utilities

import org.openmrs.mobile.application.OpenMRS


object ThemeUtils {
    fun isDarkModeActivated(): Boolean {
            val defaultSharedPref = OpenMRS.getInstance().openMRSSharedPreferences
            return defaultSharedPref.getBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, false)
        }

    fun setDarkMode(darkMode: Boolean) {
        val editor = OpenMRS.getInstance().openMRSSharedPreferences.edit()
        editor.putBoolean(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, darkMode)
        editor.apply()
    }
}
