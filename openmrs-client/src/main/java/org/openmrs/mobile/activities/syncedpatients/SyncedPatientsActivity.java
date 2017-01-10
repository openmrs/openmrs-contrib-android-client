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

package org.openmrs.mobile.activities.syncedpatients;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.NetworkUtils;

public class SyncedPatientsActivity extends ACBaseActivity {

    SyncedPatientsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        SyncedPatientsFragment syncedPatientsFragment =
                (SyncedPatientsFragment) getSupportFragmentManager().findFragmentById(R.id.syncedPatientsContentFrame);
        if (syncedPatientsFragment == null) {
            syncedPatientsFragment = SyncedPatientsFragment.newInstance();
        }
        if (!syncedPatientsFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    syncedPatientsFragment, R.id.syncedPatientsContentFrame);
        }

        // Create the presenter
        mPresenter = new SyncedPatientsPresenter(syncedPatientsFragment);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.find_locally_and_add_patients_menu, menu);

        // Search function
        MenuItem searchMenuItem = menu.findItem(R.id.actionSearchLocal);
        final SearchView searchView;
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            searchView = (SearchView) searchMenuItem.getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mPresenter.setQuery(query);
                mPresenter.updateLocalPatientsList();
                return true;
            }
        });

        return true;
    }

}