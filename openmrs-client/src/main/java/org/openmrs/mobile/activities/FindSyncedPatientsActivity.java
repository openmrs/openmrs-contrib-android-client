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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.SyncedPatientRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class FindSyncedPatientsActivity extends ACBaseActivity {

    private RecyclerView syncedPatientRecyclerView;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        syncedPatientRecyclerView = (RecyclerView) this.findViewById(R.id.syncedPatientRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        syncedPatientRecyclerView.setHasFixedSize(true);
        syncedPatientRecyclerView.setLayoutManager(linearLayoutManager);
        syncedPatientRecyclerView.setAdapter(new SyncedPatientRecyclerViewAdapter(this,
                new ArrayList<Patient>()));
        emptyList = (TextView) this.findViewById(R.id.emptySyncedPatientList);
        emptyList.setText(getString(R.string.search_patient_no_results));
        emptyList.setVisibility(View.VISIBLE);
        syncedPatientRecyclerView.setVisibility(View.GONE);
        updatePatientsInDatabaseList();
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePatientsInDatabaseList();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_locally_and_add_patients_menu, menu);
        MenuItem mFindPatientMenuItem = menu.findItem(R.id.actionSearchLocal);
        final SearchView findPatientView;
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        // Listener for search panel
        findPatientView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findPatientView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                updatePatientsInDatabaseList(query);
                return true;
            }
        });

        // Disable add patient icon when there is no internet connection
        if (!NetworkUtils.isOnline()) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setIcon(R.drawable.ic_add_disabled);
        }
        return true;
    }

    public void updatePatientsInDatabaseList() {
        emptyList.setText(getString(R.string.search_patient_no_results));
        List<Patient> mPatientList = new PatientDAO().getAllPatients();
        updateListVisibility(mPatientList);
    }

    public void updatePatientsInDatabaseList(String query) {
        emptyList.setText(getString(R.string.search_patient_no_result_for_query, query));
        List<Patient> mPatientList = getPatientsFilteredByQuery(new PatientDAO().getAllPatients(), query);
        updateListVisibility(mPatientList);
        ((SyncedPatientRecyclerViewAdapter) syncedPatientRecyclerView.getAdapter()).setIsFiltering(true);
    }

    private void updateListVisibility(List<Patient> mPatientList) {
        if (mPatientList.isEmpty()){
            syncedPatientRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
        else {
            syncedPatientRecyclerView.setAdapter(new SyncedPatientRecyclerViewAdapter(this,
                    mPatientList));
            syncedPatientRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }
    }

    private List<Patient> getPatientsFilteredByQuery(List<Patient> patientList, String query) {
        List<Patient> filteredList = new ArrayList<>();
        query = query.toLowerCase();

        for (Patient patient : patientList) {

            String patientName = patient.getPerson().getNames().get(0).getGivenName().toLowerCase();
            String patientSurname = patient.getPerson().getNames().get(0).getFamilyName().toLowerCase();
            String patientIdentifier = patient.getIdentifier().getIdentifier();

            boolean isPatientNameFitQuery = patientName.length() >= query.length() && patientName.substring(0,query.length()).equals(query);
            boolean isPatientSurnameFitQuery = patientSurname.length() >= query.length() && patientSurname.substring(0,query.length()).equals(query);
            boolean isPatientIdentifierFitQuery = false;
            if (patientIdentifier != null) {
                isPatientIdentifierFitQuery = patientIdentifier.length() >= query.length() && patientIdentifier.substring(0,query.length()).equals(query);
            }
            if (isPatientNameFitQuery || isPatientSurnameFitQuery || isPatientIdentifierFitQuery) {
                filteredList.add(patient);
            }
        }
        return filteredList;
    }

    public void startFindLastViewedPatientsActivity(MenuItem item) {
        Intent intent = new Intent(FindSyncedPatientsActivity.this, FindLastViewedPatientsActivity.class);
        startActivity(intent);
    }
}