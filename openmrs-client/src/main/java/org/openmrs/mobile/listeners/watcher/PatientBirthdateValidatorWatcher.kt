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

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.openmrs.android_sdk.utilities.ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE
import com.openmrs.android_sdk.utilities.ToastUtil
import org.openmrs.mobile.R

class PatientBirthdateValidatorWatcher(private val context: Context,
                                       private val edDob: EditText,
                                       private val edMonth: EditText,
                                       private val edYear: EditText) : TextWatcher {

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // No usage for this method
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        // No usage for this method
    }

    override fun afterTextChanged(editable: Editable) {
        if (editable.isEmpty()) return

        // Years or months estimation is being used instead of full date of birth
        edDob.text.clear()

        if (editable.isNotEmpty() && editable.toString().toInt() > MAX_PATIENT_AGE) {
            ToastUtil.error(
                    String.format(
                            context.getString(R.string.age_out_of_bounds_message),
                            MAX_PATIENT_AGE
                    )
            )
            edMonth.text.clear()
            edYear.text.clear()
        }

        // Convert estimated number of months (if >= 12) into years
        if (edMonth.text.isNotEmpty()) {
            val monthValue = edMonth.text.toString().toInt()
            if (monthValue >= 12) {
                edMonth.setText((monthValue % 12).toString())
                edYear.setText((monthValue / 12).toString())
            }
        }
    }

}
