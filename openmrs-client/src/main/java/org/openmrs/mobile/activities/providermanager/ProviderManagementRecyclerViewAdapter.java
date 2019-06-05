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

package org.openmrs.mobile.activities.providermanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class ProviderManagementRecyclerViewAdapter extends
        RecyclerView.Adapter<ProviderManagementRecyclerViewAdapter.ProviderViewHolder> {
    private Context mContext;
    private List<Provider> mItems;

    public ProviderManagementRecyclerViewAdapter(Context context, List<Provider> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ProviderManagementRecyclerViewAdapter.ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_provider_management, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProviderManagementRecyclerViewAdapter.ProviderViewHolder holder, int position) {
        final Provider provider = mItems.get(position);
        if (provider.getPerson().getDisplay() != null) {
            holder.mName.setText(provider.getPerson().getDisplay());
        }

        if (provider.getIdentifier() != null) {
            holder.mIdentifier.setText(provider.getIdentifier());
        }

        holder.mRowLayout.setOnClickListener(v -> {
            // TODO open dashboard for selected Provider
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mName;


        public ProviderViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (ConstraintLayout) itemView;
            mIdentifier = itemView.findViewById(R.id.providerManagementIdentifier);
            mName = itemView.findViewById(R.id.providerManagementName);

        }
    }

    public void setItems(List<Provider> mItems) {
        this.mItems = mItems;
    }
}