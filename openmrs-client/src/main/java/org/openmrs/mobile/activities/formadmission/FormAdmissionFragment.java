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

package org.openmrs.mobile.activities.formadmission;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.databinding.FragmentFormAdmissionBinding;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.utilities.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FormAdmissionFragment extends ACBaseFragment<FormAdmissionContract.Presenter> implements FormAdmissionContract.View {

    private FragmentFormAdmissionBinding formAdmissionBinding;

    private String providerUUID = "";
    private String locationUUID = "";
    private String encounterRoleUUID = "";

    public static FormAdmissionFragment newInstance() {
        return new FormAdmissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        formAdmissionBinding = FragmentFormAdmissionBinding.inflate(inflater, container, false);
        View root = formAdmissionBinding.getRoot();

        initFragmentFields();
        mPresenter.getEncounterRoles();
        mPresenter.getProviders(this);
        mPresenter.getLocation(OpenMRS.getInstance().getServerUrl());

        return root;
    }

    private void initFragmentFields() {

        Date currentDate = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formAdmissionBinding.admissionDateHeader.setText(df.format(currentDate));
        formAdmissionBinding.submitButton.setOnClickListener(v -> createEncounter());
    }

    private void createEncounter() {
        if (providerUUID.isEmpty() || locationUUID.isEmpty() || encounterRoleUUID.isEmpty())
            ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.ERROR, getString(R.string.admission_fields_required));
        else
            mPresenter.createEncounter(providerUUID, locationUUID, encounterRoleUUID);
    }

    @Override
    public void updateProviderAdapter(List<Provider> providerList) {
        String[] providers = new String[providerList.size()];
        for (int i = 0; i < providerList.size(); i++) {
            providers[i] = providerList.get(i).getDisplay();
        }

        ArrayAdapter<String> adapterAdmittedBy = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, providers);
        formAdmissionBinding.admittedBySpinner.setAdapter(adapterAdmittedBy);
        formAdmissionBinding.admittedBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String providerDisplay = formAdmissionBinding.admittedBySpinner.getSelectedItem().toString();

                for (int i = 0; i < providerList.size(); i++) {
                    if (providerDisplay.equals(providerList.get(i).getDisplay())) {
                        providerUUID = providerList.get(i).getUuid();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void updateLocationAdapter(List<LocationEntity> locationList) {
        String[] locations = new String[locationList.size()];
        for (int i = 0; i < locationList.size(); i++) {
            locations[i] = locationList.get(i).getDisplay();
        }
        ArrayAdapter<String> adapterAdmittedTo = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locations);
        formAdmissionBinding.admittedToSpinner.setAdapter(adapterAdmittedTo);
        formAdmissionBinding.admittedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String locationDisplay = formAdmissionBinding.admittedToSpinner.getSelectedItem().toString();

                for (int i = 0; i < locationList.size(); i++) {
                    if (locationDisplay.equals(locationList.get(i).getDisplay())) {
                        locationUUID = locationList.get(i).getUuid();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void updateEncounterRoleList(List<Resource> encounterRoleList) {
        String[] encounterRole = new String[encounterRoleList.size()];
        for (int i = 0; i < encounterRoleList.size(); i++) {
            encounterRole[i] = encounterRoleList.get(i).getDisplay();
        }
        ArrayAdapter<String> adapterEncounterRole = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, encounterRole);
        formAdmissionBinding.encounterRoleSpinner.setAdapter(adapterEncounterRole);
        formAdmissionBinding.encounterRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String encounterRoleDisplay = formAdmissionBinding.encounterRoleSpinner.getSelectedItem().toString();

                for (int i = 0; i < encounterRoleList.size(); i++) {
                    if (encounterRoleDisplay.equals(encounterRoleList.get(i).getDisplay())) {
                        encounterRoleUUID = encounterRoleList.get(i).getUuid();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showToast(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    @Override
    public void enableSubmitButton(boolean value) {
        formAdmissionBinding.submitButton.setEnabled(value);
    }

    @Override
    public void quitFormEntry() {
        getActivity().finish();
    }

}
