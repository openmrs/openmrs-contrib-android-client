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

package org.openmrs.mobile.activities.patientdashboard.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.DateUtils;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.databinding.FragmentPatientDetailsBinding;
import org.openmrs.mobile.utilities.ImageUtils;

public class PatientDetailsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientDetails {
    private PatientDashboardActivity patientDashboardActivity;
    private FragmentPatientDetailsBinding binding = null;

    public static PatientDetailsFragment newInstance() {
        return new PatientDetailsFragment();
    }

    @Override
    public void attachSnackbarToActivity() {
        ((ACBaseActivity) getActivity()).showNoInternetConnectionSnackbar();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        patientDashboardActivity = (PatientDashboardActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patient_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSynchronize:
                ((PatientDashboardDetailsPresenter) mPresenter).synchronizePatient();
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientDetailsBinding.inflate(inflater, null, false);
        return binding.getRoot();
    }

    @Override
    public void resolvePatientDataDisplay(final Patient patient) {
        if (isAdded()) {
            if (("M").equals(patient.getGender())) {
                binding.patientDetailsGender.setText(getString(R.string.male));
            } else {
                binding.patientDetailsGender.setText(getString(R.string.female));
            }
        }
        ImageView patientImageView = binding.patientPhoto;

        if (patient.getPhoto() != null) {
            final Bitmap photo = patient.getResizedPhoto();
            final String patientName = patient.getName().getNameString();
            patientImageView.setImageBitmap(photo);
            patientImageView.setOnClickListener(view -> ImageUtils.showPatientPhoto(getContext(), photo, patientName));
        }

        binding.patientDetailsName.setText(patient.getName().getNameString());

        Long longTime = DateUtils.convertTime(patient.getBirthdate());

        if (longTime != null) {
            binding.patientDetailsBirthDate.setText(DateUtils.convertTime(longTime));
        }

        if (null != patient.getAddress()) {
            binding.addressDetailsStreet.setText(patient.getAddress().getAddressString());
            showAddressDetailsViewElement(binding.addressDetailsStateLabel, binding.addressDetailsState, patient.getAddress().getStateProvince());
            showAddressDetailsViewElement(binding.addressDetailsCountryLabel, binding.addressDetailsCountry, patient.getAddress().getCountry());
            showAddressDetailsViewElement(binding.addressDetailsPostalCodeLabel, binding.addressDetailsPostalCode, patient.getAddress().getPostalCode());
            showAddressDetailsViewElement(binding.addressDetailsCityLabel, binding.addressDetailsCity, patient.getAddress().getCityVillage());
        }

        if (patient.isDeceased()) {
            binding.deceasedView.setVisibility(View.VISIBLE);
            binding.deceasedView.setText(getString(R.string.marked_patient_deceased_successfully, patient.getCauseOfDeath().getDisplay()));
        }
    }

    @Override
    public void showDialog(int resId) {
        patientDashboardActivity.showProgressDialog(resId);
    }

    private void showAddressDetailsViewElement(TextView detailsViewLabel, TextView detailsView, String detailsText) {
        if (StringUtils.notNull(detailsText) && StringUtils.notEmpty(detailsText)) {
            detailsView.setText(detailsText);
        } else {
            detailsView.setVisibility(View.GONE);
            detailsViewLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismissDialog() {
        patientDashboardActivity.dismissCustomFragmentDialog();
    }

    @Override
    public void showToast(int stringRes, boolean error) {
        ToastUtil.ToastType toastType = error ? ToastUtil.ToastType.ERROR : ToastUtil.ToastType.SUCCESS;
        ToastUtil.showShortToast(patientDashboardActivity, toastType, stringRes);
    }

    @Override
    public void setMenuTitle(String nameString, String identifier) {
        patientDashboardActivity.getSupportActionBar().setTitle(nameString);
        patientDashboardActivity.getSupportActionBar().setSubtitle("#" + identifier);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                patientDashboardActivity.hideFABs(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
