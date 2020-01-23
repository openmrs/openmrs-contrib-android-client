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

package org.openmrs.mobile.utilities;

import android.content.SharedPreferences;
import org.openmrs.mobile.application.OpenMRS;

public class LanguageUtils
{
    public static String getLanguage() {
        SharedPreferences defaultSharedPref = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        String selecetedLang = defaultSharedPref.getString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, "en");
        return selecetedLang;
    }

    public static void setLanguage(String language) {
        SharedPreferences.Editor editor = OpenMRS.getInstance().getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.OpenMRSlanguage.KEY_LANGUAGE_MODE, language);
        editor.apply();
    }
}
