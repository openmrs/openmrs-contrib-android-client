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
import com.openmrs.android_sdk.utilities.StringUtils
import com.hbb20.CountryCodePicker
import java.util.regex.Pattern

object ViewUtils {
    const val ILLEGAL_CHARACTERS = "[$&+:;=\\\\?@#|/'<>^*()%!]"
    const val ILLEGAL_ADDRESS_CHARACTERS = "[$&+:;=\\\\?@|<>^%!]"
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

    /**
     * Validate a String for invalid characters
     *
     * @param toValidate the String to check @nullable
     * @return true if String is appropriate
     */
    @JvmStatic
    fun validateText(toValidate: String?, invalidCharacters: String): Boolean {
        //TODO: Add more checks to the String
        return !containsCharacters(toValidate, invalidCharacters)
    }

    /**
     * Check if a name contains a character from a string param
     *
     * @param toExamine the String to check
     * @param characters characters checked against toExamine
     * @return true if the String contains a character from a sequence of characters
     */
    private fun containsCharacters(toExamine: String?, characters: String): Boolean {
        if (StringUtils.isBlank(toExamine)) {
            return false
        }
        val charPattern = Pattern.compile(characters)
        return charPattern.matcher(toExamine as CharSequence).find()
    }
}