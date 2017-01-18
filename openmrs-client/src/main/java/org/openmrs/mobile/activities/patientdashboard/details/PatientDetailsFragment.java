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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.ByteArrayOutputStream;

public class PatientDetailsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientDetails {

    private View rootView;

    private PatientDashboardContract.PatientDetailsPresenter mPresenter;

    public static PatientDetailsFragment newInstance() {
        return new PatientDetailsFragment();
    }

    @Override
    public void attachSnackbarToActivity() {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(R.id.patientDashboardContentFrame), "Offline mode. Patient data may not be up to date.", Snackbar.LENGTH_INDEFINITE);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
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
                mPresenter.synchronizePatient();
                break;
            case R.id.actionUpdatePatient:
                startPatientUpdateActivity(mPresenter.getPatientId());
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_patient_details, null, false);
        FontsUtil.setFont((ViewGroup) rootView);
        return rootView;
    }

    @Override
    public void resolvePatientDataDisplay(final Patient patient) {
        if (("M").equals(patient.getPerson().getGender())) {
            ((TextView) rootView.findViewById(R.id.patientDetailsGender)).setText("Male");
        } else {
            ((TextView) rootView.findViewById(R.id.patientDetailsGender)).setText("Female");
        }
        ImageView patientImageView = (ImageView) rootView.findViewById(R.id.patientPhoto);

        if (patient.getPerson().getPhoto() != null) {
            final Bitmap photo = patient.getPerson().getPhoto();
            final String patientName = patient.getPerson().getName().getNameString();
            patientImageView.setImageBitmap(photo);
            patientImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPatientPhoto(photo, patientName);
                }
            });
        }

        ((TextView) rootView.findViewById(R.id.patientDetailsName)).setText(patient.getPerson().getName().getNameString());

        Long longTime = DateUtils.convertTime(patient.getPerson().getBirthdate());
        
        if (longTime != null) {
            ((TextView) rootView.findViewById(R.id.patientDetailsBirthDate)).setText(DateUtils.convertTime(longTime));
        }

        if (null != patient.getPerson().getAddress()) {
            showAddressDetailsViewElement(rootView.findViewById(R.id.addressLayout), R.id.addressDetailsStreet, patient.getPerson().getAddress().getAddressString());
            showAddressDetailsViewElement(rootView.findViewById(R.id.stateLayout), R.id.addressDetailsState, patient.getPerson().getAddress().getStateProvince());
            showAddressDetailsViewElement(rootView.findViewById(R.id.countryLayout), R.id.addressDetailsCountry, patient.getPerson().getAddress().getCountry());
            showAddressDetailsViewElement(rootView.findViewById(R.id.postalCodeLayout), R.id.addressDetailsPostalCode, patient.getPerson().getAddress().getPostalCode());
            showAddressDetailsViewElement(rootView.findViewById(R.id.cityLayout), R.id.addressDetailsCity, patient.getPerson().getAddress().getCityVillage());
        }
    }

    @Override
    public void showDialog(int resId) {
        ((PatientDashboardActivity) this.getActivity()).showProgressDialog(resId);
    }

    private void showAddressDetailsViewElement(View detailsLayout, int detailsViewId, String detailsText) {
        if (StringUtils.notNull(detailsText) && StringUtils.notEmpty(detailsText)) {
            ((TextView) detailsLayout.findViewById(detailsViewId)).setText(detailsText);
        } else {
            detailsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void dismissDialog() {
        ((PatientDashboardActivity) this.getActivity()).dismissCustomFragmentDialog();
    }

    @Override
    public void showToast(int stringRes, boolean error) {
        ToastUtil.ToastType toastType = error ? ToastUtil.ToastType.ERROR : ToastUtil.ToastType.SUCCESS;
        ToastUtil.showShortToast(this.getActivity() ,toastType ,stringRes);
    }

    @Override
    public void setMenuTitle(String nameString, String identifier) {
        ((PatientDashboardActivity) this.getActivity()).getSupportActionBar().setTitle(nameString);
        ((PatientDashboardActivity) this.getActivity()).getSupportActionBar().setSubtitle("#" + identifier);
    }

    @Override
    public void setPresenter(PatientDashboardContract.PatientDashboardMainPresenter presenter) {
        this.mPresenter = ((PatientDashboardContract.PatientDetailsPresenter) presenter);
    }

    @Override
    public void startPatientUpdateActivity(long patientId) {
        Intent updatePatient = new Intent(this.getActivity(), AddEditPatientActivity.class);
        updatePatient.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE,
                String.valueOf(patientId));
        startActivity(updatePatient);
    }

    public void showPatientPhoto(Bitmap photo, String patientName) {
        Intent intent = new Intent(getContext(), PatientPhotoActivity.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        intent.putExtra("photo", byteArrayOutputStream.toByteArray());
        intent.putExtra("name", patientName);
        startActivity(intent);
    }
}
