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

package org.openmrs.mobile.activities.formentrypatientlist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.formlist.FormListActivity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

public class FormEntryPatientListFragment extends ACBaseFragment<FormEntryPatientListContract.Presenter> implements  FormEntryPatientListContract.View {

    private RecyclerView mPatientRecyclerView;
    private TextView mEmptyList;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_entry_patient_list, container, false);

        mPatientRecyclerView = root.findViewById(R.id.patientRecyclerView);
        mPatientRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mPatientRecyclerView.setLayoutManager(linearLayoutManager);

        mEmptyList = root.findViewById(R.id.emptyPatientList);
        mProgressBar = root.findViewById(R.id.formEntryListInitialProgressBar);
        mSwipeRefreshLayout = root.findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshUI();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        // Font config
        FontsUtil.setFont(this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    private void refreshUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.updatePatientsList();
    }

    @Override
    public void updateAdapter(List<Patient> patientList) {
        FormEntryPatientListAdapter adapter = new FormEntryPatientListAdapter(this,patientList);
        adapter.notifyDataSetChanged();
        mPatientRecyclerView.setAdapter(adapter);
    }

    @Override
    public void updateListVisibility(boolean isVisible, int emptyListTextStringId, @Nullable String replacementWord) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mPatientRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        }
        else {
            mPatientRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        }

        if (StringUtils.isBlank(replacementWord)) {
            mEmptyList.setText(getString(emptyListTextStringId));
        }
        else {
            mEmptyList.setText(getString(emptyListTextStringId, replacementWord));
        }
    }

    @Override
    public void startEncounterForPatient(Long selectedPatientID) {
        Intent intent = new Intent(this.getActivity(), FormListActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, selectedPatientID);
        startActivity(intent);
    }

    public static FormEntryPatientListFragment newInstance() {
        return new FormEntryPatientListFragment();
    }

    public void showSnackbarInactivePatients(View v) {
        Snackbar snackbar = Snackbar.make(v, R.string.snackbar_nonvisitting_patients, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = view.findViewById(R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
