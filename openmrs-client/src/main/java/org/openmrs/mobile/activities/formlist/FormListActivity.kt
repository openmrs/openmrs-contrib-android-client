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
package org.openmrs.mobile.activities.formlist

import android.os.Bundle
import com.openmrs.android_sdk.utilities.ApplicationConstants
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class FormListActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_list)

        supportActionBar?.run {
            elevation = 0f
            setTitle(R.string.action_form_entry)
        }

        var patientId: Long? = null
        intent.extras?.let {
            patientId = it.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE)
        }

        var formListFragment = supportFragmentManager.findFragmentById(R.id.formListContentFrame) as FormListFragment?
        if (formListFragment == null) {
            formListFragment = FormListFragment.newInstance(patientId!!)
        }
        if (!formListFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, formListFragment, R.id.formListContentFrame)
        }
    }
}
