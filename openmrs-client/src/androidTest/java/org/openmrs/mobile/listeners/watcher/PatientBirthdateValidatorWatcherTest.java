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

package org.openmrs.mobile.listeners.watcher;

import android.content.Context;
import android.text.TextWatcher;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.utilities.ApplicationConstants;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientBirthdateValidatorWatcherTest {

    private EditText mMonthEditText;
    private EditText mYearEditText;

    @Before
    public void setUp() {
        Context context = getInstrumentation().getContext();
        EditText birthdateEditText = new EditText(context);
        mMonthEditText = new EditText(context);
        mYearEditText = new EditText(context);

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(
                birthdateEditText, mMonthEditText, mYearEditText);

        mMonthEditText.addTextChangedListener(textWatcher);
        mYearEditText.addTextChangedListener(textWatcher);
    }

    @Test
    public void test_shouldConvertMonthsToYears() {
        final String monthsInTenYears = "120";
        final String tenYears = "10";

        mYearEditText.getText().clear();
        mMonthEditText.setText(monthsInTenYears);
        assertEquals(mYearEditText.getText().toString(), tenYears);
    }

    @Test
    public void test_shouldWipeYearsAndMonthInputWhenYearInputWasTooBig() {
        final String yearsLessThanMaxAllowed = String.valueOf(
                ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE / 2);
        final String yearsMoreThanMaxAllowed = String.valueOf(
                ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE * 2);

        getInstrumentation().runOnMainSync(() -> {
            mYearEditText.setText(yearsLessThanMaxAllowed);
            assertEquals(mYearEditText.getText().toString(), yearsLessThanMaxAllowed);
            mYearEditText.setText(yearsMoreThanMaxAllowed);
            assertEquals(mYearEditText.getText().toString(), ApplicationConstants.EMPTY_STRING);

        });
    }
}