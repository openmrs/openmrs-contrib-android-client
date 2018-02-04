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
package org.openmrs.mobile.activities.manageappointment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.appointment.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManageAppointmentRecyclerViewAdapter extends RecyclerView.Adapter<ManageAppointmentRecyclerViewAdapter.ServicesViewHolder> {
    private ManageAppointmentFragment mContext;
    private List<Result> mItems;

    public ManageAppointmentRecyclerViewAdapter(ManageAppointmentFragment context, List<Result> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ManageAppointmentRecyclerViewAdapter.ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_appointment_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ManageAppointmentRecyclerViewAdapter.ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManageAppointmentRecyclerViewAdapter.ServicesViewHolder holder, final int position) {
        final Result result = mItems.get(position);
        if (null != result.getPatient()) {
for (int i=0;i<result.getPatient().getIdentifiers().size();i++){
    holder.midentifierName.append(result.getPatient().getIdentifiers().get(i).getDisplay());
}

            holder.mAge.setText(result.getPatient().getPerson().getAge().toString());
            holder.mpatientName.setText(result.getPatient().getPerson().getDisplay());
            holder.mGender.setText(result.getPatient().getPerson().getGender());
        }
        if (null!=result.getTimeSlot().getStartDate())
        {
            Date date = DateUtils.getDateFromString(result.getTimeSlot().getStartDate());
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
            holder.mDate.setText(dateFormat.format(date));
            Long startTime = DateUtils.convertTime(result.getTimeSlot().getStartDate());
            DateFormat timeFormat = new SimpleDateFormat("HH:ss");
            holder.mDate.append(" "+timeFormat.format(startTime));
            Long endTime = DateUtils.convertTime(result.getTimeSlot().getEndDate());
            DateFormat endTimeFormat = new SimpleDateFormat("HH:ss");
            holder.mDate.append("-"+endTimeFormat.format(endTime));
        }
        if (null != result.getStatus()) {
            holder.mStatus.setText(result.getStatus().getName());
        }
        if (null != result.getAppointmentType()) {
            holder.mStatus.append(" "+result.getAppointmentType().getDisplay());
        }
        if (null != result.getTimeSlot().getAppointmentBlock().getLocation()) {
            holder.mStatus.append(" "+result.getTimeSlot().getAppointmentBlock().getLocation().getDisplay());
        }
        if (null != result.getTimeSlot().getAppointmentBlock().getProvider()) {
            holder.mStatus.append(" "+result.getTimeSlot().getAppointmentBlock().getProvider().getDisplay());
        }

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.openDialog(result);
            }
        });



    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }


    class ServicesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView midentifierName;
        private TextView mpatientName;


        private TextView mAge;
        private TextView mDate;
        private TextView mGender;
        private TextView mStatus;


        public ServicesViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            midentifierName = (TextView) itemView.findViewById(R.id.identifierName);
            mpatientName = (TextView) itemView.findViewById(R.id.patientName);
            mAge = (TextView) itemView.findViewById(R.id.age);
            mDate = (TextView) itemView.findViewById(R.id.birthDate);
            mGender = (TextView) itemView.findViewById(R.id.gender);
            mStatus = (TextView) itemView.findViewById(R.id.status);
        }
    }
}


