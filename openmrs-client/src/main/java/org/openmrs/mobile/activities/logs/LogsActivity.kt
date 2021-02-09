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
package org.openmrs.mobile.activities.logs

import android.os.Bundle
import android.view.Menu
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

class LogsActivity : ACBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.title_activity_logs)
        }
        // Create fragment
        var logsFragment = supportFragmentManager.findFragmentById(R.id.logsContentFragment) as LogsFragment?
        if (logsFragment == null) {
            logsFragment = LogsFragment.newInstance()
        }
        if (!logsFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    logsFragment, R.id.logsContentFragment)
        }
        // Create the presenter
        LogsPresenter(logsFragment, mOpenMRSLogger)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //Disable Settings Option in Menu
        val settingsItem = menu.findItem(R.id.actionSettings)
        settingsItem.isVisible = false
        return true
    }
}