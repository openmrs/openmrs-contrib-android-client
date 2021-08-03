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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.DateUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.databinding.FragmentMatchingPatientsBinding;

import java.util.List;

public class MatchingPatientsFragment extends ACBaseFragment<MatchingPatientsContract.Presenter> implements MatchingPatientsContract.View {
    private FragmentMatchingPatientsBinding binding = null;
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
    private RecyclerView recyclerView;

    public static MatchingPatientsFragment newInstance() {
        return new MatchingPatientsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMatchingPatientsBinding.inflate(inflater, container, false);
        initFragmentFields(binding.getRoot());
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        registerNewPatientButton.setOnClickListener(view -> mPresenter.registerNewPatient());
        mergePatientsButton.setOnClickListener(view -> mPresenter.mergePatients());
    }

    private void initFragmentFields(View root) {
        registerNewPatientButton = binding.registerNewPatientButton;
        mergePatientsButton = binding.mergePatientsButton;
        givenName = binding.givenName;
        middleName = binding.middleName;
        familyName = binding.familyName;
        gender = binding.gender;
        birthDate = binding.birthDate;
        address1 = binding.address1;
        address2 = binding.address2;
        city = binding.cityAutoComplete;
        state = binding.stateAutoComplete;
        country = binding.country;
        postalCode = binding.postalCode;
        recyclerView = binding.recyclerView;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MergePatientsRecycleViewAdapter((getActivity()), mPresenter, matchingPatients, patient));
    }

    private void setPatientInfo(Patient patient) {
        givenName.setText(patient.getName().getGivenName());
        middleName.setText(patient.getName().getMiddleName());
        familyName.setText(patient.getName().getFamilyName());
        if (("M").equals(patient.getGender())) {
            gender.setText(getString(R.string.male));
        } else {
            gender.setText(getString(R.string.female));
        }
        birthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getBirthdate())));
        if (patient.getAddress().getAddress1() != null) {
            address1.setText(patient.getAddress().getAddress1());
        } else {
            address1.setVisibility(View.GONE);
            (binding.addr2Separator).setVisibility(View.GONE);
            (binding.addr2Hint).setVisibility(View.GONE);
        }
        if (patient.getAddress().getAddress2() != null) {
            address2.setText(patient.getAddress().getAddress2());
        } else {
            address2.setVisibility(View.GONE);
            (binding.addr2Separator).setVisibility(View.GONE);
            (binding.addr2Hint).setVisibility(View.GONE);
        }
        if (patient.getAddress().getCityVillage() != null) {
            city.setText(patient.getAddress().getCityVillage());
        } else {
            city.setVisibility(View.GONE);
            (binding.citySeparator).setVisibility(View.GONE);
            (binding.cityHint).setVisibility(View.GONE);
        }
        if (patient.getAddress().getStateProvince() != null) {
            state.setText(patient.getAddress().getStateProvince());
        } else {
            state.setVisibility(View.GONE);
            (binding.stateSeparator).setVisibility(View.GONE);
            (binding.stateHint).setVisibility(View.GONE);
        }
        if (patient.getAddress().getCountry() != null) {
            country.setText(patient.getAddress().getCountry());
        } else {
            country.setVisibility(View.GONE);
            (binding.countrySeparator).setVisibility(View.GONE);
            (binding.countryHint).setVisibility(View.GONE);
        }
        if (patient.getAddress().getPostalCode() != null) {
            postalCode.setText(patient.getAddress().getPostalCode());
        } else {
            postalCode.setVisibility(View.GONE);
            (binding.postalCodeSeparator).setVisibility(View.GONE);
            (binding.postalCodeHint).setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
