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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class SimilarPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SimilarPatientsRecyclerViewAdapter.PatientViewHolder>{

    private List<Patient> patientList;
    private Patient newPatient;
    private Activity mContext;

    public SimilarPatientsRecyclerViewAdapter(Activity mContext, List<Patient> patientList, Patient patient) {
        this.newPatient = patient;
        this.patientList = patientList;
        this.mContext = mContext;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_patient_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        final Patient patient = patientList.get(position);

        setPatientName(holder, patient);
        setGender(holder, patient);
        setBirthdate(holder, patient);
        setPatientAdres(holder, patient);

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(new PatientDAO().isUserAlreadySaved(patient.getUuid()))) {
                    downloadPatient(patient);
                }
                Intent intent = new Intent(mContext, PatientDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, getPatientId(patient));
                mContext.startActivity(intent);
                mContext.finish();
            }
        });
    }

    private String getPatientId(Patient patient) {
        return new PatientDAO().findPatientByUUID(patient.getUuid()).getId().toString();
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder{

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
            mGivenName = (TextView) itemView.findViewById(R.id.patientGivenName);
            mMiddleName = (TextView) itemView.findViewById(R.id.patientMiddleName);
            mFamilyName = (TextView) itemView.findViewById(R.id.patientFamilyName);
            mGender = (TextView) itemView.findViewById(R.id.patientGender);
            mBirthDate = (TextView) itemView.findViewById(R.id.patientBirthDate);
            mAddres = (TextView) itemView.findViewById(R.id.patientAddres);
            mPostalCode = (TextView) itemView.findViewById(R.id.patientPostalCode);
            mCity = (TextView) itemView.findViewById(R.id.patientCity);
            mCountry = (TextView) itemView.findViewById(R.id.patientCountry);
        }

    }
    private void downloadPatient(Patient patient) {
        new PatientDAO().savePatient(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    new VisitApi().syncVisitsData(patient);
                    new VisitApi().syncLastVitals(patient.getUuid());
                });
    }

    private void setBirthdate(PatientViewHolder holder, Patient patient) {
        try{
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
            if (Objects.equal(patient.getPerson().getBirthdate(), newPatient.getPerson().getBirthdate())) {
                setStyleForMatchedPatientFields(holder.mBirthDate);
            }
        }
        catch (Exception e)
        {
            holder.mBirthDate.setText(" ");
        }
    }

    private void setGender(PatientViewHolder holder, Patient patient) {
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
            if (Objects.equal(patient.getPerson().getGender(), newPatient.getPerson().getGender())) {
                setStyleForMatchedPatientFields(holder.mGender);            }
        }
    }

    private void setPatientAdres(PatientViewHolder holder, Patient patient) {
        if (null != patient.getPerson().getAddress().getAddress1()) {
            holder.mAddres.setText(patient.getPerson().getAddress().getAddress1());
            if (Objects.equal(patient.getPerson().getAddress().getAddress1(), newPatient.getPerson().getAddress().getAddress1())) {
                setStyleForMatchedPatientFields(holder.mAddres);
            }
        }
        if (null != patient.getPerson().getAddress().getPostalCode()) {
            holder.mPostalCode.setText(patient.getPerson().getAddress().getPostalCode());
            if (Objects.equal(patient.getPerson().getAddress().getPostalCode(), newPatient.getPerson().getAddress().getPostalCode())) {
                setStyleForMatchedPatientFields(holder.mPostalCode);
            }
        }
        if (null != patient.getPerson().getAddress().getCityVillage()) {
            holder.mCity.setText(patient.getPerson().getAddress().getCityVillage());
            if (Objects.equal(patient.getPerson().getAddress().getCityVillage(), newPatient.getPerson().getAddress().getCityVillage())) {
                setStyleForMatchedPatientFields(holder.mCity);
            }
        }
        if (null != patient.getPerson().getAddress().getCountry()) {
            holder.mCountry.setText(patient.getPerson().getAddress().getCountry());
            if (Objects.equal(patient.getPerson().getAddress().getCountry(), newPatient.getPerson().getAddress().getCountry())) {
                setStyleForMatchedPatientFields(holder.mCountry);
            }
        }
    }

    private void setPatientName(PatientViewHolder holder, Patient patient) {
        if (null != patient.getPerson().getName().getGivenName()) {
            holder.mGivenName.setText(patient.getPerson().getName().getGivenName());
            if (Objects.equal(patient.getPerson().getName().getGivenName(), newPatient.getPerson().getName().getGivenName())) {
                setStyleForMatchedPatientFields(holder.mGivenName);
            }
        }
        if (null != patient.getPerson().getName().getMiddleName()) {
            holder.mMiddleName.setText(patient.getPerson().getName().getMiddleName());
            if (Objects.equal(patient.getPerson().getName().getMiddleName(), newPatient.getPerson().getName().getMiddleName())) {
                setStyleForMatchedPatientFields(holder.mMiddleName);
            }
        }
        if (null != patient.getPerson().getName().getFamilyName()) {
            holder.mFamilyName.setText(patient.getPerson().getName().getFamilyName());
            if (Objects.equal(patient.getPerson().getName().getFamilyName(), newPatient.getPerson().getName().getFamilyName())) {
                setStyleForMatchedPatientFields(holder.mFamilyName);
            }
        }
    }

    private void setStyleForMatchedPatientFields(TextView textView){
        textView.setTypeface(null , Typeface.BOLD);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
