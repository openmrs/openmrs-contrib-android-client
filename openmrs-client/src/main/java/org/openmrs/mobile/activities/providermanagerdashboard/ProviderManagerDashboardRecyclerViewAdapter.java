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

package org.openmrs.mobile.activities.providermanagerdashboard;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.openmrs.android_sdk.library.models.Provider;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardActivity;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

import java.util.List;

public class ProviderManagerDashboardRecyclerViewAdapter extends
    RecyclerView.Adapter<ProviderManagerDashboardRecyclerViewAdapter.ProviderViewHolder> {
    private Fragment fragment;
    private List<Provider> mItems;

    public ProviderManagerDashboardRecyclerViewAdapter(Fragment fragment, ProviderManagerDashboardContract.Presenter presenter, List<Provider> items) {
        this.fragment = fragment;
        this.mItems = items;
    }

    @NotNull
    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_provider_management, parent, false);
        return new ProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull ProviderViewHolder holder, int position) {
        final Provider provider = mItems.get(position);
        if (provider.getPerson().getDisplay()!=null) {
            holder.mName.setText(provider.getPerson().getDisplay());
        }

        if (provider.getIdentifier() != null) {
            holder.mIdentifier.setText(provider.getIdentifier());
        }

        holder.mRowLayout.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.getContext(), ProviderDashboardActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider);
            fragment.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private CardView mRowLayout;
        private TextView mIdentifier;
        private TextView mName;

        public ProviderViewHolder(View itemView) {
            super(itemView);
            mRowLayout = itemView.findViewById(R.id.provider_management_foreground_card_view);
            mIdentifier = itemView.findViewById(R.id.providerManagementIdentifier);
            mName = itemView.findViewById(R.id.providerManagementName);
        }
    }

    public void setItems(List<Provider> mItems) {
        this.mItems = mItems;
    }
}