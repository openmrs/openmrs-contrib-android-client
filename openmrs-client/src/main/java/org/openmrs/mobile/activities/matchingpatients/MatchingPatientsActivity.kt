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
package org.openmrs.mobile.activities.matchingpatients

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.PatientAndMatchesWrapper
import org.openmrs.mobile.utilities.ToastUtil.notifyLong

class MatchingPatientsActivity : ACBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_patients)

        findViewById<Toolbar>(R.id.toolbar)?.let {
            it.setTitle(R.string.matching_patients_toolbar_title)
            setSupportActionBar(it)
        }

        var matchingPatientsFragment = supportFragmentManager.findFragmentById(R.id.matchingPatientsContentFrame) as MatchingPatientsFragment?
        matchingPatientsFragment = matchingPatientsFragment
                ?: MatchingPatientsFragment.newInstance()

        if (!matchingPatientsFragment.isAdded) {
            addFragmentToActivity(supportFragmentManager,
                    matchingPatientsFragment, R.id.matchingPatientsContentFrame)
        }
        if (intent.extras!!.getBoolean(ApplicationConstants.BundleKeys.CALCULATED_LOCALLY, false)) {
            showToast(getString(R.string.registration_core_info))
        }
        val patientAndMatchesWrapper = intent.getSerializableExtra(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES) as PatientAndMatchesWrapper

        MatchingPatientsPresenter(matchingPatientsFragment, patientAndMatchesWrapper.matchingPatients)
    }

    private fun showToast(message: String) {
        notifyLong(message)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit {
            putBoolean("sync", false)
        }
    }
}