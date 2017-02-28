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


package org.openmrs.mobile.activities.logs;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;


public class LogsActivity extends ACBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        LogsFragment logsFragment =
                (LogsFragment) getSupportFragmentManager().findFragmentById(R.id.logsContentFragment);
        if (logsFragment == null) {
            logsFragment = LogsFragment.newInstance();
        }
        if (!logsFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    logsFragment, R.id.logsContentFragment);
        }

        // Create the presenter
        new LogsPresenter(logsFragment, mOpenMRSLogger);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Disable Settings Option in Menu
        MenuItem settingsItem = menu.findItem(R.id.actionSettings);
        settingsItem.setVisible(false);
        return true;
    }
}
