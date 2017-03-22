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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class FormEntryPatientListAdapter extends RecyclerView.Adapter<FormEntryPatientListAdapter.PatientViewHolder> {
    private FormEntryPatientListFragment mContext;
    private List<Patient> mItems;

    public FormEntryPatientListAdapter(FormEntryPatientListFragment context, List<Patient> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_details_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, final int position) {
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
                    } else {
                        holder.mVisitStatus.setText(ApplicationConstants.EMPTY_STRING);
                    }
                });
        if (null != patient.getIdentifier()) {
            final String display = "#" + patient.getIdentifier().getIdentifier();
            holder.mIdentifier.setText(display);
        }
        if (null != patient.getPerson().getName()) {
            holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
        }

        holder.mRowLayout.setOnClickListener(v ->
                mContext.startEncounterForPatient(mItems.get(adapterPos).getId()));

        holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
    }

    @Override
    public void onViewDetachedFromWindow(PatientViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;
        private TextView mVisitStatus;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mVisitStatus = (TextView) itemView.findViewById(R.id.visitStatusLabel);
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

}
