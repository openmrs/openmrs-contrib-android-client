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
package org.openmrs.mobile.listeners.watcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.StringUtils.notEmpty

class PatientBirthdateValidatorWatcher(private val eddob: EditText,
                                       private val edmonth: EditText,
                                       private val edyr: EditText) : TextWatcher {

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // This method is intentionally empty
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // This method is intentionally empty
    }

    override fun afterTextChanged(editable: Editable) {
        eddob.text.clear()
        if (editable.length > 3) {
            //string resource added "input_too_big_error_message"
            error("Input is too big")
            edmonth.text.clear()
            edyr.text.clear()
        } else {
            if (notEmpty(editable.toString()) && editable.toString().toInt() > ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE) {
                //string resource added "patient_age_out_of_bounds_error_message"
                error("Patient's age must be between 0 and " + ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE)
                edmonth.text.clear()
                edyr.text.clear()
            }
        }
        var monthValue = 0
        if (notEmpty(edmonth.text.toString())) {
            monthValue = edmonth.text.toString().toInt()
        }
        if (monthValue >= 12) {
            edmonth.setText((monthValue % 12).toString())
            edyr.setText((monthValue / 12).toString())
        }
    }

}