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

package org.openmrs.mobile.activities.settings;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.logs.LogsActivity;
import org.openmrs.mobile.models.SettingsListItemDTO;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewAdapter.SettingsViewHolder> {

    private List<SettingsListItemDTO> mItems;

    public SettingsRecyclerViewAdapter(List<SettingsListItemDTO> items) {
        this.mItems = items;
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_settings_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new SettingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        holder.mTitle.setText(mItems.get(position).getTitle());

        if (position==0){
            holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext() , LogsActivity.class);
                        v.getContext().startActivity(i);
                        }
                });
        }

        if (mItems.get(position).getDesc1() != null) {
            holder.mDesc1.setText(mItems.get(position).getDesc1());
        }

        if (mItems.get(position).getDesc2() != null) {
            holder.mDesc2.setText(mItems.get(position).getDesc2());
        }

        if (mItems.get(position).getDesc1() == null && mItems.get(position).getDesc2() == null) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) holder.mTitle.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            holder.mTitle.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onViewDetachedFromWindow(SettingsViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mRowLayout;
        private TextView mTitle;
        private TextView mDesc1;
        private TextView mDesc2;

        public SettingsViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (RelativeLayout) itemView;
            mTitle = (TextView) itemView.findViewById(R.id.settingsTitle);
            mDesc1 = (TextView) itemView.findViewById(R.id.settingsDesc1);
            mDesc2 = (TextView) itemView.findViewById(R.id.settingsDesc2);
        }

        public void clearAnimation() {
            mRowLayout.clearAnimation();
        }
    }


}
