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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.Visit;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity;
import org.openmrs.mobile.databinding.FragmentPatientVisitBinding;

import java.util.List;

public class PatientVisitsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientVisits {
    private RecyclerView visitRecyclerView;
    private TextView emptyList;
    private FragmentPatientVisitBinding binding =null;
    private PatientDashboardActivity mPatientDashboardActivity;
    public static final int REQUEST_CODE_FOR_VISIT = 1;
    private Patient patient;

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
                if (OpenmrsAndroid.getSyncState()) {
                    ((PatientDashboardVisitsPresenter) mPresenter).syncVisits();
                } else {
                    ToastUtil.notify(getString(R.string.offline_mode_not_supported));
                }
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
    public void showErrorToast(int messageId) {
        ToastUtil.error(getString(messageId));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientVisitBinding.inflate(inflater, null, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        visitRecyclerView = binding.patientVisitRecyclerView;
        visitRecyclerView.setHasFixedSize(true);
        visitRecyclerView.setLayoutManager(linearLayoutManager);

        emptyList = binding.emptyVisitsList;
        return binding.getRoot();

    }

    public void startVisit() {
        if (patient.isDeceased()) {
            ToastUtil.error(getString(R.string.cannot_start_visit_for_deceased));
        } else {
            ((PatientDashboardVisitsPresenter) mPresenter).startVisit();
        }
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
        } else {
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
            } else {
                activity.showStartVisitImpossibleDialog(activity.getSupportActionBar().getTitle());
            }
        }
    }

    @Override
    public void showStartVisitProgressDialog() {
        ((PatientDashboardActivity) getActivity()).showProgressDialog(R.string.action_starting_visit);
    }

    @Override
    public void setPatient(Patient mPatient) {
        patient = mPatient;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                mPatientDashboardActivity.hideFABs(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
