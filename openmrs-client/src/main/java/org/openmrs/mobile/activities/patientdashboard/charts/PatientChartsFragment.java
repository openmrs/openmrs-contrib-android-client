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

package org.openmrs.mobile.activities.patientdashboard.charts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class PatientChartsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientCharts {

    private ExpandableListView mExpandableListView;
    private TextView mEmptyListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_charts, null, false);

        mEmptyListView = (TextView) root.findViewById(R.id.vitalEmpty);
        FontsUtil.setFont(mEmptyListView, FontsUtil.OpenFonts.OPEN_SANS_BOLD);
        mExpandableListView = (ExpandableListView) root.findViewById(R.id.vitalExpList);
        mExpandableListView.setEmptyView(mEmptyListView);
        setEmptyListVisibility(false);
        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    public static PatientChartsFragment newInstance() {
        return new PatientChartsFragment();
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        if (visibility) {
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void populateList(List<Visit> visits) {
        final String[] displayableEncounterTypes = ApplicationConstants.EncounterTypes.ENCOUNTER_TYPES_DISPLAYS;
        final HashSet<String> displayableEncounterTypesArray = new HashSet<>(Arrays.asList(displayableEncounterTypes));
        JSONObject observationList = new JSONObject();

        for (Visit visit : visits) {
            List<Encounter> encounters = visit.getEncounters();
            if (!encounters.isEmpty()) {
                setEmptyListVisibility(false);
                for (Encounter encounter : encounters) {
                    String datetime = encounter.getEncounterDate();
                    String encounterTypeDisplay = encounter.getEncounterType().getDisplay();
                    if (displayableEncounterTypesArray.contains(encounterTypeDisplay)) {
                        for (Observation obs : encounter.getObservations()) {
                            String observationLabel = obs.getDisplay();
                            if (observationLabel.contains(":")) {
                                observationLabel = observationLabel.substring(0, observationLabel.indexOf(':'));
                            }
                            if (observationList.has(observationLabel)) {
                                JSONObject chartData = null;
                                try {
                                    chartData = observationList.getJSONObject(observationLabel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (chartData.has(datetime)) {
                                    JSONArray obsValue = null;
                                    try {
                                        obsValue = chartData.getJSONArray(datetime);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    obsValue.put(obs.getDisplayValue());
                                    try {
                                        chartData.put(datetime, obsValue);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    JSONArray obsValue = new JSONArray();
                                    obsValue.put(obs.getDisplayValue());
                                    try {
                                        chartData.put(datetime, obsValue);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                JSONObject chartData = new JSONObject();
                                JSONArray obsValue = new JSONArray();
                                obsValue.put(obs.getDisplayValue());
                                try {
                                    chartData.put(datetime, obsValue);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    observationList.put(observationLabel, chartData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            }


            VitalsListAdapter vitalsListAdapter = new VitalsListAdapter(this.getActivity(), observationList);
            mExpandableListView.setAdapter(vitalsListAdapter);
            mExpandableListView.setGroupIndicator(null);
        }


    }

}
