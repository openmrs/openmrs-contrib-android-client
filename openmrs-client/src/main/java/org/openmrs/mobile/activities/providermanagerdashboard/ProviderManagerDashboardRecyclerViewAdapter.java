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

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardActivity;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderActivity;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.RequestCodes.EDIT_PROVIDER_REQ_CODE;

public class ProviderManagerDashboardRecyclerViewAdapter extends
        RecyclerView.Adapter<ProviderManagerDashboardRecyclerViewAdapter.ProviderViewHolder> {
    private Fragment fragment;
    private List<Provider> mItems;
    private ProviderManagerDashboardContract.Presenter presenter;

    public ProviderManagerDashboardRecyclerViewAdapter(Fragment fragment, ProviderManagerDashboardContract.Presenter presenter, List<Provider> items) {
        this.fragment = fragment;
        this.mItems = items;
        this.presenter = presenter;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_provider_management, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProviderViewHolder holder, int position) {
        final Provider provider = mItems.get(position);
        if (provider.getPerson().getDisplay() != null) {
            holder.mName.setText(provider.getPerson().getDisplay());
        }

        if (provider.getIdentifier() != null) {
            holder.mIdentifier.setText(provider.getIdentifier());
        }

        holder.deleteIv.setOnClickListener(view -> {
            createDeleteDialogBox(provider);
        });

        holder.editIv.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.getContext(), AddProviderActivity.class);
            ArrayList<Provider> providerArrayList = new ArrayList<>(mItems);
            intent.putExtra(ApplicationConstants.BundleKeys.EXISTING_PROVIDERS_BUNDLE, providerArrayList);
            intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider);
            fragment.startActivityForResult(intent, EDIT_PROVIDER_REQ_CODE);
        });

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

    private void createDeleteDialogBox(Provider provider) {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragment.getContext());
        alertDialogBuilder.setTitle(R.string.dialog_title_are_you_sure);

        alertDialogBuilder
                .setMessage(R.string.dialog_provider_retired)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, id) -> {
                    presenter.deleteProvider(provider.getUuid());
                    dialog.cancel();
                })
                .setNegativeButton(R.string.dialog_button_cancel, (dialog, id) -> {
                    dialog.dismiss();
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private CardView mRowLayout;
        private TextView mIdentifier;
        private TextView mName;
        private ImageView deleteIv, editIv;


        public ProviderViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (CardView) itemView.findViewById(R.id.provider_management_foreground_card_view);
            mIdentifier = itemView.findViewById(R.id.providerManagementIdentifier);
            mName = itemView.findViewById(R.id.providerManagementName);
            deleteIv = itemView.findViewById(R.id.row_provider_management_delete_iv);
            editIv = itemView.findViewById(R.id.row_provider_management_edit_iv);

        }
    }

    public void setItems(List<Provider> mItems) {
        this.mItems = mItems;
    }
}