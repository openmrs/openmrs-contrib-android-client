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

package org.openmrs.mobile.activities.addeditpatient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Objects;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.api.repository.VisitRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class SimilarPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SimilarPatientsRecyclerViewAdapter.PatientViewHolder> {

    private List<Patient> patientList;
    private Patient newPatient;
    private Activity mContext;

    public SimilarPatientsRecyclerViewAdapter(Activity mContext, List<Patient> patientList, Patient patient) {
        this.newPatient = patient;
        this.patientList = patientList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_similar_patient, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        final Patient patient = patientList.get(position);

        setPatientName(holder, patient);
        setGender(holder, patient);
        setBirthdate(holder, patient);
        setPatientAdres(holder, patient);

        holder.mRowLayout.setOnClickListener(view -> {
            if (!(new PatientDAO().isUserAlreadySaved(patient.getUuid()))) {
                downloadPatient(patient);
            }
            Intent intent = new Intent(mContext, PatientDashboardActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, getPatientId(patient));
            mContext.startActivity(intent);
            mContext.finish();
        });
    }

    private String getPatientId(Patient patient) {
        return new PatientDAO().findPatientByUUID(patient.getUuid()).getId().toString();
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mRowLayout;
        private TextView mGivenName;
        private TextView mMiddleName;
        private TextView mFamilyName;
        private TextView mGender;
        private TextView mBirthDate;
        private TextView mAddres;
        private TextView mPostalCode;
        private TextView mCity;
        private TextView mCountry;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mGivenName = itemView.findViewById(R.id.patientGivenName);
            mMiddleName = itemView.findViewById(R.id.patientMiddleName);
            mFamilyName = itemView.findViewById(R.id.patientFamilyName);
            mGender = itemView.findViewById(R.id.patientGender);
            mBirthDate = itemView.findViewById(R.id.patientBirthDate);
            mAddres = itemView.findViewById(R.id.patientAddres);
            mPostalCode = itemView.findViewById(R.id.patientPostalCode);
            mCity = itemView.findViewById(R.id.patientCity);
            mCountry = itemView.findViewById(R.id.patientCountry);
        }

    }

    private void downloadPatient(Patient patient) {
        new PatientDAO().savePatient(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    new VisitRepository().syncVisitsData(patient);
                    new VisitRepository().syncLastVitals(patient.getUuid());
                });
    }

    private void setBirthdate(PatientViewHolder holder, Patient patient) {
        try {
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getBirthdate())));
            if (Objects.equal(patient.getBirthdate(), newPatient.getBirthdate())) {
                setStyleForMatchedPatientFields(holder.mBirthDate);
            }
        } catch (Exception e) {
            holder.mBirthDate.setText(" ");
        }
    }

    private void setGender(PatientViewHolder holder, Patient patient) {
        if (null != patient.getGender()) {
            holder.mGender.setText(patient.getGender());
            if (Objects.equal(patient.getGender(), newPatient.getGender())) {
                setStyleForMatchedPatientFields(holder.mGender);
            }
        }
    }

    private void setPatientAdres(PatientViewHolder holder, Patient patient) {
        if (null != patient.getAddress().getAddress1()) {
            holder.mAddres.setText(patient.getAddress().getAddress1());
            if (Objects.equal(patient.getAddress().getAddress1(), newPatient.getAddress().getAddress1())) {
                setStyleForMatchedPatientFields(holder.mAddres);
            }
        }
        if (null != patient.getAddress().getPostalCode()) {
            holder.mPostalCode.setText(patient.getAddress().getPostalCode());
            if (Objects.equal(patient.getAddress().getPostalCode(), newPatient.getAddress().getPostalCode())) {
                setStyleForMatchedPatientFields(holder.mPostalCode);
            }
        }
        if (null != patient.getAddress().getCityVillage()) {
            holder.mCity.setText(patient.getAddress().getCityVillage());
            if (Objects.equal(patient.getAddress().getCityVillage(), newPatient.getAddress().getCityVillage())) {
                setStyleForMatchedPatientFields(holder.mCity);
            }
        }
        if (null != patient.getAddress().getCountry()) {
            holder.mCountry.setText(patient.getAddress().getCountry());
            if (Objects.equal(patient.getAddress().getCountry(), newPatient.getAddress().getCountry())) {
                setStyleForMatchedPatientFields(holder.mCountry);
            }
        }
    }

    private void setPatientName(PatientViewHolder holder, Patient patient) {
        if (null != patient.getName().getGivenName()) {
            holder.mGivenName.setText(patient.getName().getGivenName());
            if (Objects.equal(patient.getName().getGivenName(), newPatient.getName().getGivenName())) {
                setStyleForMatchedPatientFields(holder.mGivenName);
            }
        }
        if (null != patient.getName().getMiddleName()) {
            holder.mMiddleName.setText(patient.getName().getMiddleName());
            if (Objects.equal(patient.getName().getMiddleName(), newPatient.getName().getMiddleName())) {
                setStyleForMatchedPatientFields(holder.mMiddleName);
            }
        }
        if (null != patient.getName().getFamilyName()) {
            holder.mFamilyName.setText(patient.getName().getFamilyName());
            if (Objects.equal(patient.getName().getFamilyName(), newPatient.getName().getFamilyName())) {
                setStyleForMatchedPatientFields(holder.mFamilyName);
            }
        }
    }

    private void setStyleForMatchedPatientFields(TextView textView) {
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
