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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class LastViewedPatientsActivity extends ACBaseActivity {

    private LastViewedPatientsContract.Presenter mPresenter;
    private SearchView findPatientView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_last_viewed_patients);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
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

        if(savedInstanceState != null){
            query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
            mPresenter = new LastViewedPatientsPresenter(lastViewedPatientsFragment, query);
        } else {
            mPresenter = new LastViewedPatientsPresenter(lastViewedPatientsFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
        MenuItem mFindPatientMenuItem = menu.findItem(R.id.actionSearchRemote);
        findPatientView = (SearchView) mFindPatientMenuItem.getActionView();

        if(StringUtils.notEmpty(query)){
            mFindPatientMenuItem.expandActionView();
            findPatientView.setQuery(query, true);
            findPatientView.clearFocus();
        }

        // Search function
        findPatientView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPresenter.findPatients(query);
                return true;
            }

            // This listener restores last viewed list to initial state when query is empty or SearchView is closed.
            @Override
            public boolean onQueryTextChange(String query) {
                mPresenter.updateLastViewedList(query);
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
                ((LastViewedPatientsPresenter) mPresenter).setLastQueryEmpty();
            }
        });

        return true;
    }

}
