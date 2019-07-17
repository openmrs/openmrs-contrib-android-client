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

package org.openmrs.mobile.activities.formentrypatientlist;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class FormEntryPatientListAdapter extends RecyclerView.Adapter<FormEntryPatientListAdapter.PatientViewHolder> {
    private FormEntryPatientListFragment mContext;
    private List<Patient> mItems;

    public FormEntryPatientListAdapter(FormEntryPatientListFragment context, List<Patient> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_patient_details, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, final int position) {
        final int adapterPos = holder.getAdapterPosition();
        final Patient patient = mItems.get(adapterPos);
        new VisitDAO().getActiveVisitByPatientId(patient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    if (visit != null) {
                        Drawable icon = mContext.getResources().getDrawable(R.drawable.active_visit_dot);
                        icon.setBounds(0, 0, icon.getIntrinsicHeight(), icon.getIntrinsicWidth());
                        holder.mVisitStatus.setCompoundDrawables(icon, null, null, null);
                        holder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label_capture_vitals));

                        holder.mRowLayout.setOnClickListener(v ->
                                mContext.startEncounterForPatient(mItems.get(adapterPos).getId()));
                    } else {
                        holder.mVisitStatus.setText(ApplicationConstants.EMPTY_STRING);

                        holder.mRowLayout.setOnClickListener(v ->
                                mContext.showSnackbarInactivePatients(v));
                    }
                });
        if (null != patient.getIdentifier()) {
            final String display = "#" + patient.getIdentifier().getIdentifier();
            holder.mIdentifier.setText(display);
        }
        if (null != patient.getName()) {
            holder.mDisplayName.setText(patient.getName().getNameString());
        }
        if (null != patient.getGender()) {
            holder.mGender.setText(patient.getGender());
        }

        holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getBirthdate())));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PatientViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        private CardView mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;
        private TextView mVisitStatus;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mVisitStatus = itemView.findViewById(R.id.visitStatusLabel);
            mRowLayout = (CardView) itemView;
            mIdentifier = itemView.findViewById(R.id.syncedPatientIdentifier);
            mDisplayName = itemView.findViewById(R.id.syncedPatientDisplayName);
            mGender = itemView.findViewById(R.id.syncedPatientGender);
            mAge = itemView.findViewById(R.id.syncedPatientAge);
            mBirthDate = itemView.findViewById(R.id.syncedPatientBirthDate);
        }

        public void clearAnimation() {
            mRowLayout.clearAnimation();
        }
    }

}
