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