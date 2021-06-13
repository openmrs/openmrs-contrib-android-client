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
package com.example.openmrs_android_sdk.utilities

import android.annotation.SuppressLint
import com.example.openmrs_android_sdk.library.OpenmrsAndroid
import com.example.openmrs_android_sdk.utilities.StringUtils.notEmpty
import com.example.openmrs_android_sdk.utilities.StringUtils.notNull
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateUtils {
    const val DEFAULT_DATE_FORMAT = "dd/MM/yyyy"
    const val DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm"
    private const val OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val OPEN_MRS_REQUEST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val OPEN_MRS_REQUEST_PATIENT_FORMAT = "yyyy-MM-dd"
    const val ZERO = 0L
    private val openMRSLogger = OpenmrsAndroid.getOpenMRSLogger();

    @JvmStatic
    fun convertTime(time: Long, dateFormat: String?, timeZone: TimeZone): String {
        val date:Date = Date(time)
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(dateFormat)
        format.timeZone = timeZone
        return format.format(date)
    }

    @JvmStatic
    fun convertTime(time: Long, dateFormat: String?): String {
        return convertTime(time, dateFormat, TimeZone.getDefault())
    }

    @JvmStatic
    fun convertTime(timestamp: Long, timeZone: TimeZone): String {
        return convertTime(timestamp, DEFAULT_DATE_FORMAT, timeZone)
    }

    @JvmStatic
    fun convertTime(timestamp: Long): String {
        return convertTime(timestamp, DEFAULT_DATE_FORMAT, TimeZone.getDefault())
    }

    @JvmStatic
    fun convertTime(dateAsString: String?): Long? {
        return if (isValidFormat(DEFAULT_DATE_FORMAT, dateAsString)) {
            convertTime(dateAsString, DEFAULT_DATE_FORMAT)
        } else {
            convertTime(dateAsString, OPEN_MRS_RESPONSE_FORMAT)
        }
    }

    @JvmStatic
    fun convertTime(dateAsString: String?, dateFormat: String?): Long? {
        var time: Long? = null
        if (notNull(dateAsString)) {
            val format: DateFormat = SimpleDateFormat(dateFormat)
            var formattedDate: Date?
            try {
                formattedDate = parseString(dateAsString, format)
                time = formattedDate!!.time
            } catch (e: ParseException) {
                try {
                    formattedDate = parseString(dateAsString, SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT))
                    time = formattedDate!!.time
                } catch (e1: ParseException) {
                    openMRSLogger.w("Failed to parse date :$dateAsString caused by $e")
                }
            }
        }
        return time
    }

    @JvmStatic
    @Throws(ParseException::class)
    private fun parseString(dateAsString: String?, format: DateFormat): Date? {
        var formattedDate: Date? = null
        try {
            formattedDate = format.parse(dateAsString!!)
        } catch (e: NullPointerException) {
            openMRSLogger.w("Failed to parse date :$dateAsString caused by $e")
        }
        return formattedDate
    }

    @JvmStatic
    fun convertTimeString(dateAsString: String?): DateTime? {
        var date: DateTime? = null
        if (notNull(dateAsString)) {
            val originalFormat: DateTimeFormatter
            originalFormat = if (dateAsString!!.length == OPEN_MRS_REQUEST_PATIENT_FORMAT.length) {
                DateTimeFormat.forPattern(OPEN_MRS_REQUEST_PATIENT_FORMAT)
            } else {
                DateTimeFormat.forPattern(OPEN_MRS_REQUEST_FORMAT)
            }
            date = originalFormat.parseDateTime(dateAsString)
        }
        return date
    }

    @JvmStatic
    fun convertTime1(dateAsString: String, dateFormat: String?): String {
        return if (notNull(dateAsString) && notEmpty(dateAsString)) {
            convertTime(convertTime(dateAsString)!!, dateFormat, TimeZone.getDefault())
        } else dateAsString
    }

    @JvmStatic
    fun getDateFromString(dateAsString: String): Date? {
        return getDateFromString(dateAsString, DEFAULT_DATE_FORMAT)
    }

    @JvmStatic
    fun getDateFromString(dateAsString: String?, dateFormat: String?): Date? {
        var formattedDate: Date? = null
        if (notNull(dateAsString)) {
            val format: DateFormat = SimpleDateFormat(dateFormat)
            try {
                formattedDate = parseString(dateAsString, format)
            } catch (e: ParseException) {
                try {
                    formattedDate = parseString(dateAsString, SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT))
                } catch (e1: ParseException) {
                    openMRSLogger.w("Failed to parse date :$dateAsString caused by $e")
                }
            }
        }
        return formattedDate
    }

    @JvmStatic
    fun getCurrentDateTime(): String {
        val dateFormat: DateFormat = SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT)
        val date = Date()
        return dateFormat.format(date)
    }

    /**
     * Validate a date and make sure it is between the minimum and maximum date
     * Date format is dd/MM/yyyy
     *
     * @param dateString the date to check
     * @param minDate    minimum date allowed
     * @param maxDate    maximum date limit
     * @return true if date is appropriate
     */

    @JvmStatic
    fun validateDate(dateString: String, minDate: DateTime, maxDate: DateTime): Boolean {
        if (minDate.isAfter(maxDate)) {
            return false
        }
        val s = dateString.trim { it <= ' ' }
        // length must be min d/M/yyyy and max dd/MM/yyyy
        if (s.isEmpty() || s.length < 8 || s.length > 10) {
            return false
        }
        // number of slashes must be 2
        if (!s.contains("/")) {
            return false
        }
        var numberOfDashes = 0
        for (i in 0 until s.length) {
            if (s[i] == '/') {
                numberOfDashes++
            }
        }
        return if (numberOfDashes != 2) {
            false
        } else { // check day, month and year
            val bundledDate = s.split("/").toTypedArray()
            val day = bundledDate[0].toInt()
            val month = bundledDate[1].toInt()
            val year = bundledDate[2].toInt()
            val maxDays: Int
            // Leap year on February -> 29 days
            // Non leap year on February -> 28 days
            // April, June, September, November -> 30 days
            // January, March, May, July, August, October, December -> 31 days
            maxDays = if (month == 2) {
                if (year % 4 == 0) {
                    29
                } else {
                    28
                }
            } else if (day == 31 && month == 4 || month == 6 || month == 9 || month == 11) {
                30
            } else {
                31
            }
            val maxMonths = 12
            if (day <= 0 || day > maxDays || month <= 0 || month > maxMonths || year <= minDate.year || year > maxDate.year) {
                false
            } else { // Now we are able to convert the string into a DateTime variable
                val formatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)
                val dob = formatter.parseDateTime(s)
                // Final check to ensure dob is between the minimum and maximum date (up to the second)
                dob.isAfter(minDate) && dob.isBefore(maxDate)
            }
        }
    }

    /**
     * Validates a date against a given format
     *
     * @param format the format to check against
     * @param dateAsString the value of raw date
     * @return true if the given date matches the given format
     */
    @JvmStatic
    fun isValidFormat(format: String?, dateAsString: String?): Boolean {
        var date: Date? = null
        if (dateAsString != null) {
            try {
                val simpleDateFormat = SimpleDateFormat(format)
                date = simpleDateFormat.parse(dateAsString)
                if (dateAsString != simpleDateFormat.format(date!!)) {
                    date = null
                }
            } catch (exception: ParseException) {
                openMRSLogger.w("Failed to validate date format :$dateAsString caused by $exception")
            }
            return date != null
        }
        return false
    }
}