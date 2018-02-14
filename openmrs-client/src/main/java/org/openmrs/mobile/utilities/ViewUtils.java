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

import android.widget.EditText;

import java.util.regex.Pattern;

public class ViewUtils {

    public static final String ILLEGAL_CHARACTERS = "[$&+:;=\\\\?@#|/'<>^*()%!]";

    public static final String ILLEGAL_ADDRESS_CHARACTERS = "[$&+:;=\\\\?@|<>^%!]";

    public static String getInput(EditText e) {
        if(e.getText() == null) {
            return null;
        } else if (isEmpty(e)) {
            return null;
        } else {
            return e.getText().toString();
        }
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    /**
     * Validate a String for invalid characters
     * @param toValidate    the String to check
     * @return true if String is appropriate
     */
    public static boolean validateText(String toValidate, String invalidCharacters) {
        //TODO: Add more checks to the String

        return !containsCharacters(toValidate, invalidCharacters);
    }

    /**
     * Check if a name contains a character from a string param
     * @param toExamine     the String to check
     * @param characters    characters checked against toExamine
     * @return true if the String contains a character from a sequence of characters
     */
    private static boolean containsCharacters(String toExamine, String characters) {

        if (StringUtils.isBlank(toExamine)) {
            return false;
        }

        Pattern charPattern = Pattern.compile(characters);

        return charPattern.matcher(toExamine).find();
    }
}
