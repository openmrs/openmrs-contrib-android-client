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
package org.openmrs.mobile.activities.addeditallergy

import android.os.Bundle
import android.view.MenuItem
import com.openmrs.android_sdk.utilities.ApplicationConstants
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class AddEditAllergyActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allergy_info)

        supportActionBar?.run {
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
        }

        var patientID: String? = null
        var allergyUuid: String? = null
        intent.extras?.let {
            patientID = it.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE)
            allergyUuid = it.getString(ApplicationConstants.BundleKeys.ALLERGY_UUID)
        }
        var addEditAllergyFragment = supportFragmentManager.findFragmentById(R.id.allergyFrame) as AddEditAllergyFragment?
        if (addEditAllergyFragment == null) {
            addEditAllergyFragment = AddEditAllergyFragment.newInstance(patientID!!, allergyUuid)
        }
        if (!addEditAllergyFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, addEditAllergyFragment, R.id.allergyFrame)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        else super.onOptionsItemSelected(item)
        return true
    }
}
