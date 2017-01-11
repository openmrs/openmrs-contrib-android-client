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
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.List;

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
        if (new VisitDAO().isPatientNowOnVisit(patient.getId())) {
            holder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.active_visit_dot,
                            holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label_capture_vitals));
        }
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

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startEncounterForPatient(mItems.get(adapterPos).getId());
            }
        });

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
        private ImageView mVisitStatusIcon;
        private TextView mVisitStatus;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mVisitStatusIcon = (ImageView) itemView.findViewById(R.id.visitStatusIcon);
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
