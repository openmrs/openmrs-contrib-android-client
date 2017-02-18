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
import android.text.TextWatcher;
import android.widget.EditText;

import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class PatientBirthDateValidatorWatcherTest extends InstrumentationTestCase {

    private EditText mMonthEditText;
    private EditText mYearEditText;

    @Override
    public void setUp() {
        EditText birthdateEditText = new EditText(this.getInstrumentation().getContext());
        mMonthEditText = new EditText(this.getInstrumentation().getContext());
        mYearEditText = new EditText(this.getInstrumentation().getContext());

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(birthdateEditText, mMonthEditText, mYearEditText);

        mMonthEditText.addTextChangedListener(textWatcher);
        mYearEditText.addTextChangedListener(textWatcher);
    }

    public void test_shouldConvertMonthsToYears() {
        final String monthsInTenYears = "120";
        final String tenYears = "10";

        mYearEditText.getText().clear();
        mMonthEditText.setText(monthsInTenYears);
        assertEquals(mYearEditText.getText().toString(), tenYears);
    }

    public void test_shouldWipeYearsAndMonthInputWhenYearInputWasTooBig() {
        final String yearsLessThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE / 2);
        final String yearsMoreThanMaxAllowed = String.valueOf(ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE * 2);

        mYearEditText.setText(yearsLessThanMaxAllowed);
        assertEquals(mYearEditText.getText().toString(), yearsLessThanMaxAllowed);
        mYearEditText.setText(yearsMoreThanMaxAllowed);
        assertEquals(mYearEditText.getText().toString(), ApplicationConstants.EMPTY_STRING);
    }

}
