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

package org.openmrs.mobile.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.AnimationUtils;

import android.widget.LinearLayout;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.FindSyncedPatientsActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

public class SyncedPatientRecyclerViewAdapter extends RecyclerView.Adapter<SyncedPatientRecyclerViewAdapter.PatientViewHolder> {
    private Activity mContext;
    private List<Patient> mItems;
    private PatientDataArrayList patientDataArrayList = new PatientDataArrayList();
    private boolean isUpdatingExistingData = false;

    public SyncedPatientRecyclerViewAdapter(Activity context, List<Patient> items){
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public SyncedPatientRecyclerViewAdapter.PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_synced_patients_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SyncedPatientRecyclerViewAdapter.PatientViewHolder holder, final int position) {
        final Patient patient = mItems.get(position);

        patientDataArrayList.add(new PatientData(holder, patient, position));

        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier().getIdentifier());
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
            holder.mBirthDate.setText(" ");
        }

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PatientDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getUuid());
                mContext.startActivity(intent);
            }
        });

        if (!isUpdatingExistingData) {
            //This logic is preventing list animation when list if filtered by query
            new AnimationUtils().setAnimation(holder.mRowLayout,mContext,position);
        }
    }

    public void setIsFiltering(boolean isFiltering) {
        isUpdatingExistingData = isFiltering;
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(PatientViewHolder holder) {
        holder.clearAnimation();
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
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

        public void clearAnimation() {
            mRowLayout.clearAnimation();
        }
    }

    private class PatientData {
        // class to store patient data
        private int position;
        private Patient patient;
        private PatientViewHolder holder;
        private boolean isSelected = false;

        public PatientData(PatientViewHolder holder, Patient patient, int position) {
            this.holder = holder;
            this.patient = patient;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    private class PatientDataArrayList extends ArrayList<PatientData> {
        public PatientData getPatientDataByPosition(int position) {
            for (int index = 0; index < size(); index++) {
                PatientData patientData = get(index);
                if (patientData.getPosition() == position) {
                    return patientData;
                }
            }
            return null;
        }
    }

    private void updatePatientsInDatabase() {
        ((FindSyncedPatientsActivity) mContext).updatePatientsInDatabaseList();
    }
}
