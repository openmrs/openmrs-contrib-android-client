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

package org.openmrs.mobile.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.PatientHierarchyAdapter;
import org.openmrs.mobile.bundle.PatientListBundle;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;


public class PatientsVitalsListFragment extends Fragment {

    private List<Patient> mPatientList;
    private View fragmentLayout;
    private ListView visitList;

    public PatientsVitalsListFragment() {
        //empty constructor
    }

    public static PatientsVitalsListFragment newInstance(PatientListBundle patientListBundle) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_LIST, patientListBundle);
        PatientsVitalsListFragment fragment = new PatientsVitalsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle savedInstanceStateToReadFrom = savedInstanceState;
        if (savedInstanceStateToReadFrom == null) {
            savedInstanceStateToReadFrom = getArguments();
        }
        mPatientList = ((PatientListBundle) savedInstanceStateToReadFrom.getSerializable(ApplicationConstants.BundleKeys.PATIENT_LIST)).getPatients();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_find_patients, null, false);
        visitList = (ListView) fragmentLayout.findViewById(R.id.patientListView);

        TextView emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyPatientListView);
        emptyList.setText(getString(R.string.search_patient_no_results));
        if (null == mPatientList || mPatientList.isEmpty()) {
            visitList.setEmptyView(emptyList);
        } else {
            visitList.setAdapter(new PatientHierarchyAdapter(getActivity(), R.layout.patient_details_row, mPatientList));
        }

        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
