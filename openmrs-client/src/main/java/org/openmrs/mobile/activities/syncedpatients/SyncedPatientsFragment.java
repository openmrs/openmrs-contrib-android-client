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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

public class SyncedPatientsFragment extends ACBaseFragment<SyncedPatientsContract.Presenter> implements SyncedPatientsContract.View {

    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mSyncedPatientRecyclerView;

    //Initialization Progress bar
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_synced_patients, container, false);

        // Patient list config
        mSyncedPatientRecyclerView = (RecyclerView) root.findViewById(R.id.syncedPatientRecyclerView);
        mSyncedPatientRecyclerView.setHasFixedSize(true);
        mSyncedPatientRecyclerView.setAdapter(new SyncedPatientsRecyclerViewAdapter(this,
                new ArrayList<Patient>()));
        mSyncedPatientRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mSyncedPatientRecyclerView.setLayoutManager(linearLayoutManager);

        mEmptyList = (TextView) root.findViewById(R.id.emptySyncedPatientList);
        mProgressBar = (ProgressBar) root.findViewById(R.id.syncedPatientsInitialProgressBar);

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        return root;
    }

    /**
     * This method is used to update data in SyncedPatientRecyclerView.
     * It creates SyncedPatientsRecyclerViewAdapter and fills it with fresh data.
     * Then new instance of SyncedPatientsRecyclerViewAdapter is set to its SyncedPatientRecyclerView.
     * @param patientList new set of data
     */
    @Override
    public void updateAdapter(List<Patient> patientList) {
        SyncedPatientsRecyclerViewAdapter adapter = new SyncedPatientsRecyclerViewAdapter(this,patientList);
        adapter.notifyDataSetChanged();
        mSyncedPatientRecyclerView.setAdapter(adapter);
    }

    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mSyncedPatientRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        }
        else {
            mSyncedPatientRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyList.setText(getString(R.string.search_patient_no_results));
        }
    }

    @Override
    public void updateListVisibility(boolean isVisible, @NonNull String replacementWord) {
        if (isVisible) {
            mSyncedPatientRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        }
        else {
            mSyncedPatientRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, replacementWord));
        }
    }

    /**
     * @return New instance of SyncedPatientsFragment
     */
    public static SyncedPatientsFragment newInstance() {
        return new SyncedPatientsFragment();
    }

}
