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
package org.openmrs.mobile.activities.visitdashboard

import android.os.Bundle
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.VISIT_ID
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.databinding.ActivityVisitDashboardBinding

@AndroidEntryPoint
class VisitDashboardActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityVisitDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.run {
            elevation = 0f
            setTitle(R.string.visit_dashboard_label)
        }

        val visitId: Long = intent.getLongExtra(VISIT_ID, -1L).also {
            if (it == -1L) throw IllegalStateException("No valid visit id passed")
        }

        // Create fragment
        var visitDashboardFragment = supportFragmentManager.findFragmentById(R.id.visitDashboardContentFrame) as VisitDashboardFragment?
        if (visitDashboardFragment == null) {
            visitDashboardFragment = VisitDashboardFragment.newInstance(visitId)
        }
        if (!visitDashboardFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, visitDashboardFragment, R.id.visitDashboardContentFrame)
        }
    }
}
