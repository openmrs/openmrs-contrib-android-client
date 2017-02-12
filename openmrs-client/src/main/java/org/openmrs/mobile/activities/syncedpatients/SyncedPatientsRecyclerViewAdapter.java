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

package org.openmrs.mobile.activities.syncedpatients;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class SyncedPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SyncedPatientsRecyclerViewAdapter.PatientViewHolder> {
    private SyncedPatientsFragment mContext;
    private List<Patient> mItems;

    public SyncedPatientsRecyclerViewAdapter(SyncedPatientsFragment context, List<Patient> items){
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public SyncedPatientsRecyclerViewAdapter.PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_synced_patients_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SyncedPatientsRecyclerViewAdapter.PatientViewHolder holder, final int position) {
        final Patient patient = mItems.get(position);

        if (null != patient.getIdentifier()) {
            String patientIdentifier = String.format(mContext.getResources().getString(R.string.patient_identifier),
                    patient.getIdentifier().getIdentifier());
            holder.mIdentifier.setText(patientIdentifier);
        }
        if (null != patient.getPerson().getName()) {
            holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
        }
        try{
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        }
        catch (Exception e)
        {
            holder.mBirthDate.setText("");
        }

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getActivity(), PatientDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.syncedPatientIdentifier);
            mDisplayName = (TextView) itemView.findViewById(R.id.syncedPatientDisplayName);
            mGender = (TextView) itemView.findViewById(R.id.syncedPatientGender);
            mAge = (TextView) itemView.findViewById(R.id.syncedPatientAge);
            mBirthDate = (TextView) itemView.findViewById(R.id.syncedPatientBirthDate);
        }
    }
}
