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

package org.openmrs.mobile.activities.lastviewedpatients;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.databinding.ActivityFindLastViewedPatientsBinding;

public class LastViewedPatientsActivity extends ACBaseActivity {
    private LastViewedPatientsContract.Presenter presenter;
    private SearchView findPatientView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFindLastViewedPatientsBinding binding= ActivityFindLastViewedPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.action_download_patients);
        }

        // Create fragment
        LastViewedPatientsFragment lastViewedPatientsFragment =
            (LastViewedPatientsFragment) getSupportFragmentManager().findFragmentById(R.id.lastPatientsContentFrame);
        if (lastViewedPatientsFragment == null) {
            lastViewedPatientsFragment = LastViewedPatientsFragment.newInstance();
        }
        if (!lastViewedPatientsFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                lastViewedPatientsFragment, R.id.lastPatientsContentFrame);
        }

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
            presenter = new LastViewedPatientsPresenter(lastViewedPatientsFragment, query,getApplicationContext());
        } else {
            presenter = new LastViewedPatientsPresenter(lastViewedPatientsFragment,getApplicationContext());
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String query = findPatientView.getQuery().toString();
        outState.putString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.find_patients_remote_menu, menu);
        MenuItem findPatientMenuItem = menu.findItem(R.id.actionSearchRemote);
        findPatientView = (SearchView) findPatientMenuItem.getActionView();

        if (StringUtils.notEmpty(query)) {
            findPatientMenuItem.expandActionView();
            findPatientView.setQuery(query, true);
            findPatientView.clearFocus();
        }

        // Search function
        findPatientView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.findPatients(query);
                return true;
            }

            // This listener restores last viewed list to initial state when query is empty or SearchView is closed.
            @Override
            public boolean onQueryTextChange(String query) {
                presenter.updateLastViewedList(query);
                return true;
            }
        });

        findPatientView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                // nothing to do
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                ((LastViewedPatientsPresenter) presenter).setLastQueryEmpty();
            }
        });

        return true;
    }
}
