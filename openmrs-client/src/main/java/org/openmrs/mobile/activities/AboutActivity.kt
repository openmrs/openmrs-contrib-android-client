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

package org.openmrs.mobile.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import org.openmrs.mobile.utilities.ApplicationConstants
import kotlinx.android.synthetic.main.activity_about.*
import org.openmrs.mobile.R

class AboutActivity : ACBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        OpenMRSWebsiteTextView.setOnClickListener { intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(ApplicationConstants.ABOUT_OPENMRS_URL))
            startActivity(intent) }
    }
}