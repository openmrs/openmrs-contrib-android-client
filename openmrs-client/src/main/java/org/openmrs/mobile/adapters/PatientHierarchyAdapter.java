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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.CaptureVitalsActivity;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.List;

public class PatientHierarchyAdapter extends ArrayAdapter<Patient> {
    private Activity mContext;
    private List<Patient> mItems;
    private int mResourceID;

    class ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;
        private ImageView mVisitStatusIcon;
        private TextView mVisitStatus;
    }

    public PatientHierarchyAdapter(Activity context, int resourceID, List<Patient> items) {
        super(context, resourceID, items);
        this.mContext = context;
        this.mItems = items;
        this.mResourceID = resourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(mResourceID, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mVisitStatusIcon = (ImageView) rowView.findViewById(R.id.visitStatusIcon);
            viewHolder.mVisitStatus = (TextView) rowView.findViewById(R.id.visitStatusLabel);
            viewHolder.mRowLayout = (LinearLayout) rowView;
            viewHolder.mIdentifier = (TextView) rowView.findViewById(R.id.patientIdentifier);
            viewHolder.mDisplayName = (TextView) rowView.findViewById(R.id.patientDisplayName);
            viewHolder.mGender = (TextView) rowView.findViewById(R.id.patientGender);
            viewHolder.mAge = (TextView) rowView.findViewById(R.id.patientAge);
            viewHolder.mBirthDate = (TextView) rowView.findViewById(R.id.patientBirthDate);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Patient patient = mItems.get(position);
        if (new VisitDAO().isPatientNowOnVisit(patient.getId())) {
            holder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.active_visit_dot,
                            holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label_capture_vitals));
        }
        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier());
        }
        if (null != patient.getDisplay()) {
            holder.mDisplayName.setText(patient.getDisplay());
        }
        if (null != patient.getGender()) {
            holder.mGender.setText(patient.getGender());
        }
        if (null != patient.getAge()) {
            holder.mAge.setText(patient.getAge());
        }

        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof CaptureVitalsActivity) {
                    ((CaptureVitalsActivity) mContext).startFormEntry(mItems.get(position).getUuid(), mItems.get(position).getId());
                } else {
                    throw new IllegalStateException("Current context is not an instance of CaptureVitalsActivity.class");
                }
            }

        });

        holder.mBirthDate.setText(DateUtils.convertTime(patient.getBirthDate()));
        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }
}
