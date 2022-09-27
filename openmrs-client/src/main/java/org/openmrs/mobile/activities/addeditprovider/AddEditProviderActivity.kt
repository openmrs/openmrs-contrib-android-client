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
package org.openmrs.mobile.activities.addeditprovider

import android.os.Bundle
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

@AndroidEntryPoint
class AddEditProviderActivity : ACBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_provider)

        supportActionBar?.run {
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.title_provider_info)
        }

        val providerToEdit: Provider? = intent.getSerializableExtra(PROVIDER_BUNDLE) as? Provider
        // Create fragment
        var addProviderFragment = supportFragmentManager.findFragmentById(R.id.activity_add_provider_content_frame) as AddEditProviderFragment?
        if (addProviderFragment == null) {
            addProviderFragment = AddEditProviderFragment.newInstance(providerToEdit)
        }
        if (!addProviderFragment.isActive) {
            addFragmentToActivity(supportFragmentManager, addProviderFragment, R.id.activity_add_provider_content_frame)
        }
    }
}
