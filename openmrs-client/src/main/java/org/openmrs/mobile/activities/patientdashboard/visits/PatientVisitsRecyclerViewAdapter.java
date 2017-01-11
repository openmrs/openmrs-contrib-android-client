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

package org.openmrs.mobile.activities.patientdashboard.visits;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.List;

public class PatientVisitsRecyclerViewAdapter extends RecyclerView.Adapter<PatientVisitsRecyclerViewAdapter.VisitViewHolder> {
    private PatientVisitsFragment mContext;
    private List<Visit> mVisits;


    public PatientVisitsRecyclerViewAdapter(PatientVisitsFragment context, List<Visit> items) {
        this.mContext = context;
        this.mVisits = items;
    }

    @Override
    public VisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_visit_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new VisitViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VisitViewHolder visitViewHolder, final int position) {
        final int adapterPos = visitViewHolder.getAdapterPosition();
        Visit visit = mVisits.get(adapterPos);
        visitViewHolder.mVisitStart.setText(DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
        if (DateUtils.convertTime(visit.getStopDatetime()) != null) {
            visitViewHolder.mVisitEnd.setVisibility(View.VISIBLE);
            visitViewHolder.mVisitEnd.setText(DateUtils.convertTime1((visit.getStopDatetime()), DateUtils.DATE_WITH_TIME_FORMAT));

            visitViewHolder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.past_visit_dot,
                            visitViewHolder.mVisitStatusIcon.getLayoutParams().width, visitViewHolder.mVisitStatusIcon.getLayoutParams().height));
            visitViewHolder.mVisitStatus.setText(mContext.getString(R.string.past_visit_label));
        } else {
            visitViewHolder.mVisitEnd.setVisibility(View.INVISIBLE);
            visitViewHolder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.active_visit_dot,
                            visitViewHolder.mVisitStatusIcon.getLayoutParams().width, visitViewHolder.mVisitStatusIcon.getLayoutParams().height));
            visitViewHolder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label));
        }
        if (visit.getLocation() != null) {
            visitViewHolder.mVisitPlace.setText(mContext.getString(R.string.visit_in, visit.getLocation().getDisplay()));
        }

        visitViewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.goToVisitDashboard(mVisits.get(adapterPos).getId());
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
        private TextView mVisitPlace;
        private TextView mVisitStart;
        private TextView mVisitEnd;
        private TextView mVisitStatus;
        private RelativeLayout mRelativeLayout;
        private ImageView mVisitStatusIcon;

        public VisitViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (RelativeLayout) itemView;
            mVisitStart = (TextView) itemView.findViewById(R.id.patientVisitStartDate);
            mVisitEnd = (TextView) itemView.findViewById(R.id.patientVisitEndDate);
            mVisitPlace = (TextView) itemView.findViewById(R.id.patientVisitPlace);
            mVisitStatusIcon = (ImageView) itemView.findViewById(R.id.visitStatusIcon);
            mVisitStatus = (TextView) itemView.findViewById(R.id.visitStatusLabel);
        }
        public void clearAnimation() {
            mRelativeLayout.clearAnimation();
        }
    }
}
