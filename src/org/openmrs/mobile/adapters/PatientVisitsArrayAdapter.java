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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.List;

public class PatientVisitsArrayAdapter extends ArrayAdapter<Visit> {
    private Context mContext;
    private List<Visit> mVisits;

    public PatientVisitsArrayAdapter(Context context, List<Visit> items) {
        super(context, R.layout.patient_visit_row, items);
        this.mContext = context;
        this.mVisits = items;
    }

    class ViewHolder {
        private TextView mVisitPlace;
        private TextView mVisitStart;
        private TextView mVisitEnd;
        private TextView mVisitStatus;
        private RelativeLayout mRelativeLayout;
        private ImageView mVisitStatusIcon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.patient_visit_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mRelativeLayout = (RelativeLayout) rowView.findViewById(R.id.visitRow);
            viewHolder.mVisitStart = (TextView) rowView.findViewById(R.id.patientVisitStartDate);
            viewHolder.mVisitEnd = (TextView) rowView.findViewById(R.id.patientVisitEndDate);
            viewHolder.mVisitPlace = (TextView) rowView.findViewById(R.id.patientVisitPlace);
            viewHolder.mVisitStatusIcon = (ImageView) rowView.findViewById(R.id.visitStatusIcon);
            viewHolder.mVisitStatus = (TextView) rowView.findViewById(R.id.visitStatusLabel);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        Visit visit = mVisits.get(position);
        holder.mVisitStart.setText(DateUtils.convertTime(visit.getStartDate(), DateUtils.DATE_WITH_TIME_FORMAT));
        if (!DateUtils.ZERO.equals(visit.getStopDate())) {
            holder.mVisitEnd.setVisibility(View.VISIBLE);
            holder.mVisitEnd.setText(DateUtils.convertTime(visit.getStopDate(), DateUtils.DATE_WITH_TIME_FORMAT));

            holder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.past_visit_dot,
                            holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.past_visit_label));
        } else {
            holder.mVisitEnd.setVisibility(View.INVISIBLE);
            holder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.active_visit_dot,
                            holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label));
        }
        holder.mVisitPlace.setText(mContext.getString(R.string.visit_in, visit.getVisitPlace()));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientDashboardActivity) mContext).goToVisitDashboard(mVisits.get(position).getId());
            }
        });
        return rowView;
    }
}
