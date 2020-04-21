package org.openmrs.mobile.utilities

import java.util.regex.Pattern


object URLValidator {
    private const val URL_PATTERN = "^((https|http)?://)([\\da-z.-]*)\\.([a-z.]*)([\\w .-]*)*(:([0-9]{2,5}))?((/(\\w*(-\\w+)?))*?)/*$"
    private const val SLASH = "/"
    private const val SPACE = " "
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

    fun trimLastSpace(url: String): String {
        var trimmedUrl = url
        while (trimmedUrl.endsWith(SPACE)) {
            trimmedUrl = trimmedUrl.substring(0, trimmedUrl.lastIndexOf(SPACE))
        }
        return trimmedUrl
    }

    fun toLowerCase(url: String): String {
        return url.toLowerCase()
    }

    fun trimLastSlash(url: String): String {
        var validUrl = url
        while (validUrl.endsWith(SLASH)) {
            validUrl = validUrl.substring(0, validUrl.lastIndexOf(SLASH))
        }
        return validUrl
    }

    fun ensureProtocol(url: String): String {
        val pattern = Regex("\\w+:.*")
        return if (!url.matches(pattern)) {
            "http://$url"
        } else url
    }

    class ValidationResult(val isURLValid: Boolean, val url: String)
}
