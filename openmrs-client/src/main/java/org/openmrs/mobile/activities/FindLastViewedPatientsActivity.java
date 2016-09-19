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

package org.openmrs.mobile.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.LastViewedPatientRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.listeners.findPatients.FindPatientListener;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.util.List;

public class FindLastViewedPatientsActivity extends ACBaseActivity {

    private FindLastViewedPatientsActivity mActivity;
    private TextView mEmptyList;
    private ProgressBar mSpinner;
    private RecyclerView mPatientsRecyclerView;
    private LastViewedPatientRecyclerViewAdapter mAdapter;
    private FindPatientListener mFpmResponseListener;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private static List<Patient> mPatientsList;
    private String mLastQuery = "QUERY";
    private String mQuery;
    private boolean mIsSearching;

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectionAvailable()) {
            FindPatientsManager fpm = new FindPatientsManager();
            mSpinner.setVisibility(View.VISIBLE);
            fpm.getLastViewedPatient(mFpmResponseListener);
        } else {
            mEmptyList.setText(R.string.find_patient_no_connection);
            mEmptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_last_viewed_patients);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPatientsRecyclerView = ((RecyclerView) findViewById(R.id.lastViewedPatientRecyclerView));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPatientsRecyclerView.setLayoutManager(linearLayoutManager);
        mSpinner = (ProgressBar) findViewById(R.id.patientRecyclerViewLoading);

        mSwipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swiperefreshLastPatients));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mIsSearching) {
                    executeQuery(mQuery);
                }
                else {
                    updateLastViewedList();
                }
            }
        });

        mEmptyList = (TextView) findViewById(R.id.emptyLastViewedPatientList);
        mFpmResponseListener = FindPatientsHelper.createFindPatientListener(this);
        mActivity = this;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_patients_remote_menu, menu);
        final SearchView findPatientView;
        MenuItem mFindPatientMenuItem = menu.findItem(R.id.actionSearchRemote);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        findPatientView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mIsSearching = true;
                mQuery = query;
                executeQuery(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty() && !mLastQuery.isEmpty()) {
                    mIsSearching = false;
                    updateLastViewedList();
                }
                return true;
            }
        });
        return true;
    }

    public void updatePatientsData(List<Patient> patientsList) {
        mSpinner.setVisibility(View.GONE);
        mPatientsList = patientsList;
        if (patientsList.isEmpty()) {
            if (mIsSearching) {
                mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
            }
            else {
                mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
            }
            mPatientsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        }
        else {
            mAdapter = new LastViewedPatientRecyclerViewAdapter(this, patientsList);
            mPatientsRecyclerView.setAdapter(mAdapter);
            mPatientsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void stopLoader() {
        if (mIsSearching) {
            mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
        }
        else {
            mEmptyList.setText(getString(R.string.find_patient_no_last_viewed));
        }
        mPatientsRecyclerView.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
        mSpinner.setVisibility(View.GONE);
    }

    private void updateLastViewedList() {
        if (isConnectionAvailable()) {
            mEmptyList.setVisibility(View.GONE);
            mPatientsRecyclerView.setVisibility(View.GONE);
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSpinner.setVisibility(View.VISIBLE);
            }
            if (mAdapter != null) {
                mAdapter.clear();
            }
            FindPatientsManager fpm = new FindPatientsManager();
            fpm.getLastViewedPatient(mFpmResponseListener);
        } else {
            mEmptyList.setText(getString(R.string.find_patient_no_connection));
            mPatientsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        }
    }

    private void executeQuery(String query) {
        mLastQuery = query;
        FindPatientsManager fpm = new FindPatientsManager();
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSpinner.setVisibility(View.VISIBLE);
        }
        mEmptyList.setVisibility(View.GONE);
        fpm.findPatient(FindPatientsHelper.createFindPatientListener(query, mActivity));
    }

    public boolean isConnectionAvailable() {
        return NetworkUtils.isOnline();
    }

    public static void clearLastViewedPatientList() {
        mPatientsList = null;
    }

    public static void setLastViewedPatientList(List<Patient> patientsList) {
        mPatientsList = patientsList;
    }

    public enum ActivityMethod {
        StopLoader,
        Update
    }

}
