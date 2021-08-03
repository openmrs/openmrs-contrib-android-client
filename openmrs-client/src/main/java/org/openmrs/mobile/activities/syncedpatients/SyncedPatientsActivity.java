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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity;
import org.openmrs.mobile.databinding.ActivityFindPatientsBinding;

public class SyncedPatientsActivity extends ACBaseActivity {
    public SyncedPatientsPresenter presenter;
    private SearchView searchView;
    private String query;
    private MenuItem addPatientMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFindPatientsBinding binding = ActivityFindPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_synced_patients);
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

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
            presenter = new SyncedPatientsPresenter(syncedPatientsFragment, query);
        } else {
            presenter = new SyncedPatientsPresenter(syncedPatientsFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        /*
         * searchView takes up null value in dark mode of operation in absence of ActionBar
         * thus onCreateOptionsMenu never gets called
         */
        if (searchView != null) {
            String query = searchView.getQuery().toString();
            outState.putString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case R.id.syncbutton:
                enableAddPatient(OpenmrsAndroid.getSyncState());
                break;
            case R.id.actionAddPatients:
                Intent intent = new Intent(this, LastViewedPatientsActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                // Do nothing
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.find_locally_and_add_patients_menu, menu);

        addPatientMenuItem = menu.findItem(R.id.actionAddPatients);
        enableAddPatient(OpenmrsAndroid.getSyncState());

        MenuItem searchMenuItem = menu.findItem(R.id.actionSearchLocal);
        searchView = (SearchView) searchMenuItem.getActionView();

        if (StringUtils.notEmpty(query)) {
            searchMenuItem.expandActionView();
            searchView.setQuery(query, true);
            searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                presenter.setQuery(query);
                presenter.updateLocalPatientsList();
                return true;
            }
        });

        return true;
    }

    private void enableAddPatient(boolean enabled) {
        int resId = enabled ? R.drawable.ic_add : R.drawable.ic_add_disabled;
        addPatientMenuItem.setEnabled(enabled);
        addPatientMenuItem.setIcon(resId);
    }
}