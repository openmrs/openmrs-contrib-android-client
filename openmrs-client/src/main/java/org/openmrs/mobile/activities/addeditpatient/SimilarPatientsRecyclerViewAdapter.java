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
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Objects;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.api.repository.VisitRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.databinding.RowSimilarPatientBinding;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class SimilarPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SimilarPatientsRecyclerViewAdapter.PatientViewHolder> {
    private final List<Patient> patientList;
    private final Patient newPatient;
    private final Activity mContext;

    public SimilarPatientsRecyclerViewAdapter(Activity mContext, List<Patient> patientList, Patient patient) {
        this.newPatient = patient;
        this.patientList = patientList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSimilarPatientBinding binding = RowSimilarPatientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PatientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        final Patient patient = patientList.get(position);

        setPatientName(holder, patient);
        setGender(holder, patient);
        setBirthdate(holder, patient);
        setPatientAdres(holder, patient);

        holder.binding.getRoot().setOnClickListener(view -> {
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

    public static class PatientViewHolder extends RecyclerView.ViewHolder {

        RowSimilarPatientBinding binding;

        public PatientViewHolder(RowSimilarPatientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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
            holder.binding.patientBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getBirthdate())));
            if (Objects.equal(patient.getBirthdate(), newPatient.getBirthdate())) {
                setStyleForMatchedPatientFields(holder.binding.patientBirthDate);
            }
        } catch (Exception e) {
            holder.binding.patientBirthDate.setText(" ");
        }
    }

    private void setGender(PatientViewHolder holder, Patient patient) {
        if (null != patient.getGender()) {
            holder.binding.patientGender.setText(patient.getGender());
            if (Objects.equal(patient.getGender(), newPatient.getGender())) {
                setStyleForMatchedPatientFields(holder.binding.patientGender);
            }
        }
    }

    private void setPatientAdres(PatientViewHolder holder, Patient patient) {
        if (null != patient.getAddress().getAddress1()) {
            holder.binding.patientAddres.setText(patient.getAddress().getAddress1());
            if (Objects.equal(patient.getAddress().getAddress1(), newPatient.getAddress().getAddress1())) {
                setStyleForMatchedPatientFields(holder.binding.patientAddres);
            }
        }
        if (null != patient.getAddress().getPostalCode()) {
            holder.binding.patientPostalCode.setText(patient.getAddress().getPostalCode());
            if (Objects.equal(patient.getAddress().getPostalCode(), newPatient.getAddress().getPostalCode())) {
                setStyleForMatchedPatientFields(holder.binding.patientPostalCode);
            }
        }
        if (null != patient.getAddress().getCityVillage()) {
            holder.binding.patientCity.setText(patient.getAddress().getCityVillage());
            if (Objects.equal(patient.getAddress().getCityVillage(), newPatient.getAddress().getCityVillage())) {
                setStyleForMatchedPatientFields(holder.binding.patientCity);
            }
        }
        if (null != patient.getAddress().getCountry()) {
            holder.binding.patientCountry.setText(patient.getAddress().getCountry());
            if (Objects.equal(patient.getAddress().getCountry(), newPatient.getAddress().getCountry())) {
                setStyleForMatchedPatientFields(holder.binding.patientCountry);
            }
        }
    }

    private void setPatientName(PatientViewHolder holder, Patient patient) {
        if (null != patient.getName().getGivenName()) {
            holder.binding.patientGivenName.setText(patient.getName().getGivenName());
            if (Objects.equal(patient.getName().getGivenName(), newPatient.getName().getGivenName())) {
                setStyleForMatchedPatientFields(holder.binding.patientGivenName);
            }
        }
        if (null != patient.getName().getMiddleName()) {
            holder.binding.patientMiddleName.setText(patient.getName().getMiddleName());
            if (Objects.equal(patient.getName().getMiddleName(), newPatient.getName().getMiddleName())) {
                setStyleForMatchedPatientFields(holder.binding.patientMiddleName);
            }
        }
        if (null != patient.getName().getFamilyName()) {
            holder.binding.patientFamilyName.setText(patient.getName().getFamilyName());
            if (Objects.equal(patient.getName().getFamilyName(), newPatient.getName().getFamilyName())) {
                setStyleForMatchedPatientFields(holder.binding.patientFamilyName);
            }
        }
    }

    private void setStyleForMatchedPatientFields(TextView textView) {
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
