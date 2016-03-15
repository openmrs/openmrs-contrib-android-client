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
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.fragments.FindPatientInDatabaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientArrayAdapter extends ArrayAdapter<Patient> {
    private Activity mContext;
    private List<Patient> mItems;
    private int mResourceID;

    private boolean isAllSelected = false;
    private ActionMode mActionMode;
    private boolean isLongClicked = false;
    private PatientDataArrayList patientDataArrayList = new PatientDataArrayList();


    private final OpenMRS mOpenMRS = OpenMRS.getInstance();
    private final OpenMRSLogger mOpenMRSLogger = mOpenMRS.getOpenMRSLogger();

    private class PatientData {
        private int position;
        private Patient patient;
        private ViewHolder holder;
        private boolean isChecked = false;

        public PatientData(ViewHolder holder, Patient patient, int position) {
            this.holder = holder;
            this.patient = patient;
            this.position = position;
        }

        public void setChecked(boolean value) {
            isChecked = value;
            holder.mRowLayout.setSelected(value);
            if (isChecked)
                holder.mRowLayout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.light_teal));
            else
                holder.mRowLayout.setBackgroundColor(Color.WHITE);
            mOpenMRSLogger.i("isSelected: " + isChecked +".ID: " + patient.getId());
        }

        public int getPosition() {
            return position;
        }
    }

    private class PatientDataArrayList extends ArrayList<PatientData> {
        public void check(int position) {
            for (int index = 0; index < size(); index++) {
                PatientData patientData = get(index);
                if (patientData.getPosition() == position) {
                    patientData.setChecked(true);
                    break;
                }
            }
        }

        public void uncheck(int position) {
            for (int index = 0; index < size(); index++) {
                PatientData patientData = get(index);
                if (patientData.getPosition() == position) {
                    patientData.setChecked(false);
                    break;
                }
            }
        }
    }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Patient patient = mItems.get(position);

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

            patientDataArrayList.add(new PatientData(viewHolder, patient, position));
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

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
        if(null!=patient.getBirthDate()) {
            holder.mBirthDate.setText(DateUtils.convertTime(patient.getBirthDate()));
        }
        if (null != holder.mAvailableOfflineCheckbox) {
            setUpCheckBoxLogic(holder, patient);
        }
        holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new PatientDAO().isUserAlreadySaved(patient.getUuid())) {
                    Intent intent = new Intent(mContext, PatientDashboardActivity.class);
                    intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getUuid());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.mRowLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onLongClick(View v) {
                if (v.isSelected()) {
                    mOpenMRSLogger.i("ID: " + position + " Unselected");
                    patientDataArrayList.uncheck(position);
                } else {
                    if (!isLongClicked)
                        mActionMode = mContext.startActionMode(mActionModeCallback);
                    isLongClicked = true;
                    mOpenMRSLogger.i("ID: " + position + " Selected");
                    patientDataArrayList.check(position);
                }
                return true;
            }
        });


        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Download Multiple");
            MenuInflater inflator = mode.getMenuInflater();
            inflator.inflate(R.menu.download_multiple, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    if (isAllSelected)
                        unselectAll();
                    else
                        selectAll();
                    isAllSelected = !isAllSelected;
                    break;
                case R.id.action_download:
                    downloadPatientData();
                    break;
                case R.id.close_context_menu:
                    unselectAll();
                    mode.finish();
                    break;
                default:
                    unselectAll();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            unselectAll();
            mActionMode = null;
        }
    };

    public void selectAll() {
        for (PatientData patientData : patientDataArrayList) {
            patientData.setChecked(true);
        }
    }

    public void unselectAll() {
        for (PatientData patientData : patientDataArrayList) {
            patientData.setChecked(false);
        }
    }


    public void downloadPatientData() {
        for (PatientData patientData : patientDataArrayList) {
            long patientId = new PatientDAO().savePatient(patientData.patient);
            new VisitsManager().findVisitsByPatientUUID(
                    VisitsHelper.createVisitsByPatientUUIDListener(patientData.patient.getUuid(),
                            patientId, (ACBaseActivity) mContext));
            ToastUtil.showShortToast(mContext, ToastUtil.ToastType.NOTICE, R.string.download_started);
        }

    }

    public void setUpCheckBoxLogic(final ViewHolder holder, final Patient patient){
        if (new PatientDAO().userDoesNotExist(patient.getUuid())) {
            holder.mAvailableOfflineCheckbox.setChecked(false);
            holder.mAvailableOfflineCheckbox.setVisibility(View.VISIBLE);
            holder.mAvailableOfflineCheckbox.setButtonDrawable(R.drawable.ic_download);
            holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_download_label));
            holder.mAvailableOfflineCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        long patientId = new PatientDAO().savePatient(patient);
                        ToastUtil.showShortToast(mContext,
                                ToastUtil.ToastType.NOTICE,
                                R.string.download_started);
                        new VisitsManager().findVisitsByPatientUUID(
                                VisitsHelper.createVisitsByPatientUUIDListener(patient.getUuid(), patientId, (ACBaseActivity) mContext));
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
        holder.mAvailableOfflineCheckbox.setButtonDrawable(R.drawable.ic_offline);
    }
}
