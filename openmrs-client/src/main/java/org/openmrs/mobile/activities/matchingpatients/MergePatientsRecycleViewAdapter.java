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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Objects;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class MergePatientsRecycleViewAdapter extends RecyclerView.Adapter<MergePatientsRecycleViewAdapter.PatientViewHolder> {

    private List<Patient> patientList;
    private Patient newPatient;
    private Activity mContext;
    private MatchingPatientsContract.Presenter mPresenter;
    private int selectedPosition = -1;

    public MergePatientsRecycleViewAdapter(Activity mContext, MatchingPatientsContract.Presenter presenter, List<Patient> patientList, Patient patient) {
        this.newPatient = patient;
        this.patientList = patientList;
        this.mContext = mContext;
        this.mPresenter = presenter;
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
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {

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
            mGivenName = itemView.findViewById(R.id.patientGivenName);
            mMiddleName = itemView.findViewById(R.id.patientMiddleName);
            mFamilyName = itemView.findViewById(R.id.patientFamilyName);
            mGender = itemView.findViewById(R.id.patientGender);
            mBirthDate = itemView.findViewById(R.id.patientBirthDate);
            mAddres = itemView.findViewById(R.id.patientAddres);
            mPostalCode = itemView.findViewById(R.id.patientPostalCode);
            mCity = itemView.findViewById(R.id.patientCity);
            mCountry = itemView.findViewById(R.id.patientCountry);
            itemView.setOnClickListener(view -> {
                CardView cardView = view.findViewById(R.id.cardView);
                if (selectedPosition == -1) {
                    selectedPosition = getAdapterPosition();
                    mPresenter.setSelectedPatient(patientList.get(selectedPosition));
                    cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.patient_selected_highlight));
                } else if (selectedPosition == getAdapterPosition()) {
                    selectedPosition = -1;
                    mPresenter.removeSelectedPatient();
                    cardView.setCardBackgroundColor(Color.WHITE);
                } else {
                    ToastUtil.notify("You can select only one similar patient");
                }
            });
        }

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
