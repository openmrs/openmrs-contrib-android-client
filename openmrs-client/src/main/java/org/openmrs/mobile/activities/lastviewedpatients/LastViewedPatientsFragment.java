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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.databinding.FragmentLastViewedPatientsBinding;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LastViewedPatientsFragment extends ACBaseFragment<LastViewedPatientsContract.Presenter> implements LastViewedPatientsContract.View {
    private FragmentLastViewedPatientsBinding binding = null;
    private static final String PATIENT_LIST = "patient_list";
    private static final String SELECTED_PATIENT_POSITIONS = "selected_patient_positions";
    private TextView emptyList;
    private ProgressBar progressBar;
    private RecyclerView patientsRecyclerView;
    private LastViewedPatientRecyclerViewAdapter mAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLastViewedPatientsBinding.inflate(inflater,container,false);

        patientsRecyclerView = binding.lastViewedPatientRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        patientsRecyclerView.setLayoutManager(linearLayoutManager);
        progressBar = binding.patientRecyclerViewLoading;
        emptyList = binding.emptyLastViewedPatientList;
        swipeRefreshLayout = binding.swiperefreshLastPatients;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (NetworkUtils.hasNetwork()) {
                mPresenter.refresh();
                mAdapter.finishActionMode();
            } else {
                ToastUtil.error(getString(R.string.no_internet_connection_message));
                getActivity().finish();
            }
        });

        patientsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    mPresenter.loadMorePatients();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            mPresenter.onSaveInstanceState(outState);
            List<Patient> patientList = mAdapter.getPatients();
            outState.putSerializable(PATIENT_LIST, (Serializable) patientList);
            outState.putSerializable(SELECTED_PATIENT_POSITIONS, (Serializable) mAdapter.getSelectedPatientPositions());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            List<Patient> patientList = (List<Patient>) savedInstanceState.getSerializable(PATIENT_LIST);
            if (patientList == null) {
                mPresenter.subscribe();
            } else {
                updateList(patientList);
                mPresenter.setStartIndex(savedInstanceState.getInt(ApplicationConstants.BundleKeys.PATIENTS_START_INDEX, 0));
                Set<Integer> selectedPatientPositions = (Set<Integer>) savedInstanceState.getSerializable(SELECTED_PATIENT_POSITIONS);
                if (selectedPatientPositions != null && !selectedPatientPositions.isEmpty()) {
                    mAdapter.startActionMode();
                    mAdapter.setSelectedPatientPositions(selectedPatientPositions);
                }
            }
        } else {
            mPresenter.subscribe();
        }
    }

    @Override
    public void enableSwipeRefresh(boolean enabled) {
        swipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public void setProgressBarVisibility(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        emptyList.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setListVisibility(boolean visibility) {
        patientsRecyclerView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyListText(String text) {
        emptyList.setText(text);
    }

    public static LastViewedPatientsFragment newInstance() {
        return new LastViewedPatientsFragment();
    }

    public void updateList(List<Patient> patientList) {
        mAdapter = new LastViewedPatientRecyclerViewAdapter(this.getActivity(), patientList, this);
        patientsRecyclerView.setAdapter(mAdapter);
    }

    public boolean isRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }

    public void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorToast(String message) {
        ToastUtil.error(message);
    }

    @Override
    public void showOpenPatientSnackbar(final Long patientId) {
        FrameLayout frameLayout = swipeRefreshLayout.findViewById(R.id.swipe_container);
        Snackbar snackbar = Snackbar.make(frameLayout, getResources().getString(R.string.snackbar_info_patient_downloaded), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.setAction(getResources().getString(R.string.snackbar_action_open), view -> openPatientDashboardActivity(patientId));
        snackbar.show();
    }

    @Override
    public void addPatientsToList(List<Patient> patients) {
        mAdapter.addPatients(patients);
    }

    @Override
    public void showRecycleViewProgressBar(boolean visibility) {
        if (visibility) {
            mAdapter.addPatients(Collections.singletonList(null));
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        } else {
            mAdapter.deleteLastItem();
        }
    }

    private void openPatientDashboardActivity(Long patientId) {
        Intent intent = new Intent(this.getContext(), PatientDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId);
        this.getContext().startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
