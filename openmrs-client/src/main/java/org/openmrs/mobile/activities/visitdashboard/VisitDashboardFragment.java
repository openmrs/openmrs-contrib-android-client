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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.formlist.FormListActivity;
import org.openmrs.mobile.databinding.FragmentVisitDashboardBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VisitDashboardFragment extends ACBaseFragment<VisitDashboardContract.Presenter> implements VisitDashboardContract.View {
    private FragmentVisitDashboardBinding binding = null;
    private ExpandableListView expandableListView;
    private TextView emptyListView;
    private Snackbar snackbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVisitDashboardBinding.inflate(inflater, container, false);

        emptyListView = binding.visitDashboardEmpty;
        expandableListView = binding.visitDashboardExpList;
        expandableListView.setEmptyView(emptyListView);
        setEmptyListVisibility(false);

        return binding.getRoot();
    }

    @Override
    public void startCaptureVitals(long patientId) {
        try {
            Intent intent = new Intent(this.getActivity(), FormListActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showLongToast(this.getActivity(), ToastUtil.ToastType.ERROR, R.string.failed_to_open_vitals_form);
            OpenmrsAndroid.getOpenMRSLogger().d(e.toString());
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

        List<Encounter> displayableEncounters = new ArrayList<>();

        for (Encounter encounter : visitEncounters) {
            String encounterTypeDisplay = encounter.getEncounterType().getDisplay();
            encounterTypeDisplay = encounterTypeDisplay.split("\\(")[0].trim();
            if (displayableEncounterTypesArray.contains(encounterTypeDisplay)) {
                encounter.getEncounterType().setDisplay(encounterTypeDisplay.split("\\(")[0].trim());
                displayableEncounters.add(encounter);
            }
        }
        setupSnackBar();
        if (displayableEncounters.size() == 0) {
            snackbar.show();
        } else {
            snackbar.dismiss();
        }

        VisitExpandableListAdapter expandableListAdapter = new VisitExpandableListAdapter(this.getActivity(), displayableEncounters);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setGroupIndicator(null);
    }

    private void setupSnackBar() {
        snackbar = Snackbar.make(binding.getRoot(), ApplicationConstants.EMPTY_STRING, Snackbar.LENGTH_INDEFINITE);
        View customSnackBarView = getLayoutInflater().inflate(R.layout.snackbar, null);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setPadding(0, 0, 0, 0);

        TextView noticeField = customSnackBarView.findViewById(R.id.snackbar_text);
        noticeField.setText(R.string.snackbar_empty_visit_list);
        TextView dismissButton = customSnackBarView.findViewById(R.id.snackbar_action_button);
        dismissButton.setText(R.string.snackbar_select);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), ApplicationConstants.TypeFacePathConstants.ROBOTO_MEDIUM);
        dismissButton.setTypeface(typeface);
        dismissButton.setOnClickListener(v -> mPresenter.fillForm());
        snackBarLayout.addView(customSnackBarView, 0);
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        if (visibility) {
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setActionBarTitle(String name) {
        ((VisitDashboardActivity) getActivity()).getSupportActionBar().setTitle(name);
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

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
