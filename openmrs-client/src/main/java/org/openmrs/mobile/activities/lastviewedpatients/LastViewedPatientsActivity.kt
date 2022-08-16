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
package org.openmrs.mobile.activities.lastviewedpatients

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.databinding.ActivityFindLastViewedPatientsBinding

@AndroidEntryPoint
class LastViewedPatientsActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindLastViewedPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(supportActionBar!!) {
            elevation = 0f
            title = getString(R.string.action_download_patients)
            setDisplayHomeAsUpEnabled(true)
        }

        // Create fragment
        var lastViewedPatientsFragment = supportFragmentManager.findFragmentById(R.id.lastPatientsContentFrame) as LastViewedPatientsFragment?
        if (lastViewedPatientsFragment == null) {
            lastViewedPatientsFragment = LastViewedPatientsFragment.newInstance()
        }
        if (!lastViewedPatientsFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, lastViewedPatientsFragment, R.id.lastPatientsContentFrame)
        }
    }
}
