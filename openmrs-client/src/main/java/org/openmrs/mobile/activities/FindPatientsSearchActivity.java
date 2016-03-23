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
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.adapters.PatientRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.utilities.FontsUtil;
import java.util.ArrayList;
import java.util.List;

public class FindPatientsSearchActivity extends ACBaseActivity {
    private String mLastQuery;
    private MenuItem mFindPatientMenuItem;
    private List<Patient> mSearchedPatientsList;
    private PatientRecyclerViewAdapter mAdapter;
    private RecyclerView patientsRecyclerView;
    private TextView mEmptyList;
    private ProgressBar mSpinner;
    private boolean mSearching;
    private static int mLastSearchId;

    private static final String SEARCH_BUNDLE = "searchBundle";
    private static final String LAST_QUERY_BUNDLE = "lastQueryBundle";
    private static final String PATIENT_LIST_BUNDLE = "patientListBundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_find_patients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSpinner = (ProgressBar) findViewById(R.id.patientRecyclerViewLoading);
        patientsRecyclerView = (RecyclerView) findViewById(R.id.patientRecyclerView);
        patientsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        patientsRecyclerView.setLayoutManager(linearLayoutManager);
        mEmptyList = (TextView) findViewById(R.id.emptyPatientList);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));

        if (savedInstanceState != null) {
            mSearching = savedInstanceState.getBoolean(SEARCH_BUNDLE);
            mLastQuery = savedInstanceState.getString(LAST_QUERY_BUNDLE);
            mSearchedPatientsList = (ArrayList<Patient>) savedInstanceState.getSerializable(PATIENT_LIST_BUNDLE);
        }

        if (mSearching) {
            patientsRecyclerView.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
        } else if (mSearchedPatientsList != null) {
            mAdapter = new PatientRecyclerViewAdapter(this, mSearchedPatientsList, FindPatientLastViewedFragment.FIND_PATIENT_LAST_VIEWED_FM_ID);
            patientsRecyclerView.setAdapter(mAdapter);
            if (mSearchedPatientsList.isEmpty()){
                mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
                patientsRecyclerView.setVisibility(View.GONE);
                mSpinner.setVisibility(View.GONE);
                mEmptyList.setVisibility(View.VISIBLE);
            }
        }
        else if (getIntent().getAction() == null) {
            getIntent().setAction(Intent.ACTION_SEARCH);
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SEARCH_BUNDLE, mSearching);
        outState.putString(LAST_QUERY_BUNDLE, mLastQuery);
        outState.putSerializable(PATIENT_LIST_BUNDLE, (ArrayList) mSearchedPatientsList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mFindPatientMenuItem != null) {
            MenuItemCompat.collapseActionView(mFindPatientMenuItem);
        }
        super.onBackPressed();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearching = true;
            mLastSearchId++;
            mEmptyList.setVisibility(View.GONE);
            patientsRecyclerView.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
            mSearchedPatientsList = new ArrayList<Patient>();
            mAdapter = new PatientRecyclerViewAdapter(this,
                    mSearchedPatientsList, FindPatientLastViewedFragment.FIND_PATIENT_LAST_VIEWED_FM_ID);
            patientsRecyclerView.setAdapter(mAdapter);
            mLastQuery = intent.getStringExtra(SearchManager.QUERY);
            new FindPatientsManager().findPatient(
                    FindPatientsHelper.createFindPatientListener(mLastQuery, mLastSearchId, this));

            if (mFindPatientMenuItem != null) {
                MenuItemCompat.collapseActionView(mFindPatientMenuItem);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView findPatientView;

        mFindPatientMenuItem = menu.findItem(R.id.actionSearch);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);
        findPatientView.setIconifiedByDefault(false);
        return true;
    }

    public void updatePatientsData(int searchId, List<Patient> patientsList) {
        if (mLastSearchId == searchId) {
            mSearchedPatientsList = patientsList;
            if (patientsList.isEmpty()) {
                mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
                mSpinner.setVisibility(View.GONE);
                patientsRecyclerView.setVisibility(View.GONE);
                mEmptyList.setVisibility(View.VISIBLE);
            }
            mAdapter = new PatientRecyclerViewAdapter(this, patientsList,
                    FindPatientLastViewedFragment.FIND_PATIENT_LAST_VIEWED_FM_ID);
            patientsRecyclerView.setAdapter(mAdapter);
            patientsRecyclerView.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            mSearching = false;
        }
    }

    public void stopLoader(int searchId) {
        if (mLastSearchId == searchId) {
            mSearching = false;
            mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
            mSpinner.setVisibility(View.GONE);
            patientsRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        }
    }
}
