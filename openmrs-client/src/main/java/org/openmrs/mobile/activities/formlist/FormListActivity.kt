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
import android.view.Menu
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import com.openmrs.android_sdk.utilities.ApplicationConstants

class FormListActivity : ACBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_form_list)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setTitle(R.string.action_form_entry)
        }

        var formListFragment = supportFragmentManager.findFragmentById(R.id.formListContentFrame) as FormListFragment?
        if (formListFragment == null) {
            formListFragment = FormListFragment.newInstance()
        }
        if (!formListFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    formListFragment, R.id.formListContentFrame)
        }
        val bundle = intent.extras
        var mPatientID: Long? = null
        if (bundle != null) {
            mPatientID = bundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE)
        }

        FormListPresenter(formListFragment, mPatientID!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        return true
    }
}