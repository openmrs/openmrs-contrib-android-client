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

package org.openmrs.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.SettingsListItemDTO;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class SettingsArrayAdapter extends ArrayAdapter<SettingsListItemDTO> {
    private Activity mContext;
    private List<SettingsListItemDTO> mItems;

    class ViewHolder {
        private TextView mTitle;
        private TextView mDesc1;
        private TextView mDesc2;
    }

    public SettingsArrayAdapter(Activity context, List<SettingsListItemDTO> items) {
        super(context, R.layout.activity_settings_row, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.activity_settings_row, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mTitle = (TextView) rowView.findViewById(R.id.settings_title);
            viewHolder.mDesc1 = (TextView) rowView.findViewById(R.id.settings_desc1);
            viewHolder.mDesc2 = (TextView) rowView.findViewById(R.id.settings_desc2);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.mTitle.setText(mItems.get(position).getTitle());

        if (mItems.get(position).getDesc1() != null) {
            holder.mDesc1.setText(mItems.get(position).getDesc1());
        }

        if (mItems.get(position).getDesc2() != null) {
            holder.mDesc2.setText(mItems.get(position).getDesc2());
        }

        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }
}
