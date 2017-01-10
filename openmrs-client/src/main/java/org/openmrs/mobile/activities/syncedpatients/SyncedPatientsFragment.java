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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SyncedPatientsFragment extends Fragment implements SyncedPatientsContract.View {

    // Presenter
    private SyncedPatientsContract.Presenter mPresenter;

    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mSyncedPatientRecyclerView;

    //Menu Items
    private MenuItem mAddPatientMenuItem;

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

        // Empty list notification config
        mEmptyList = (TextView) root.findViewById(R.id.emptySyncedPatientList);
        mEmptyList.setText(getString(R.string.search_patient_no_results));
        mEmptyList.setVisibility(View.VISIBLE);

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.syncbutton:
                enableAddPatient(OpenMRS.getInstance().getSyncState());
                break;
            case R.id.actionAddPatients:
                Intent intent = new Intent(getActivity(), LastViewedPatientsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return state of fragment
     */
    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * Sets presenter for this view
     */
    @Override
    public void setPresenter(@NonNull SyncedPatientsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
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

    /**
     * Method used to switch visibility of empty list notification and RecyclerView list
     * This method is also able to set empty list notification message
     * @param isVisible describes visibility of RecyclerView
     * @param emptyListTextStringId id for getString() to get specified message from project resources
     * @param replacementWord Optional and Nullable replacement word for overloaded getString() method
     */
    @Override
    public void updateListVisibility(boolean isVisible, int emptyListTextStringId, @Nullable String replacementWord) {
        if (isVisible) {
            mSyncedPatientRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        }
        else {
            mSyncedPatientRecyclerView.setVisibility(View.GONE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        mAddPatientMenuItem = menu.findItem(R.id.actionAddPatients);
        enableAddPatient(OpenMRS.getInstance().getSyncState());
    }

    @Override
    public void enableAddPatient(boolean enabled) {
        int resId = enabled ? R.drawable.ic_add : R.drawable.ic_add_disabled;
        mAddPatientMenuItem.setEnabled(enabled);
        mAddPatientMenuItem.setIcon(resId);
    }


    /**
     * @return New instance of SyncedPatientsFragment
     */
    public static SyncedPatientsFragment newInstance() {
        return new SyncedPatientsFragment();
    }

}
