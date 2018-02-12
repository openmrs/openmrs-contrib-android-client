
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
package org.openmrs.mobile.activities.appointmentrequests;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.appointmentrequestmodel.Result;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;


public class AppointmentRequestsRecyclerViewAdapter extends RecyclerView.Adapter<AppointmentRequestsRecyclerViewAdapter.AppointmentRequestViewHolder> {
    public AppointmentRequestsFragment mContext;
    private  List<Result> mItemsResult;

    public AppointmentRequestsRecyclerViewAdapter(AppointmentRequestsFragment context, List<Result> items) {
        this.mContext = context;
        this.mItemsResult = items;
    }

    @Override
    public AppointmentRequestsRecyclerViewAdapter.AppointmentRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_appointment_request_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new AppointmentRequestsRecyclerViewAdapter.AppointmentRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppointmentRequestsRecyclerViewAdapter.AppointmentRequestViewHolder holder, final int position) {
        final Result result = mItemsResult.get(position);
        if (null != result.getPatient()) {
            holder.mPersonName.setText(result.getPatient().getPerson().getDisplay());
        }
        if (null != result.getAppointmentType()) {
            holder.mServiceType.setText(result.getAppointmentType().getDisplay());
        }
        if (null != result.getProvider()) {
            holder.mServiceProvider.setText(result.getProvider().getPerson().getDisplay());
        }
        else {
            holder.mServiceProvider.setText("No Provider Filled");
        }
        if (null != result.getMinTimeFrameValue()||null != result.getMaxTimeFrameValue()) {
            holder.mTime.setText(result.getMinTimeFrameValue()+" "+result.getMinTimeFrameUnits()+" - "+result.getMaxTimeFrameValue()+" "+result.getMaxTimeFrameUnits());
        }
        if (null != result.getNotes()) {
            holder.mNotes.setText(result.getNotes());
        }
        else {
            holder.mNotes.setText("No Notes");
        }
        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getForm(result);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemsResult.size();
    }


    class AppointmentRequestViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mServiceType;
        private TextView mServiceProvider;
        private TextView mPersonName;
        private TextView mTime;
        private TextView mNotes;


        public AppointmentRequestViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mPersonName = (TextView) itemView.findViewById(R.id.personName);
            mServiceType = (TextView) itemView.findViewById(R.id.serviceType);
            mServiceProvider = (TextView) itemView.findViewById(R.id.serviceProvider);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mNotes = (TextView) itemView.findViewById(R.id.notes);
        }
    }
}
