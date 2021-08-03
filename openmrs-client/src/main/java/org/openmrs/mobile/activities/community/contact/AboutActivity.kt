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

package org.openmrs.mobile.activities.community.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.openmrs.android_sdk.utilities.ApplicationConstants
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.databinding.ActivityAboutBinding

class AboutActivity : ACBaseActivity() {
    private lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        binding.moreAboutOpenmrsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.ABOUT_OPENMRS_URL))
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //Disable About Option in Menu
        val aboutItem = menu.findItem(R.id.actionAbout)
        aboutItem.isVisible = false
        val contactItem = menu.findItem(R.id.actionContact)
        contactItem.isVisible = false
        val logOutItem = menu.findItem(R.id.actionLogout)
        logOutItem.isVisible = false
        val locationItem = menu.findItem(R.id.actionLocation)
        locationItem.isVisible = false
        val settingItem = menu.findItem(R.id.actionSettings)
        settingItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}