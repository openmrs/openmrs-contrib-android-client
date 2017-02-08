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

package org.openmrs.mobile.activities.visitdashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.formlist.FormListActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VisitDashboardFragment extends ACBaseFragment<VisitDashboardContract.Presenter> implements VisitDashboardContract.View{

    private ExpandableListView mExpandableListView;
    private TextView mEmptyListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_visit_dashboard, container, false);

        mEmptyListView = (TextView) root.findViewById(R.id.visitDashboardEmpty);
        FontsUtil.setFont(mEmptyListView, FontsUtil.OpenFonts.OPEN_SANS_BOLD);
        mExpandableListView = (ExpandableListView) root.findViewById(R.id.visitDashboardExpList);
        mExpandableListView.setEmptyView(mEmptyListView);
        setEmptyListVisibility(false);
        return root;
    }

    @Override
    public void startCaptureVitals(long patientId) {
        try {
            Intent intent = new Intent(this.getActivity(), FormListActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showLongToast(this.getActivity(), ToastUtil.ToastType.ERROR, R.string.failed_to_open_vitals_form);
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
    }

    public static VisitDashboardFragment newInstance() {
        return new VisitDashboardFragment();
    }

    @Override
    public void moveToPatientDashboard() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void updateList(List<Encounter> visitEncounters) {
        final String[] displayableEncounterTypes = ApplicationConstants.EncounterTypes.ENCOUNTER_TYPES_DISPLAYS;
        final HashSet<String> displayableEncounterTypesArray = new HashSet<>(Arrays.asList(displayableEncounterTypes));
        
        List<Encounter> displayableEncounters  = new ArrayList<>();

        for (Encounter encounter : visitEncounters) {
            String encounterTypeDisplay = encounter.getEncounterType().getDisplay();
            if (displayableEncounterTypesArray.contains(encounterTypeDisplay)) {
                displayableEncounters.add(encounter);
            }
        }

        VisitExpandableListAdapter expandableListAdapter = new VisitExpandableListAdapter(this.getActivity(), displayableEncounters);
        mExpandableListView.setAdapter(expandableListAdapter);
        mExpandableListView.setGroupIndicator(null);
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        if (visibility) {
            mEmptyListView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setActionBarTitle(String name) {
        ((VisitDashboardActivity)getActivity()).getSupportActionBar().setTitle(name);
    }

    @Override
    public void setActiveVisitMenu() {
        Menu menu = ((VisitDashboardActivity) getActivity()).menu;
        (getActivity()).getMenuInflater().inflate(R.menu.active_visit_menu, menu);
    }

    @Override
    public void showErrorToast(String message) {
        ToastUtil.error(message);
    }

    @Override
    public void showErrorToast(int messageId) {
        ToastUtil.error(getString(messageId));
    }

}
