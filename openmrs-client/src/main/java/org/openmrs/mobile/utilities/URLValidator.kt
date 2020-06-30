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

import java.util.regex.Pattern

object URLValidator {
    private const val URL_PATTERN = "^((https|http)?://)([\\da-z.-]*)\\.([a-z.]*)([\\w .-]*)*(:([0-9]{2,5}))?((/(\\w*(-\\w+)?))*?)/*$"
    private const val SLASH = "/"
    private const val SPACE = " "
    private  val pattern = Regex("\\w+:.*")

    @JvmStatic
    fun validate(url: String): ValidationResult {
        val result: ValidationResult
        val ensuredUrl = ensureProtocol(url)
        val urlPattern = Pattern.compile(URL_PATTERN)
        val matcher = urlPattern.matcher(trimLastSpace(ensuredUrl))
        var validURL = trimLastSpace(ensuredUrl)
        if (matcher.matches()) {
            validURL = trimLastSlash(validURL)
            validURL = toLowerCase(validURL)
            result = ValidationResult(true, validURL)
        } else {
            result = ValidationResult(false, validURL)
        }
        return result
    }

    @JvmStatic
    fun trimLastSpace(url: String): String {
        var trimmedUrl = url
        while (trimmedUrl.endsWith(SPACE)) {
            trimmedUrl = trimmedUrl.substring(0, trimmedUrl.lastIndexOf(SPACE))
        }
        return trimmedUrl
    }

    @JvmStatic
    fun toLowerCase(url: String): String {
        return url.toLowerCase()
    }

    @JvmStatic
    fun trimLastSlash(url: String): String {
        var validUrl = url
        while (validUrl.endsWith(SLASH)) {
            validUrl = validUrl.substring(0, validUrl.lastIndexOf(SLASH))
        }
        return validUrl
    }

    @JvmStatic
    fun ensureProtocol(url: String): String {

        return if (!url.matches(pattern)) {
            "http://$url"
        } else url
    }

    class ValidationResult(val isURLValid: Boolean, val url: String)
}