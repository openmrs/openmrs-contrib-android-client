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

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.models.VisitItemDTO;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class ActiveVisitsRecyclerViewAdapter extends RecyclerView.Adapter<ActiveVisitsRecyclerViewAdapter.VisitViewHolder> {

    private Context mContext;
    private List<Visit> mVisitList;

    public ActiveVisitsRecyclerViewAdapter(Context context, List<Visit> items) {
        this.mContext = context;
        this.mVisitList = items;
    }

    @Override
    public VisitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_visits_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new VisitViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VisitViewHolder visitViewHolder, final int position) {
        final Visit visit = mVisitList.get(position);
        Patient patient=new PatientDAO().findPatientByID(Long.toString(visit.getPatientID()));
        visitViewHolder.mPatientName.setText(patient.getPerson().getName().getNameString());
        visitViewHolder.mPatientID.setText("#" + String.valueOf(patient.getIdentifier().getIdentifier()));
        visitViewHolder.mVisitPlace.setText("@ " + visit.getVisitPlace());
        visitViewHolder.mVisitStart.setText(DateUtils.convertTime(visit.getStartDate()));

        visitViewHolder.mTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VisitDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, mVisitList.get(position).getId());
                mContext.startActivity(intent);
            }
        });
        new AnimationUtils().setAnimation(visitViewHolder.mTableLayout,mContext,position);

    }

    @Override
    public void onViewDetachedFromWindow(VisitViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mVisitList.size();
    }

    class VisitViewHolder extends RecyclerView.ViewHolder {
        private TableLayout mTableLayout;
        private TextView mPatientID;
        private TextView mPatientName;
        private TextView mVisitPlace;
        private TextView mVisitStart;

        public VisitViewHolder(View itemView) {
            super(itemView);
            mTableLayout = (TableLayout) itemView.findViewById(R.id.visitRow);
            mPatientID = (TextView) itemView.findViewById(R.id.visitPatientID);
            mPatientName = (TextView) itemView.findViewById(R.id.visitPatientName);
            mVisitPlace = (TextView) itemView.findViewById(R.id.patientVisitPlace);
            mVisitStart = (TextView) itemView.findViewById(R.id.patientVisitStartDate);
        }

        public void clearAnimation() {
            mTableLayout.clearAnimation();
        }
    }

}
