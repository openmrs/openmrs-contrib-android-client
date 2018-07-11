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
package org.openmrs.mobile.activities.formappointmentrequest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.timeblocks.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FormAppointmentRequestRecyclerViewAdapter extends RecyclerView.Adapter<FormAppointmentRequestRecyclerViewAdapter.ServicesViewHolder> {

    public final FormAppointmentRequestFragment mContext;
    private List<Result> mItems;


    public FormAppointmentRequestRecyclerViewAdapter(FormAppointmentRequestFragment context, List<Result> items) {
        this.mContext = context;
        this.mItems = items;

    }

    @Override
    public FormAppointmentRequestRecyclerViewAdapter.ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_time_blocks_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new FormAppointmentRequestRecyclerViewAdapter.ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FormAppointmentRequestRecyclerViewAdapter.ServicesViewHolder holder, final int position) {
        final Result result = mItems.get(position);

        if (null != result.getAppointmentBlock().getProvider()) {
            holder.mProviderName.setText(result.getAppointmentBlock().getProvider().getPerson().getDisplay());
        }
        if (null != result.getCountOfAppointments()) {
            holder.mAppointments.setText(result.getCountOfAppointments()+" Appointments "+result.getUnallocatedMinutes()+"mins available");
        }
        if (null != result.getAppointmentBlock().getLocation()) {
            holder.mLocation.setText(result.getAppointmentBlock().getLocation().getDisplay());
        }
        if (null != result.getStartDate()) {
            Date date = DateUtils.getDateFromString(result.getStartDate());
            DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
            holder.mTime.setText(dateFormat.format(date));
        }
        if (null != result.getEndDate()) {
            Date date = DateUtils.getDateFromString(result.getEndDate());
            DateFormat dateFormat = new SimpleDateFormat("dd MM yy");
            holder.mTime.append(" to "+dateFormat.format(date));

    }

        if (null != result.getAppointmentBlock().getTypes()) {
         for(int i=0;i<result.getAppointmentBlock().getTypes().size();i++) {
             holder.mType.append(result.getAppointmentBlock().getTypes().get(i).getDisplay()+" ");
         }
        }
        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This method is left blank intentionally.
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
        private TextView mAppointments;
        private TextView mLocation;
        private TextView mTime;
        private TextView mType;
        public ServicesViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mProviderName = (TextView) itemView.findViewById(R.id.providerName);
            mAppointments = (TextView) itemView.findViewById(R.id.appointments);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mTime = (TextView) itemView.findViewById(R.id.timeBlocks);
            mType = (TextView) itemView.findViewById(R.id.appointmentType);

        }
    }
}


