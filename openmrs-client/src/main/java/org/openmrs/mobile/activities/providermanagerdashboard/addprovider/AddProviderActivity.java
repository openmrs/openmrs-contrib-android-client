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

package org.openmrs.mobile.activities.providermanagerdashboard.addprovider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class AddProviderActivity extends ACBaseActivity {
    AddProviderFragment addProviderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_provider);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        addProviderFragment =
            (AddProviderFragment) getSupportFragmentManager().findFragmentById(R.id.activity_add_provider_content_frame);
        if (addProviderFragment == null) {
            addProviderFragment = AddProviderFragment.newInstance();
        }
        if (!addProviderFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                addProviderFragment, R.id.activity_add_provider_content_frame);
        }

        AddProviderPresenter mPresenter = new AddProviderPresenter(addProviderFragment);
    }
}
