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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.SettingsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.SettingsListItemDTO;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class SettingsArrayAdapter extends ArrayAdapter<SettingsListItemDTO> {
    private Activity mContext;
    private List<SettingsListItemDTO> mItems;

    class ViewHolder {
        private TextView mTitle;
        private TextView mDesc1;
        private TextView mDesc2;
        private View switchButton;
        private RelativeLayout mRelativeLayout;
    }

    public SettingsArrayAdapter(Activity context, List<SettingsListItemDTO> items) {
        super(context, R.layout.activity_settings_row, items);
        this.mContext = context;
        this.mItems = items;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.activity_settings_row, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mRelativeLayout = (RelativeLayout) rowView.findViewById(R.id.settingsRow);
            viewHolder.mTitle = (TextView) rowView.findViewById(R.id.settingsTitle);
            viewHolder.mDesc1 = (TextView) rowView.findViewById(R.id.settingsDesc1);
            viewHolder.mDesc2 = (TextView) rowView.findViewById(R.id.settingsDesc2);
            viewHolder.switchButton = rowView.findViewById(R.id.settingsOnlineMode);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.mTitle.setText(mItems.get(position).getTitle());

        if (mItems.get(position).isVisibleSwitch()) {
            setTitleCenter(holder);
            holder.switchButton.setVisibility(View.VISIBLE);
            if (OpenMRS.getInstance().isRunningIceCreamVersionOrHigher()) {
                ((Switch) holder.switchButton).setChecked(OpenMRS.getInstance().getOnlineMode());
            } else {
                ((ToggleButton) holder.switchButton).setChecked(OpenMRS.getInstance().getOnlineMode());
            }
        }

        if (position == 1) {
            setTitleCenter(holder);
        }

        if (mItems.get(position).getDesc1() != null) {
            holder.mDesc1.setText(mItems.get(position).getDesc1());
        }

        if (mItems.get(position).getDesc2() != null) {
            holder.mDesc2.setText(mItems.get(position).getDesc2());
        }

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpenMRS.getInstance().getOnlineMode() && position == 1) {
                    ((SettingsActivity) mContext).sendAllOldRequestOneByOne();
                }
            }
        });

        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }

    private void setTitleCenter(ViewHolder holder) {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) holder.mTitle.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        holder.mTitle.setLayoutParams(layoutParams);
    }
}
