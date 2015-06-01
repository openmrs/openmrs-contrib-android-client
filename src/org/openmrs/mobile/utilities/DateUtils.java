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

import org.openmrs.mobile.application.OpenMRS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String OPEN_MRS_REQUEST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final Long ZERO = 0L;

    private DateUtils() {

    }

    public static String convertTime(long time, String dateFormat, TimeZone timeZone) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
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
        return convertTime(dateAsString, OPEN_MRS_RESPONSE_FORMAT);
    }

    public static Long convertTime(String dateAsString, String dateFormat) {
        Long time = null;
        if (StringUtils.notNull(dateAsString)) {
            DateFormat format = new SimpleDateFormat(dateFormat);
            Date formattedDate;
            try {
                formattedDate = format.parse(dateAsString);
                time = formattedDate.getTime();
            } catch (ParseException e) {
                OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
            } catch (NullPointerException e) {
                OpenMRS.getInstance().getOpenMRSLogger().w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
            }
        }
        return time;
    }
}
