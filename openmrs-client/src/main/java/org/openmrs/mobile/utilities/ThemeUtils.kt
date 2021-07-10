package org.openmrs.mobile.utilities

import android.content.SharedPreferences
import org.openmrs.mobile.application.OpenMRS

object ThemeUtils {

    @JvmStatic
    fun getTheme(): String {
        val defaultSharedPref = OpenMRS.getInstance().openMRSSharedPreferences
        return defaultSharedPref.getString(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, "System Default")!!
    }

    @JvmStatic
    fun setTheme(theme: String) {
        val editor = OpenMRS.getInstance().openMRSSharedPreferences.edit()
        editor.putString(ApplicationConstants.OpenMRSThemes.KEY_DARK_MODE, theme)
        editor.apply()
    }
}