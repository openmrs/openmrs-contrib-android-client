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
import android.view.Menu
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import com.openmrs.android_sdk.utilities.ApplicationConstants

class FormAdmissionActivity : ACBaseActivity() {
    var patientID: Long? = null
    var encounterType: String? = null
    var formName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_admission)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.admission)
        }
        val bundle = intent.extras
        if (bundle != null) {
            patientID = bundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE)
            encounterType = bundle[ApplicationConstants.BundleKeys.ENCOUNTERTYPE] as String?
            formName = bundle[ApplicationConstants.BundleKeys.FORM_NAME] as String?
        }
        var formAdmissionFragment = supportFragmentManager.findFragmentById(R.id.admissionFormContentFrame) as FormAdmissionFragment?
        if (formAdmissionFragment == null) {
            formAdmissionFragment = FormAdmissionFragment.newInstance()
        }
        if (!formAdmissionFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    formAdmissionFragment, R.id.admissionFormContentFrame)
        }
        FormAdmissionPresenter(formAdmissionFragment, patientID, encounterType, formName, applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        return true
    }

    companion object {
        fun newInstance(): FormAdmissionFragment {
            return FormAdmissionFragment()
        }
    }
}