package org.openmrs.mobile.utilities

import org.openmrs.mobile.application.OpenMRS


object LanguageUtils {

    @JvmStatic
    fun getLanguage(): String? {
            val defaultSharedPref = OpenMRS.getInstance().openMRSSharedPreferences
            return defaultSharedPref.getString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, "en")
        }

    @JvmStatic
    fun setLanguage(language : String) {
            val editor = OpenMRS.getInstance().openMRSSharedPreferences.edit()
            editor.putString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, language)
            editor.apply()
        }

}
