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
import org.openmrs.mobile.adapters.ActiveVisitsRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

public class FindActiveVisitsActivity extends ACBaseActivity {

    private RecyclerView visitsRecyclerView;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visits);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        visitsRecyclerView = (RecyclerView) findViewById(R.id.visitsRecyclerView);
        visitsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        visitsRecyclerView.setLayoutManager(linearLayoutManager);
        visitsRecyclerView.setAdapter(new ActiveVisitsRecyclerViewAdapter(this,
                new ArrayList<Visit>()));

        emptyList = (TextView) findViewById(R.id.emptyVisitsListViewLabel);
        emptyList.setText(getString(R.string.search_visits_no_results));
        emptyList.setVisibility(View.INVISIBLE);

        updateVisitsInDatabaseList();

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_visits_menu, menu);
        final SearchView findVisitView;
        MenuItem mFindVisitItem = menu.findItem(R.id.actionSearchLocalVisits);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findVisitView = (SearchView) mFindVisitItem.getActionView();
        } else {
            findVisitView = (SearchView) MenuItemCompat.getActionView(mFindVisitItem);
        }

        findVisitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findVisitView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                updateVisitsInDatabaseList(query);
                return true;
            }
        });
        return true;
    }

    private void updateVisitsInDatabaseList() {
        emptyList.setText(getString(R.string.search_patient_no_results));
        List<Visit> visits = new VisitDAO().getAllActiveVisits();
        updateListVisibility(visits);
    }
    private void updateVisitsInDatabaseList(String query) {
        emptyList.setText(getString(R.string.search_patient_no_result_for_query, query));
        List<Visit> visits = getPatientsFilteredByQuery(new VisitDAO().getAllActiveVisits(), query);
        updateListVisibility(visits);
        ((ActiveVisitsRecyclerViewAdapter) visitsRecyclerView.getAdapter()).setIsFiltering(true);
    }

    private void updateListVisibility(List<Visit> visits) {
        if (visits.isEmpty()) {
            visitsRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        } else {
            visitsRecyclerView.setAdapter(new ActiveVisitsRecyclerViewAdapter(this, visits));
            visitsRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }
    }

    private List<Visit> getPatientsFilteredByQuery(List<Visit> visitList, String query) {
        List<Visit> filteredList = new ArrayList<>();
        query = query.toLowerCase();

        for (Visit visit : visitList) {
            Patient patient = new PatientDAO().findPatientByID(visit.getPatientID().toString());

            String visitPlace = visit.getVisitPlace().toLowerCase();
            String visitType = visit.getVisitType().toLowerCase();
            String patientName = patient.getPerson().getNames().get(0).getGivenName().toLowerCase();
            String patientSurname = patient.getPerson().getNames().get(0).getFamilyName().toLowerCase();
            String patientIdentifier = patient.getIdentifier().getIdentifier().toLowerCase();

            boolean isVisitPlaceFitQuery = visitPlace.length() >= query.length() && visitPlace.substring(0, query.length()).equals(query);
            boolean isVisitTypeFitQuery = visitType.length() >= query.length() && visitType.substring(0, query.length()).equals(query);
            boolean isPatientNameFitQuery = patientName.length() >= query.length() && patientName.substring(0, query.length()).equals(query);
            boolean isPatientSurnameFitQuery = patientSurname.length() >= query.length() && patientSurname.substring(0, query.length()).equals(query);
            boolean isPatientIdentifierFitQuery = false;
            if (patientIdentifier != null) {
                isPatientIdentifierFitQuery = patientIdentifier.length() >= query.length() && patientIdentifier.substring(0, query.length()).equals(query);
            }
            if (isPatientNameFitQuery || isPatientSurnameFitQuery || isPatientIdentifierFitQuery || isVisitPlaceFitQuery || isVisitTypeFitQuery) {
                filteredList.add(visit);
            }
        }
        return filteredList;
    }
}
