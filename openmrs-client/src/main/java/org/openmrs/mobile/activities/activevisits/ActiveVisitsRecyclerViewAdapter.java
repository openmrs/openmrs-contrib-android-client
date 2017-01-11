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

package org.openmrs.mobile.activities.activevisits;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class ActiveVisitsRecyclerViewAdapter extends RecyclerView.Adapter<ActiveVisitsRecyclerViewAdapter.VisitViewHolder> {
    private Context mContext;
    private List<Visit> mVisits;

    public ActiveVisitsRecyclerViewAdapter(Context context, List<Visit> items) {
        this.mContext = context;
        this.mVisits = items;
    }

    @Override
    public VisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_visits_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new VisitViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VisitViewHolder visitViewHolder, final int position) {
        final int adapterPos = visitViewHolder.getAdapterPosition();
        Visit visit = mVisits.get(adapterPos);
        Patient patient = new PatientDAO().findPatientByID(visit.getPatient().getId().toString());

        visitViewHolder.mVisitPlace.setText(mContext.getString(R.string.visit_in, visit.getLocation().getDisplay()));

        if (null != visit.getPatient().getId()) {
            final String display = "#" + patient.getIdentifier().getIdentifier();
            visitViewHolder.mIdentifier.setText(display);
        }
        if (null != patient.getPerson().getName()) {
            visitViewHolder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            visitViewHolder.mGender.setText(patient.getPerson().getGender());
        }
        try{
            visitViewHolder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        }
        catch (Exception e)
        {
            visitViewHolder.mBirthDate.setText(" ");
        }

        visitViewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VisitDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, mVisits.get(adapterPos).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(VisitViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mVisits.size();
    }

    class VisitViewHolder extends RecyclerView.ViewHolder{
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mBirthDate;
        private TextView mVisitPlace;
        private LinearLayout mRelativeLayout;

        public VisitViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.findVisitsIdentifier);
            mDisplayName = (TextView) itemView.findViewById(R.id.findVisitsDisplayName);
            mVisitPlace = (TextView) itemView.findViewById(R.id.findVisitsPlace);
            mBirthDate = (TextView) itemView.findViewById(R.id.findVisitsPatientBirthDate);
            mGender = (TextView) itemView.findViewById(R.id.findVisitsPatientGender);
        }
        public void clearAnimation() {
            mRelativeLayout.clearAnimation();
        }
    }
}
