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

package org.openmrs.mobile.activities.patientdashboard.diagnosis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class PatientDiagnosisFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientDiagnosis {

    private ListView mDiagnosisList;

    public static PatientDiagnosisFragment newInstance() {
        return new PatientDiagnosisFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_diagnosis, null, false);
        mDiagnosisList = (ListView) fragmentLayout.findViewById(R.id.patientDiagnosisList);
        TextView emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyDiagnosisListView);
        mDiagnosisList.setEmptyView(emptyList);
        FontsUtil.setFont(fragmentLayout, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);
        return fragmentLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    @Override
    public void setDiagnosesToDisplay(List<String> encounters) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, encounters);
        mDiagnosisList.setAdapter(adapter);
    }
}
