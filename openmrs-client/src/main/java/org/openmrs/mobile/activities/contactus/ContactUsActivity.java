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

package org.openmrs.mobile.activities.contactus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;



public class ContactUsActivity extends ACBaseActivity {
    private MaterialButton sendButton, forumButton, ircButton;
    private TextInputEditText nameTIET, subjectTIET, messageTIET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ContactUsFragment contactUsFragment =
                (ContactUsFragment) getSupportFragmentManager().findFragmentById(R.id.contactUsContentFrame);
        if (contactUsFragment == null) {
            contactUsFragment = ContactUsFragment.newInstance();
        }
        if (!contactUsFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    contactUsFragment, R.id.settingsContentFrame);
        }

        // Create the presenter
        new ContactUsPresenter(contactUsFragment);

    }
}