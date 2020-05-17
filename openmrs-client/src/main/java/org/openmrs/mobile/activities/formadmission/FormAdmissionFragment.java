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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databinding.FragmentFormAdmissionBinding;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ToastUtil;

public class FormAdmissionFragment extends ACBaseFragment<FormAdmissionContract.Presenter> implements FormAdmissionContract.View {

    private FragmentFormAdmissionBinding formAdmissionBinding;

    private String admittedByPerson = "";
    private String admittedToPerson = "";

    public static FormAdmissionFragment newInstance() {
        return new FormAdmissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        formAdmissionBinding = FragmentFormAdmissionBinding.inflate(inflater, container, false);
        View root = formAdmissionBinding.getRoot();

        initFragmentFields();
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
        if(admittedByPerson.isEmpty() || admittedToPerson.isEmpty())
            ToastUtil.showShortToast(getContext(), ToastUtil.ToastType.ERROR, "Please Select Required Field");
        else
            mPresenter.createEncounter(admittedByPerson, admittedToPerson);
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
                admittedByPerson = formAdmissionBinding.admittedBySpinner.getSelectedItem().toString();

                for(int i = 0;i<providerList.size();i++) {
                    if(admittedByPerson.equals(providerList.get(i).getDisplay())){
                        admittedByPerson = providerList.get(i).getUuid();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showToast(String error) {
        Log.i("error",error);
    }

    @Override
    public void updateLocationAdapter(List<Location> results) {
        String[] locations = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            locations[i] = results.get(i).getDisplay();
        }
        ArrayAdapter<String> adapterAdmittedTo = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locations);
        formAdmissionBinding.admittedToSpinner.setAdapter(adapterAdmittedTo);
        formAdmissionBinding.admittedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                admittedToPerson = formAdmissionBinding.admittedToSpinner.getSelectedItem().toString();

                for(int i = 0;i<results.size();i++) {
                    if(admittedToPerson.equals(results.get(i).getDisplay())){
                        admittedToPerson = results.get(i).getUuid();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
