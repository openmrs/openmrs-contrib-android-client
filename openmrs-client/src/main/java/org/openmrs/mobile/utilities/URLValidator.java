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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URLValidator {
    private static final String URL_PATTERN = "^(https?:\\/\\/){1}([\\da-z\\.-]*)\\.([a-z\\.]*)([\\w \\.-]*)*(:([0-9]{2,5}))?(((\\/(\\w*(\\-\\w+)?)))*?)\\/*$";
    private static final String SLASH = "/";
    private static final String SPACE = " ";

    private URLValidator() {
    }

    public static ValidationResult validate(String url) {
        ValidationResult result;
        Pattern urlPattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = urlPattern.matcher(trimLastSpace(url));
        String validURL = trimLastSpace(url);
        if (matcher.matches()) {
            validURL = trimLastSlash(validURL);
            result = new ValidationResult(true, validURL);
        } else {
            result = new ValidationResult(false, validURL);
        }
        return result;
    }

    public static String trimLastSpace(String url) {
        String trimmedUrl = url;
        while (trimmedUrl.endsWith(SPACE)) {
            trimmedUrl = trimmedUrl.substring(0, trimmedUrl.lastIndexOf(SPACE));
        }
        return trimmedUrl;
    }

    public static String trimLastSlash(String url) {
        String validUrl = url;
        while (validUrl.endsWith(SLASH)) {
            validUrl = validUrl.substring(0, validUrl.lastIndexOf(SLASH));
        }
        return validUrl;
    }

    public static class ValidationResult {
        private final boolean isURLValid;
        private final String url;

        public ValidationResult(boolean isValid, String url) {
            this.isURLValid = isValid;
            this.url = url;
        }

        public boolean isURLValid() {
            return isURLValid;
        }

        public String getUrl() {
            return url;
        }

    }
}
