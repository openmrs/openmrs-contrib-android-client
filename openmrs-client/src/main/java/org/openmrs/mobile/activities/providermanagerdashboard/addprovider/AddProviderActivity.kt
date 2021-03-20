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
package org.openmrs.mobile.activities.providermanagerdashboard.addprovider

import org.openmrs.mobile.activities.ACBaseActivity
import android.os.Bundle
import org.openmrs.mobile.R

class AddProviderActivity : ACBaseActivity() {
    var addProviderFragment: AddProviderFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_provider)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setTitle(R.string.title_provider_info)
        }

        // Create fragment
        addProviderFragment = supportFragmentManager.findFragmentById(R.id.activity_add_provider_content_frame) as AddProviderFragment?
        if (addProviderFragment == null) {
            addProviderFragment = AddProviderFragment.newInstance()
        }
        if (!addProviderFragment!!.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    addProviderFragment!!, R.id.activity_add_provider_content_frame)
        }
        val mPresenter = AddProviderPresenter(addProviderFragment!!)
    }
}