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

package org.openmrs.mobile.activities.manageappointmentblocks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.appointmentblocksmodel.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManageAppointmentBlocksRecyclerViewAdapter extends RecyclerView.Adapter<ManageAppointmentBlocksRecyclerViewAdapter.ServicesViewHolder> {
    private ManageAppointmentBlocksFragment mContext;
    private List<Result> mItems;

    public ManageAppointmentBlocksRecyclerViewAdapter(ManageAppointmentBlocksFragment context, List<Result> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ManageAppointmentBlocksRecyclerViewAdapter.ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_appointment_blocks_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ManageAppointmentBlocksRecyclerViewAdapter.ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManageAppointmentBlocksRecyclerViewAdapter.ServicesViewHolder holder, final int position) {
        final Result result = mItems.get(position);

        if (null != result.getProvider()) {
            holder.mProviderName.setText(result.getProvider().getPerson().getDisplay());
        }

        if (null != result.getLocation()) {
            holder.mLocation.setText(result.getLocation().getDisplay());
        }
        if (null != result.getTypes()) {
            for (int i=0;i<result.getTypes().size();i++){
                holder.mAppointmentType.append(result.getTypes().get(i).getDisplay()+" "); }

        }
        if (null != result.getStartDate()) {
            Date date = DateUtils.getDateFromString(result.getStartDate());
            DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
            holder.mDate.setText(dateFormat.format(date));

        }

        if (null != result.getEndDate()) {
            Long date = DateUtils.convertTime(result.getStartDate());
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Long dateend = DateUtils.convertTime(result.getEndDate());
            DateFormat dateFormatend = new SimpleDateFormat("HH:mm");
            holder.mTime.setText(dateFormat.format(date)+" - "+dateFormatend.format(dateend));
        }


        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mContext.openForm(result);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class ServicesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mProviderName;
        private TextView mAppointmentType;
        private TextView mDate;
        private TextView mTime;
        private TextView mLocation;


        public ServicesViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mProviderName = (TextView) itemView.findViewById(R.id.providerName);
            mAppointmentType = (TextView) itemView.findViewById(R.id.appointmentType);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mLocation = (TextView) itemView.findViewById(R.id.location);

        }
    }
}


