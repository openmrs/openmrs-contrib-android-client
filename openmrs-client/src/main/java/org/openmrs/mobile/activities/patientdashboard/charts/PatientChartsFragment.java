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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.Observation;
import com.openmrs.android_sdk.library.models.Visit;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.databinding.FragmentPatientChartsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PatientChartsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientCharts, PatientChartsRecyclerViewAdapter.OnClickListener {
    private JSONObject observationList;
    private FragmentPatientChartsBinding binding;
    private PatientChartsRecyclerViewAdapter chartsListAdapter;

    public static PatientChartsFragment newInstance() {
        return new PatientChartsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientChartsBinding.inflate(inflater, container, false);

        setEmptyListVisibility(false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        if (visibility) {
            binding.vitalEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.vitalEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void populateList(List<Visit> visits) {
        final String[] displayableEncounterTypes = ApplicationConstants.EncounterTypes.ENCOUNTER_TYPES_DISPLAYS;
        final HashSet<String> displayableEncounterTypesArray = new HashSet<>(Arrays.asList(displayableEncounterTypes));
        observationList = new JSONObject();

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
            chartsListAdapter = new PatientChartsRecyclerViewAdapter(this.getActivity(), observationList, this);
            binding.vitalList.setHasFixedSize(true);
            binding.vitalList.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.vitalList.setAdapter(chartsListAdapter);
        }
    }

    @Override
    public void showChartActivity(String vitalName) {
        try {
            JSONObject chartData = observationList.getJSONObject(vitalName);
            Iterator<String> dates = chartData.keys();
            ArrayList<String> dateList = Lists.newArrayList(dates);

            if (dateList.size() == 0)
                ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.ERROR, getString(R.string.data_not_available_for_this_field));
            else {
                JSONArray dataArray = chartData.getJSONArray(dateList.get(0));
                String entry = (String) dataArray.get(0);
                try {
                    Float entryValue = Float.parseFloat(entry);
                    Intent intent = new Intent(getActivity(), ChartsViewActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("vitalName", chartData.toString());
                    intent.putExtra("bundle", mBundle);
                    startActivity(intent);
                } catch (NumberFormatException e) {
                    ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.ERROR, getString(R.string.data_type_not_available_for_this_field));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.ERROR, e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
