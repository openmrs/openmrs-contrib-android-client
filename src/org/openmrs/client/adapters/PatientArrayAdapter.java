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
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.activities.fragments.FindPatientInDatabaseFragment;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.VisitsManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.FontsUtil;
import org.openmrs.client.utilities.ToastUtil;

import java.util.List;

public class PatientArrayAdapter extends ArrayAdapter<Patient> {
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
        private CheckBox mAvailableOfflineCheckbox;
    }

    public PatientArrayAdapter(Activity context, int resourceID, List<Patient> items) {
        super(context, resourceID, items);
        this.mContext = context;
        this.mItems = items;
        this.mResourceID = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(mResourceID, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mRowLayout = (LinearLayout) rowView;
            viewHolder.mIdentifier = (TextView) rowView.findViewById(R.id.patientIdentifier);
            viewHolder.mDisplayName = (TextView) rowView.findViewById(R.id.patientDisplayName);
            viewHolder.mGender = (TextView) rowView.findViewById(R.id.patientGender);
            viewHolder.mAge = (TextView) rowView.findViewById(R.id.patientAge);
            viewHolder.mBirthDate = (TextView) rowView.findViewById(R.id.patientBirthDate);
            viewHolder.mAvailableOfflineCheckbox = (CheckBox) rowView.findViewById(R.id.offlineCheckbox);
            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Patient patient = mItems.get(position);
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
        holder.mBirthDate.setText(DateUtils.convertTime(patient.getBirthDate()));
        if (null != holder.mAvailableOfflineCheckbox) {
            setUpCheckBoxLogic(holder, patient);
        }
        if (new PatientDAO().isUserAlreadySaved(patient.getUuid())) {
            holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PatientDashboardActivity.class);
                    intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getUuid());
                    mContext.startActivity(intent);
                }
            });
        }
        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }

    public void setUpCheckBoxLogic(final ViewHolder holder, final Patient patient) {
        if (new PatientDAO().userDoesNotExist(patient.getUuid())) {
            holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_download_label));
            holder.mAvailableOfflineCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        long patientId = new PatientDAO().savePatient(patient);
                        new VisitsManager(mContext).findActiveVisitsForPatientByUUID(patient.getUuid(), patientId);
                        ToastUtil.showShortToast(mContext,
                                            ToastUtil.ToastType.SUCCESS,
                                            R.string.find_patients_row_toast_patient_saved);
                        disableCheckBox(holder);

                        if (mContext instanceof FindPatientsActivity) {
                            FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
                            FindPatientInDatabaseFragment fragment = (FindPatientInDatabaseFragment) fm
                                    .getFragments().get(FindPatientsActivity.TabHost.DOWNLOADED_TAB_POS);

                            fragment.updatePatientsInDatabaseList();
                        }
                    }
                }
            });
        } else {
            disableCheckBox(holder);
        }
    }

    public void disableCheckBox(ViewHolder holder) {
        holder.mAvailableOfflineCheckbox.setChecked(true);
        holder.mAvailableOfflineCheckbox.setClickable(false);
        holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_available_offline_label));
        holder.mAvailableOfflineCheckbox.setVisibility(View.GONE);
    }
}
