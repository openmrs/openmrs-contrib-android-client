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

package org.openmrs.mobile.activities.matchingpatients;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class MatchingPatientsFragment extends ACBaseFragment<MatchingPatientsContract.Presenter> implements MatchingPatientsContract.View{

    private Button registerNewPatientButton;
    private Button mergePatientsButton;
    private TextView givenName;
    private TextView middleName;
    private TextView familyName;
    private TextView gender;
    private TextView birthDate;
    private TextView address1;
    private TextView address2;
    private TextView city;
    private TextView state;
    private TextView country;
    private TextView postalCode;
    private RecyclerView mRecyclerView;
    private View view;

    public static MatchingPatientsFragment newInstance(){
        return new MatchingPatientsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_matching_patients, container, false);
        initFragmentFields(view);
        setListeners();
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return view;
    }

    private void setListeners() {
        registerNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.registerNewPatient();
            }
        });
        mergePatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.mergePatients();
            }
        });
    }

    private void initFragmentFields(View root) {
        registerNewPatientButton = (Button) root.findViewById(R.id.registerNewPatientButton);
        mergePatientsButton = (Button) root.findViewById(R.id.mergePatientsButton);
        givenName = (TextView) root.findViewById(R.id.givenName);
        middleName = (TextView) root.findViewById(R.id.middleName);
        familyName = (TextView) root.findViewById(R.id.familyName);
        gender = (TextView) root.findViewById(R.id.gender);
        birthDate = (TextView) root.findViewById(R.id.birthDate);
        address1 = (TextView) root.findViewById(R.id.address1);
        address2 = (TextView) root.findViewById(R.id.address2);
        city = (TextView) root.findViewById(R.id.city);
        state = (TextView) root.findViewById(R.id.state);
        country = (TextView) root.findViewById(R.id.country);
        postalCode = (TextView) root.findViewById(R.id.postalCode);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
    }

    @Override
    public void showPatientsData(Patient patient, List<Patient> matchingPatients) {
        setPatientInfo(patient);
        setMatchingPatients(patient, matchingPatients);
    }

    @Override
    public void enableMergeButton() {
        mergePatientsButton.setEnabled(true);
    }

    @Override
    public void disableMergeButton() {
        mergePatientsButton.setEnabled(false);
    }

    @Override
    public void notifyUser(int stringId) {
        ToastUtil.notify(getString(stringId));
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void showErrorToast(String message) {
        ToastUtil.error(message);
    }

    private void setMatchingPatients(Patient patient, List<Patient> matchingPatients) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new MergePatientsRecycleViewAdapter((getActivity()), mPresenter,  matchingPatients, patient));
    }

    private void setPatientInfo(Patient patient) {
        givenName.setText(patient.getPerson().getName().getGivenName());
        middleName.setText(patient.getPerson().getName().getMiddleName());
        familyName.setText(patient.getPerson().getName().getFamilyName());
        if (("M").equals(patient.getPerson().getGender())) {
            gender.setText(getString(R.string.male));
        } else {
            gender.setText(getString(R.string.female));
        }
        birthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        if (patient.getPerson().getAddress().getAddress1() != null) {
            address1.setText(patient.getPerson().getAddress().getAddress1());
        } else {
            address1.setVisibility(View.GONE);
            view.findViewById(R.id.addr2Separator).setVisibility(View.GONE);
            view.findViewById(R.id.addr2Hint).setVisibility(View.GONE);
        }
        if (patient.getPerson().getAddress().getAddress2() != null) {
            address2.setText(patient.getPerson().getAddress().getAddress2());
        } else {
            address2.setVisibility(View.GONE);
            view.findViewById(R.id.addr2Separator).setVisibility(View.GONE);
            view.findViewById(R.id.addr2Hint).setVisibility(View.GONE);
        }
        if (patient.getPerson().getAddress().getCityVillage() != null) {
            city.setText(patient.getPerson().getAddress().getCityVillage());
        } else {
            city.setVisibility(View.GONE);
            view.findViewById(R.id.citySeparator).setVisibility(View.GONE);
            view.findViewById(R.id.cityHint).setVisibility(View.GONE);
        }
        if (patient.getPerson().getAddress().getStateProvince() != null) {
            state.setText(patient.getPerson().getAddress().getStateProvince());
        } else {
            state.setVisibility(View.GONE);
            view.findViewById(R.id.stateSeparator).setVisibility(View.GONE);
            view.findViewById(R.id.stateHint).setVisibility(View.GONE);
        }
        if (patient.getPerson().getAddress().getCountry() != null) {
            country.setText(patient.getPerson().getAddress().getCountry());
        } else {
            country.setVisibility(View.GONE);
            view.findViewById(R.id.countrySeparator).setVisibility(View.GONE);
            view.findViewById(R.id.countryHint).setVisibility(View.GONE);
        }
        if (patient.getPerson().getAddress().getPostalCode() != null) {
            postalCode.setText(patient.getPerson().getAddress().getPostalCode());
        } else {
            postalCode.setVisibility(View.GONE);
            view.findViewById(R.id.postalCodeSeparator).setVisibility(View.GONE);
            view.findViewById(R.id.postalCodeHint).setVisibility(View.GONE);
        }
    }
}
