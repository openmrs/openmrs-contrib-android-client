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

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.utilities.ApplicationConstants

object LanguageUtils {

    @JvmStatic
    fun getLanguage(): String? {
        val defaultSharedPref = OpenmrsAndroid.getOpenMRSSharedPreferences()
        return defaultSharedPref.getString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, "en")
    }

    @JvmStatic
    fun setLanguage(language: String?) {
        val editor = OpenmrsAndroid.getOpenMRSSharedPreferences().edit()
        editor.putString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, language)
        editor.apply()
    }

}