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
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity;
import org.openmrs.mobile.databinding.FragmentSyncedPatientsBinding;

import java.util.ArrayList;
import java.util.List;

public class SyncedPatientsFragment extends ACBaseFragment<SyncedPatientsContract.Presenter> implements SyncedPatientsContract.View {
    private FragmentSyncedPatientsBinding binding = null;
    private TextView emptyList;
    private RecyclerView syncedPatientRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private MenuItem addPatientMenuItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSyncedPatientsBinding.inflate(inflater,container,false);

        syncedPatientRecyclerView = binding.syncedPatientRecyclerView;
        syncedPatientRecyclerView.setHasFixedSize(true);
        syncedPatientRecyclerView.setAdapter(new SyncedPatientsRecyclerViewAdapter(this, new ArrayList<>()));
        syncedPatientRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        syncedPatientRecyclerView.setLayoutManager(linearLayoutManager);

        emptyList = binding.emptySyncedPatientList;
        progressBar = binding.syncedPatientsInitialProgressBar;

        swipeRefreshLayout = binding.swipeLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshUI();
            swipeRefreshLayout.setRefreshing(false);
        });
        return binding.getRoot();
    }

    private void refreshUI() {
        progressBar.setVisibility(View.VISIBLE);
        syncedPatientRecyclerView.setVisibility(View.GONE);
        mPresenter.updateLocalPatientsList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.syncbutton:
                enableAddPatient(OpenmrsAndroid.getSyncState());
                break;
            case R.id.actionAddPatients:
                if (NetworkUtils.hasNetwork()) {
                    Intent intent = new Intent(getActivity(), LastViewedPatientsActivity.class);
                    startActivity(intent);
                } else {
                    NoInternetConnectionSnackbar();
                }
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to update data in SyncedPatientRecyclerView.
     * It creates SyncedPatientsRecyclerViewAdapter and fills it with fresh data.
     * Then new instance of SyncedPatientsRecyclerViewAdapter is set to its SyncedPatientRecyclerView.
     *
     * @param patientList new set of data
     */
    @Override
    public void updateAdapter(List<Patient> patientList) {
        SyncedPatientsRecyclerViewAdapter adapter = new SyncedPatientsRecyclerViewAdapter(this, patientList);
        adapter.notifyDataSetChanged();
        syncedPatientRecyclerView.setAdapter(adapter);
    }

    @Override
    public void updateListVisibility(boolean isVisible) {
        progressBar.setVisibility(View.GONE);
        if (isVisible) {
            syncedPatientRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            syncedPatientRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(getString(R.string.search_patient_no_results));
        }
    }

    @Override
    public void updateListVisibility(boolean isVisible, @NonNull String replacementWord) {
        if (isVisible) {
            syncedPatientRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            syncedPatientRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(getString(R.string.search_patient_no_result_for_query, replacementWord));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        addPatientMenuItem = menu.findItem(R.id.actionAddPatients);
        enableAddPatient(OpenmrsAndroid.getSyncState());
    }

    private void enableAddPatient(boolean enabled) {
        int resId = enabled ? R.drawable.ic_add : R.drawable.ic_add_disabled;
        addPatientMenuItem.setEnabled(enabled);
        addPatientMenuItem.setIcon(resId);
    }

    private void NoInternetConnectionSnackbar() {
        Snackbar mSnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
            R.string.snackbar_no_internet_connection, Snackbar.LENGTH_SHORT);
        View sbView = mSnackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    /**
     * @return New instance of SyncedPatientsFragment
     */
    public static SyncedPatientsFragment newInstance() {
        return new SyncedPatientsFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
