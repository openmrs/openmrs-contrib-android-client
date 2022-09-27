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
package org.openmrs.mobile.activities.formadmission

import android.os.Bundle
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class FormAdmissionActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_admission)

        supportActionBar?.run {
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.admission)
        }

        var patientID: Long? = null
        var encounterType: String? = null
        var formName: String? = null
        intent.extras?.let {
            patientID = it.getLong(PATIENT_ID_BUNDLE)
            encounterType = it.getString(ENCOUNTERTYPE)
            formName = it.getString(FORM_NAME)
        }

        var formAdmissionFragment = supportFragmentManager.findFragmentById(R.id.admissionFormContentFrame) as FormAdmissionFragment?
        if (formAdmissionFragment == null) {
            formAdmissionFragment = FormAdmissionFragment.newInstance(patientID!!, encounterType!!, formName!!)
        }
        if (!formAdmissionFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, formAdmissionFragment, R.id.admissionFormContentFrame)
        }
    }
}
