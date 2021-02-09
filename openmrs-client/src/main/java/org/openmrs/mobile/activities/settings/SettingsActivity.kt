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
package org.openmrs.mobile.activities.settings

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

class SettingsActivity : ACBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.action_settings)
        }

        // Create fragment
        var settingsFragment = supportFragmentManager.findFragmentById(R.id.settingsContentFrame) as SettingsFragment?
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance()
        }
        if (!settingsFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    settingsFragment, R.id.settingsContentFrame)
        }
        // Create the presenter
        SettingsPresenter(settingsFragment, mOpenMRSLogger)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //Disable Settings Option in Menu
        val settingsItem = menu.findItem(R.id.actionSettings)
        settingsItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_OK, null)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, null)
        super.onBackPressed()
    }
}