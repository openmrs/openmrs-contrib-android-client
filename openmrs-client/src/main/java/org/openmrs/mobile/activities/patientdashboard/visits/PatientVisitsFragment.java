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

package org.openmrs.mobile.activities.patientdashboard.visits;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientVisitsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientVisits {

    private RecyclerView visitRecyclerView;
    private TextView emptyList;

    public static final int REQUEST_CODE_FOR_VISIT = 1;

    public static PatientVisitsFragment newInstance() {
        return new PatientVisitsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patients_visit_tab_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionStartVisit:
                ((PatientDashboardVisitsPresenter) mPresenter).syncVisits();
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showErrorToast(String message) {
        ToastUtil.error(message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_patient_visit, null, false);
        visitRecyclerView = (RecyclerView) root.findViewById(R.id.patientVisitRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        visitRecyclerView.setHasFixedSize(true);
        visitRecyclerView.setLayoutManager(linearLayoutManager);

        emptyList = (TextView) root.findViewById(R.id.emptyVisitsList);

        return root;
    }

    public void startVisit() {
        ((PatientDashboardVisitsPresenter) mPresenter).startVisit();
    }

    @Override
    public void dismissCurrentDialog() {
        ((PatientDashboardActivity) getActivity()).dismissCustomFragmentDialog();
    }

    @Override
    public void toggleRecyclerListVisibility(boolean isVisible) {
        if (isVisible) {
            visitRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }
        else {
            visitRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setVisitsToDisplay(List<Visit> visits) {
        PatientVisitsRecyclerViewAdapter adapter = new PatientVisitsRecyclerViewAdapter(this, visits);
        visitRecyclerView.setAdapter(adapter);
        visitRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void goToVisitDashboard(Long visitID) {
        Intent intent = new Intent(this.getActivity(), VisitDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, visitID);
        startActivityForResult(intent, REQUEST_CODE_FOR_VISIT);
    }

    @Override
    public void showStartVisitDialog(boolean isVisitPossible) {
        PatientDashboardActivity activity = (PatientDashboardActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            if (isVisitPossible) {
                activity.showStartVisitDialog(activity.getSupportActionBar().getTitle());
            }
            else {
                activity.showStartVisitImpossibleDialog(activity.getSupportActionBar().getTitle());
            }
        }
    }

    @Override
    public void showStartVisitProgressDialog() {
        ((PatientDashboardActivity) getActivity()).showProgressDialog(R.string.action_starting_visit);
    }
}
