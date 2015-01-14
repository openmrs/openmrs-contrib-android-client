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

package org.openmrs.mobile.test.unit;

import android.test.InstrumentationTestCase;

import org.openmrs.mobile.utilities.URLValidator;

public class URLValidatorTest extends InstrumentationTestCase {
    private static final String INVALID_URL_1;
    private static final String INVALID_URL_2;
    private static final String VALID_URL_1;
    private static final String VALID_URL_1_TRIMMED;
    private static final String VALID_URL_2;
    private static final String VALID_URL_3;

    @Override
    public void setUp() throws java.lang.Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
    }

    static {
        INVALID_URL_1 = "http://";
        INVALID_URL_2 = "http://demo.openmrs.org/openmrsl.na15.force.com";
        VALID_URL_1 = "http://demo.openmrs.org/openmrs/";
        VALID_URL_1_TRIMMED = "http://demo.openmrs.org/openmrs";
        VALID_URL_2 = "https://demo.openmrs.org:8081/openmrs-standalone";
        VALID_URL_3 = "http://demo.openmrs.org/openmrs/ ";
    }

    public void testURLValidator() {
        URLValidator.ValidationResult result;
        URLValidator.ValidationResult expected;

        result = URLValidator.validate(INVALID_URL_1);
        expected = new URLValidator.ValidationResult(false, INVALID_URL_1);
        assertEquals(expected.isURLValid(), result.isURLValid());
        assertEquals(expected.getUrl(), result.getUrl());

        result = URLValidator.validate(INVALID_URL_2);
        expected = new URLValidator.ValidationResult(false, INVALID_URL_2);
        assertEquals(expected.isURLValid(), result.isURLValid());
        assertEquals(expected.getUrl(), result.getUrl());


        result = URLValidator.validate(VALID_URL_1);
        expected = new URLValidator.ValidationResult(true, VALID_URL_1_TRIMMED);
        assertEquals(expected.isURLValid(), result.isURLValid());
        assertEquals(expected.getUrl(), result.getUrl());

        result = URLValidator.validate(VALID_URL_2);
        expected = new URLValidator.ValidationResult(true, VALID_URL_2);
        assertEquals(expected.isURLValid(), result.isURLValid());
        assertEquals(expected.getUrl(), result.getUrl());

        result = URLValidator.validate(VALID_URL_3);
        expected = new URLValidator.ValidationResult(true, VALID_URL_1_TRIMMED);
        assertEquals(expected.isURLValid(), result.isURLValid());
        assertEquals(expected.getUrl(), result.getUrl());
    }
}
