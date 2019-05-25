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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.openmrs.mobile.application.OpenMRS;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenMRS.class)
public class DateUtilsTest {

    private static final String TEST_TIMEZONE;
    private static final String INITIAL_DATA_CUSTOM_TIMEZONE;
    private static final String INITIAL_DATA_CUSTOM_FORMAT;
    private static final String INITIAL_DATA_CUSTOM_FORMAT_AND_TIMEZONE;
    private static final String INITIAL_DATA_TIME_STRING;
    private static final String INITIAL_DATA_CONVERT_TIME_1;
    private static final String INITIAL_DATA_DATE_FROM_STRING;
    private static final String EXPECTED_DATA_CUSTOM_TIMEZONE;
    private static final String EXPECTED_DATA_CUSTOM_FORMAT_AND_TIMEZONE;
    private static final String EXPECTED_DATA_TIME_STRING;
    private static final String EXPECTED_DATA_CONVERT_TIME_1;
    private static final String EXPECTED_DATA_DATE_FROM_STRING;
    private static final String EXPECTED_DATA_CUSTOM_FORMAT;
    private static final String EXPECTED_DATA_DEFAULT_DATE_FORMAT_1;
    private static final String EXPECTED_DATA_DEFAULT_DATE_FORMAT_2;
    private static final String EXPECTED_DATA_DEFAULT_DATE_FORMAT_3;
    private static final String EXPECTED_DATA_IS_VALID_FORMAT_1;
    private static final String EXPECTED_DATA_IS_VALID_FORMAT_2;
    private static final String EXPECTED_DATA_IS_INVALID_FORMAT_3;
    private static final String INVALID_DATA_3;
    private static final String INVALID_DATA_4;
    private static final String EXPECTED_LONG_3;
    private static final String INVALID_DATA_5;
    private static final String INVALID_DATA_6;
    private static final String INVALID_DATA_7;
    private static final String INVALID_DATA_8;
    private static final String INVALID_DATA_9;
    private static final String INVALID_DATA_10;
    private static final String INVALID_DATA_11;
    private static final String INVALID_DATA_12;
    private static final String INVALID_DATA_13;
    private static final String INVALID_DATA_14;
    private static final String INVALID_DATA_15;

    static {
        TEST_TIMEZONE = "PST8PDT";
        INITIAL_DATA_CUSTOM_TIMEZONE = "1967-03-26T00:00:00.000+0200";
        EXPECTED_DATA_CUSTOM_TIMEZONE = "26/03/1967";
        INITIAL_DATA_CUSTOM_FORMAT = "1990-03-24T00:00";
        EXPECTED_DATA_CUSTOM_FORMAT = "24/03/1990";
        INITIAL_DATA_CUSTOM_FORMAT_AND_TIMEZONE = "1970-03-26T00:00:00.000+0200";
        EXPECTED_DATA_CUSTOM_FORMAT_AND_TIMEZONE = "1970-03-26T00:00";
        INITIAL_DATA_TIME_STRING = "1975-03-26T00:00:00.000+0200";
        EXPECTED_DATA_TIME_STRING = "1975-03-25T15:00:00.000-0700";
        INITIAL_DATA_CONVERT_TIME_1 = "1980-03-26T00:00:00.000+0200";
        EXPECTED_DATA_CONVERT_TIME_1 = "25/03/1980";
        INITIAL_DATA_DATE_FROM_STRING = "1980-03-26T00:00:00.000+0200";
        EXPECTED_DATA_DATE_FROM_STRING = "26/03/1980";

        EXPECTED_DATA_DEFAULT_DATE_FORMAT_1 = "26/03/1967";
        EXPECTED_DATA_DEFAULT_DATE_FORMAT_2 = "24/03/1990";
        EXPECTED_DATA_DEFAULT_DATE_FORMAT_3 = "21/6/1983";
        EXPECTED_DATA_IS_VALID_FORMAT_1 = "26/03/1971";
        EXPECTED_DATA_IS_VALID_FORMAT_2 = "1988-03-26";
        EXPECTED_DATA_IS_INVALID_FORMAT_3 = "1977-03-26T00:00";

        INVALID_DATA_3 = "09-07-1697T00:00";
        EXPECTED_LONG_3 = "598597200000";
        INVALID_DATA_4 = "1988/12/20";

        INVALID_DATA_5 = "2";
        INVALID_DATA_6 = "3/11";
        INVALID_DATA_7 = "1992-05-07";
        INVALID_DATA_8 = "2/1/2040";
        INVALID_DATA_9 = "26/03/1967";
        INVALID_DATA_10 = "29/02/1983";
        INVALID_DATA_11 = "30/02/1996";
        INVALID_DATA_12 = "31/04/1996";
        INVALID_DATA_13 = "32/05/1996";
        INVALID_DATA_14 = "-1/02/1995";
        INVALID_DATA_15 = "4/-2/1993";
    }

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OpenMRS openMRS;

    @Rule
    public TimeZoneRule timeZoneRule = new TimeZoneRule(TimeZone.getTimeZone("PST8PDT"));

    @Before
    public void setUp() {
        PowerMockito.mockStatic(OpenMRS.class);
        when(OpenMRS.getInstance()).thenReturn(openMRS);
        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone(TEST_TIMEZONE));
    }

    @Test
    public void testTimeConversion() {
        Long stringToLongResult;
        String longToStringResult;
        DateTime dateTime;
        String nullDateAsString = null;
        DateTimeFormatter formatter = DateTimeFormat
                .forPattern(DateUtils.OPEN_MRS_REQUEST_FORMAT)
                .withZone(DateTimeZone.forID(TEST_TIMEZONE));

        stringToLongResult = DateUtils.convertTime(INITIAL_DATA_CUSTOM_TIMEZONE);
        longToStringResult = DateUtils.convertTime(stringToLongResult, TimeZone.getTimeZone("GMT+02:00"));
        assertEquals(EXPECTED_DATA_CUSTOM_TIMEZONE, longToStringResult);

        stringToLongResult = DateUtils.convertTime(INITIAL_DATA_CUSTOM_FORMAT, "yyyy-MM-dd'T'HH:mm");
        longToStringResult = DateUtils.convertTime(stringToLongResult);
        assertEquals(EXPECTED_DATA_CUSTOM_FORMAT, longToStringResult);

        stringToLongResult = DateUtils.convertTime(INITIAL_DATA_CUSTOM_FORMAT_AND_TIMEZONE);
        longToStringResult = DateUtils.convertTime(stringToLongResult, "yyyy-MM-dd'T'HH:mm", TimeZone.getTimeZone("GMT+02:00"));
        assertEquals(EXPECTED_DATA_CUSTOM_FORMAT_AND_TIMEZONE, longToStringResult);

        dateTime = DateUtils.convertTimeString(INITIAL_DATA_TIME_STRING);
        assertEquals(EXPECTED_DATA_TIME_STRING, formatter.print(dateTime));

        longToStringResult = DateUtils.convertTime1(INITIAL_DATA_CONVERT_TIME_1, DateUtils.DEFAULT_DATE_FORMAT);
        assertEquals(EXPECTED_DATA_CONVERT_TIME_1, longToStringResult);

        stringToLongResult = DateUtils.convertTime(INVALID_DATA_3);
        assertNotSame(EXPECTED_LONG_3, String.valueOf(stringToLongResult));

        stringToLongResult = DateUtils.convertTime(INVALID_DATA_4);
        assertNull(stringToLongResult);

        dateTime = DateUtils.convertTimeString(nullDateAsString);
        assertNull(dateTime);
    }

    @Test
    public void testDateValidation() {
        Date date;
        String nullDateAsString = null;

        DateTime date1900 = DateTimeFormat
                .forPattern(DateUtils.DEFAULT_DATE_FORMAT).parseDateTime("1/1/1900");
        DateTime date1950 = DateTimeFormat
                .forPattern(DateUtils.DEFAULT_DATE_FORMAT).parseDateTime("1/1/1950");
        DateTime date2000 = DateTimeFormat
                .forPattern(DateUtils.DEFAULT_DATE_FORMAT).parseDateTime("1/1/2000");

        assertTrue(DateUtils.validateDate(EXPECTED_DATA_DEFAULT_DATE_FORMAT_1, date1900, DateTime.now()));
        assertTrue(DateUtils.validateDate(EXPECTED_DATA_DEFAULT_DATE_FORMAT_2, date1900, date2000));
        assertTrue(DateUtils.validateDate(EXPECTED_DATA_DEFAULT_DATE_FORMAT_3, date1900, DateTime.now()));

        // Incorrectly formatted dates
        assertFalse(DateUtils.validateDate(INVALID_DATA_5, date1900, DateTime.now()));
        assertFalse(DateUtils.validateDate(INVALID_DATA_6, date1900, DateTime.now()));
        assertFalse(DateUtils.validateDate(INVALID_DATA_7, date1900, DateTime.now()));
        // Dates that are before minimum date
        assertFalse(DateUtils.validateDate(INVALID_DATA_8, date1900, DateTime.now()));
        // Dates after the maximum date
        assertFalse(DateUtils.validateDate(INVALID_DATA_9, date1900, date1950));
        assertFalse(DateUtils.validateDate(INVALID_DATA_8, date1900, date2000));
        // Dates where months have more than valid days
        assertFalse(DateUtils.validateDate(INVALID_DATA_10, date1900, DateTime.now()));
        assertFalse(DateUtils.validateDate(INVALID_DATA_11, date1900, DateTime.now()));
        assertFalse(DateUtils.validateDate(INVALID_DATA_12, date1900, DateTime.now()));
        assertFalse(DateUtils.validateDate(INVALID_DATA_13, date1900, DateTime.now()));
        // Dates where day or month is negative
        assertFalse(DateUtils.validateDate(INVALID_DATA_14, date1900, date2000));
        assertFalse(DateUtils.validateDate(INVALID_DATA_15, date1900, date2000));

        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_FORMAT, Locale.US);

        date = DateUtils.getDateFromString(INITIAL_DATA_DATE_FROM_STRING);
        assertEquals(EXPECTED_DATA_DATE_FROM_STRING, formatter.format(date));

        date = DateUtils.getDateFromString(INITIAL_DATA_DATE_FROM_STRING, DateUtils.DATE_WITH_TIME_FORMAT);
        assertEquals(EXPECTED_DATA_DATE_FROM_STRING, formatter.format(date));

        // Null Date String
        date = DateUtils.getDateFromString(nullDateAsString, DateUtils.DATE_WITH_TIME_FORMAT);
        assertNull(date);

        // Correctly formatted dates
        assertTrue(DateUtils.isValidFormat(DateUtils.DEFAULT_DATE_FORMAT, EXPECTED_DATA_IS_VALID_FORMAT_1));
        assertTrue(DateUtils.isValidFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT, EXPECTED_DATA_IS_VALID_FORMAT_2));

        // Differently formatted dates
        assertFalse(DateUtils.isValidFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT, EXPECTED_DATA_IS_VALID_FORMAT_1));
        assertFalse(DateUtils.isValidFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT, EXPECTED_DATA_IS_INVALID_FORMAT_3));
    }
}