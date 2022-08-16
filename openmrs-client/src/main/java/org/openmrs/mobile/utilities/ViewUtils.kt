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

import android.widget.EditText
import com.hbb20.CountryCodePicker

object ViewUtils {
    @JvmStatic
    fun getInput(e: EditText): String? {
        return if (e.text == null|| isEmpty(e)) {
            null
        } else {
            e.text.toString()
        }
    }

    @JvmStatic
    fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim { it <= ' ' }.isEmpty()
    }

    @JvmStatic
    fun isCountryCodePickerEmpty(countryCodePicker: CountryCodePicker): Boolean {
        return countryCodePicker.selectedCountryName == null
    }
}
