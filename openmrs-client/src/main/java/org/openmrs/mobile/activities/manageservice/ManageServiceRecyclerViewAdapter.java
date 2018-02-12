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
package org.openmrs.mobile.activities.manageservice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class ManageServiceRecyclerViewAdapter extends RecyclerView.Adapter<ManageServiceRecyclerViewAdapter.ServicesViewHolder> {
    private ManageServiceFragment mContext;
    private List<Services> mItems;

    public ManageServiceRecyclerViewAdapter(ManageServiceFragment context, List<Services> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ManageServiceRecyclerViewAdapter.ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_services_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ManageServiceRecyclerViewAdapter.ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManageServiceRecyclerViewAdapter.ServicesViewHolder holder, final int position) {
        final Services services = mItems.get(position);

      if (null != services.getName()) {
            holder.mServiceName.setText(services.getName());
        }
        if (null != services.getDuration()) {
            holder.mServiceDuration.setText(String.valueOf(services.getDuration()));
        }
        if (null != services.getDescription()) {
            holder.mServiceDescription.setText(String.valueOf(services.getDescription()));
        }
        if (null == services.getDescription()) {
            holder.mServiceDescription.setText("No Discription");
        }
        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mContext.showDialog(services);

            }
        });
    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }


    class ServicesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mServiceName;
        private TextView mServiceDuration;
        private TextView mServiceDescription;


        public ServicesViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mServiceName = (TextView) itemView.findViewById(R.id.serviceName);
            mServiceDuration = (TextView) itemView.findViewById(R.id.serviceDuration);
            mServiceDescription = (TextView) itemView.findViewById(R.id.serviceDiscription);
        }
    }
}


