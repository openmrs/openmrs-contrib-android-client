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
package org.openmrs.mobile.activities.providermanagerdashboard

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class ProviderManagerDashboardActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_management)

        supportActionBar?.run {
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.provider_manager)
        }

        // Create fragment
        var providerManagerDashboardFragment = supportFragmentManager.findFragmentById(R.id.providerManagementContentFrame) as ProviderManagerDashboardFragment?
        if (providerManagerDashboardFragment == null) {
            providerManagerDashboardFragment = ProviderManagerDashboardFragment.newInstance()
        }
        if (!providerManagerDashboardFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, providerManagerDashboardFragment, R.id.providerManagementContentFrame)
        }
    }

    /* Override to show the SnackBar under the floating action button, to not cover it */
    override fun showNoInternetConnectionSnackbar() {
        val fragment = supportFragmentManager.findFragmentById(R.id.providerManagementContentFrame)
        mSnackbar = Snackbar.make(fragment!!.requireView(),
                getString(R.string.no_internet_connection_message), Snackbar.LENGTH_INDEFINITE)
        val sbView = mSnackbar.view
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        mSnackbar.show()
    }
}
