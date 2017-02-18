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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class PatientBirthdateValidatorWatcher implements TextWatcher{

    private EditText eddob;
    private EditText edmonth;
    private EditText edyr;

    public PatientBirthdateValidatorWatcher(EditText dateOfBirthEditText, EditText monthEditText, EditText yearsEditText) {
        this.eddob = dateOfBirthEditText;
        this.edmonth = monthEditText;
        this.edyr = yearsEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // This method is intentionally empty
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // This method is intentionally empty
    }

    @Override
    public void afterTextChanged(Editable editable) {
        eddob.getText().clear();
        if (editable.length() > 3) {
            ToastUtil.error("Input is too big");
            edmonth.getText().clear();
            edyr.getText().clear();
        }
        else {
            if (StringUtils.notEmpty(editable.toString()) && Integer.parseInt(editable.toString()) > ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE) {
                ToastUtil.error("Patient's age must be between 0 and " + ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE);
                edmonth.getText().clear();
                edyr.getText().clear();
            }
        }

        int monthValue = 0;
        if (StringUtils.notEmpty(edmonth.getText().toString())) {
            monthValue = Integer.parseInt(edmonth.getText().toString());
        }
        if (monthValue >= 12) {
            edmonth.setText(String.valueOf(monthValue % 12));
            edyr.setText(String.valueOf(monthValue / 12));
        }
    }
}
