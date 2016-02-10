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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.models.VisitItemDTO;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class ActiveVisitsArrayAdapter extends ArrayAdapter<VisitItemDTO> {

    private Context mContext;
    private int mResourceID;
    private List<VisitItemDTO> mVisitList;

    public ActiveVisitsArrayAdapter(Context context, int resource, List<VisitItemDTO> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mResourceID = resource;
        this.mVisitList = items;
    }

    class ViewHolder {
        private TableLayout mTableLayout;
        private TextView mPatientID;
        private TextView mPatientName;
        private TextView mVisitPlace;
        private TextView mVisitStart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResourceID, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mTableLayout = (TableLayout) rowView.findViewById(R.id.visitRow);
            viewHolder.mPatientID = (TextView) rowView.findViewById(R.id.visitPatientID);
            viewHolder.mPatientName = (TextView) rowView.findViewById(R.id.visitPatientName);
            viewHolder.mVisitPlace = (TextView) rowView.findViewById(R.id.patientVisitPlace);
            viewHolder.mVisitStart = (TextView) rowView.findViewById(R.id.patientVisitStartDate);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final VisitItemDTO visit = mVisitList.get(position);
        holder.mPatientName.setText(visit.getPatientName());
        holder.mPatientID.setText("#" + String.valueOf(visit.getPatientIdentifier()));
        holder.mVisitPlace.setText("@ " + visit.getVisitPlace());
        holder.mPatientName.setText(visit.getPatientName());
        holder.mVisitStart.setText(DateUtils.convertTime(visit.getVisitStart()));
        FontsUtil.setFont((ViewGroup) rowView);

        holder.mTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VisitDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, mVisitList.get(position).getVisitID());
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_NAME, mVisitList.get(position).getPatientName());
                mContext.startActivity(intent);
            }
        });

        return rowView;
    }
}
