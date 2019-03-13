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

import android.annotation.SuppressLint;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.application.OpenMRS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    private static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String OPEN_MRS_REQUEST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String OPEN_MRS_REQUEST_PATIENT_FORMAT = "yyyy-MM-dd";

    public static final Long ZERO = 0L;

    private DateUtils() {

    }

    public static String convertTime(long time, String dateFormat, TimeZone timeZone) {
        Date date = new Date(time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.setTimeZone(timeZone);
        return format.format(date);
    }

    public static String convertTime(long time, String dateFormat) {
        return convertTime(time, dateFormat, TimeZone.getDefault());
    }

    public static String convertTime(long timestamp, TimeZone timeZone) {
        return convertTime(timestamp, DEFAULT_DATE_FORMAT, timeZone);
    }

    public static String convertTime(long timestamp) {
        return convertTime(timestamp, DEFAULT_DATE_FORMAT, TimeZone.getDefault());
    }

    public static Long convertTime(String dateAsString) {
        if (isValidFormat(DEFAULT_DATE_FORMAT, dateAsString)) {
            return convertTime(dateAsString, DEFAULT_DATE_FORMAT);
        } else {
            return convertTime(dateAsString, OPEN_MRS_RESPONSE_FORMAT);
        }
    }

    public static Long convertTime(String dateAsString, String dateFormat) {
        Long time = null;
        if (StringUtils.notNull(dateAsString)) {
            DateFormat format = new SimpleDateFormat(dateFormat);
            Date formattedDate;
            try {
                formattedDate = parseString(dateAsString, format);
                time = formattedDate.getTime();
            } catch (ParseException e) {
                try {
                    formattedDate = parseString(dateAsString, new SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT));
                    time = formattedDate.getTime();
                } catch (ParseException e1) {
                    OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
                }
            }
        }

        return time;
    }

    private static Date parseString(String dateAsString, DateFormat format) throws ParseException {
        Date formattedDate = null;
        try {
            formattedDate = format.parse(dateAsString);
        } catch (NullPointerException e) {
            OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
        }
        return formattedDate;
    }

    public static DateTime convertTimeString(String dateAsString) {
        DateTime date = null;
        if (StringUtils.notNull(dateAsString)) {
            DateTimeFormatter originalFormat;
            if (dateAsString.length() == OPEN_MRS_REQUEST_PATIENT_FORMAT.length()){
                originalFormat = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);
            } else {
                originalFormat = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_FORMAT);
            }
            date = originalFormat.parseDateTime(dateAsString);
        }
        return date;

    }

    public static String convertTime1(String dateAsString, String dateFormat) {
        if (StringUtils.notNull(dateAsString) && StringUtils.notEmpty(dateAsString)) {
            return convertTime(convertTime(dateAsString), dateFormat, TimeZone.getDefault());
        }
        return dateAsString;
    }

    public static Date getDateFromString(String dateAsString) {
        return getDateFromString(dateAsString, DEFAULT_DATE_FORMAT);
    }

    public static Date getDateFromString(String dateAsString, String dateFormat) {
        Date formattedDate = null;
        if (StringUtils.notNull(dateAsString)) {
            DateFormat format = new SimpleDateFormat(dateFormat);
            try {
                formattedDate = parseString(dateAsString, format);
            } catch (ParseException e) {
                try {
                    formattedDate = parseString(dateAsString, new SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT));
                } catch (ParseException e1) {
                    OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
                }
            }
        }
        return formattedDate;
    }

    public static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
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
    public static boolean validateDate(String dateString, DateTime minDate, DateTime maxDate) {

        if (minDate.isAfter(maxDate)) {
            return false;
        }

        String s = dateString.trim();
        // length must be min d/M/yyyy and max dd/MM/yyyy
        if (s.isEmpty() || s.length() < 8 || s.length() > 10) {
            return false;
        }
        // number of slashes must be 2
        if (!s.contains("/")) {
            return false;
        }

        int numberOfDashes = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '/') {
                numberOfDashes++;
            }
        }
        if (numberOfDashes != 2) {
            return false;
        } else {
            // check day, month and year
            String[] bundledDate = s.split("/");

            int day = Integer.parseInt(bundledDate[0]),
                    month = Integer.parseInt(bundledDate[1]),
                    year = Integer.parseInt(bundledDate[2]);

            int maxDays;
            // Leap year on February -> 29 days
            // Non leap year on February -> 28 days
            // April, June, September, November -> 30 days
            // January, March, May, July, August, October, December -> 31 days
            if (month == 2) {
                if (year % 4 == 0) {
                    maxDays = 29;
                } else {
                    maxDays = 28;
                }
            } else if (day == 31 && month == 4 || month == 6 || month == 9 || month == 11) {
                maxDays = 30;
            } else {
                maxDays = 31;
            }

            int maxMonths = 12;

            if (day <= 0
                    || day > maxDays
                    || month <= 0
                    || month > maxMonths
                    || year <= minDate.getYear()
                    || year > maxDate.getYear()) {
                return false;
            } else {
                // Now we are able to convert the string into a DateTime variable
                DateTimeFormatter formatter = DateTimeFormat.forPattern(DateUtils.DEFAULT_DATE_FORMAT);

                DateTime dob = formatter.parseDateTime(s);

                // Final check to ensure dob is between the minimum and maximum date (up to the second)
                return dob.isAfter(minDate) && dob.isBefore(maxDate);
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
    public static boolean isValidFormat(String format, String dateAsString) {
        Date date = null;
        if (dateAsString != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                date = simpleDateFormat.parse(dateAsString);
                if (!dateAsString.equals(simpleDateFormat.format(date))) {
                    date = null;
                }
            } catch (ParseException exception) {
                OpenMRS.getInstance().getOpenMRSLogger().w("Failed to validate date format :" + dateAsString + " caused by " + exception.toString());
            }
            return date != null;
        }
        return false;
    }
}
