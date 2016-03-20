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
import android.support.v4.app.Fragment;
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
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
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
    private int fragmentID;

    private boolean isAllDownloadableSelected = false;
    private ActionMode actionMode;
    private boolean isLongClicked = false;
    private int howManyDownloadables = 0;
    private int howManySelected = 0;
    private PatientDataArrayList patientDataArrayList = new PatientDataArrayList();

    private final OpenMRS openMRSInstance = OpenMRS.getInstance();
    private final OpenMRSLogger openMRSLogger = openMRSInstance.getOpenMRSLogger();

    public PatientArrayAdapter(Activity context, int resourceID, List<Patient> items, int fragmentID) {
        super(context, resourceID, items);
        this.mContext = context;
        this.mItems = items;
        this.mResourceID = resourceID;
        this.fragmentID = fragmentID;
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

    private class PatientData {
        // class to store patient data
        private int position;
        private Patient patient;
        private ViewHolder holder;
        private boolean isSelected = false;

        public PatientData(ViewHolder holder, Patient patient, int position) {
            this.holder = holder;
            this.patient = patient;
            this.position = position;
        }

        public void setSelected(boolean value) {
            if (value && !isSelected) {
                // selected and previously was not selected, otherwise no change needed
                    howManySelected++;
                    holder.mRowLayout.setSelected(true);
                    holder.mRowLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_teal));
            }
            else if (!value && isSelected){
                // unselected and previously was selected, otherwise no change needed
                    howManySelected--;
                    holder.mRowLayout.setSelected(false);
                    holder.mRowLayout.setBackgroundResource(R.drawable.card);
            }
            isSelected = value;
        }

        public int getPosition() {
            return position;
        }

        public boolean isDownloadable() {
            return new PatientDAO().userDoesNotExist(this.patient.getUuid());
        }

        public void downloaded() {
            howManyDownloadables--;
            holder.mRowLayout.setSelected(false);
            holder.mRowLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private class PatientDataArrayList extends ArrayList<PatientData> {
        public PatientData getPatientDataByPosition(int position) {
            for (int index = 0; index < size(); index++) {
                PatientData patientData = get(index);
                if (patientData.getPosition() == position) {
                    return patientData;
                }
            }
            return null;
        }
    }

    private void updateIsAllDownloadableSelected() {
        if (howManyDownloadables == howManySelected && howManyDownloadables != 0) {
            isAllDownloadableSelected = true;
        } else {
            isAllDownloadableSelected = false;
        }
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

            if (!new PatientDAO().isUserAlreadySaved(patient.getUuid())) {
                howManyDownloadables++;
            }

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
            @Override
            public boolean onLongClick(View v) {
                if (fragmentID == FindPatientInDatabaseFragment.FIND_PATIENT_IN_DB_FM_ID) {
                    // this listener shouldn't work in FindPatientInDatabase Fragment
                    return true;
                }
                PatientData patientData = patientDataArrayList.getPatientDataByPosition(position);
                if (patientData.isDownloadable()) {
                    if (v.isSelected()) {
                        // unselected
                        patientData.setSelected(false);
                    } else {
                        // selected
                        if (!isLongClicked) {
                            actionMode = mContext.startActionMode(mActionModeCallback);
                        }
                        isLongClicked = true;
                        patientData.setSelected(true);
                    }
                    updateIsAllDownloadableSelected();
                } else {
                    ToastUtil.showShortToast(mContext, ToastUtil.ToastType.NOTICE,
                            R.string.patient_alreadly_available);
                }
                return true;
            }
        });

        FontsUtil.setFont((ViewGroup) rowView);
        return rowView;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(mContext.getString(R.string.download_multiple));
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
                    if (isAllDownloadableSelected) {
                        unselectAll();
                    } else
                        selectAll();
                    updateIsAllDownloadableSelected();
                    break;
                case R.id.action_download:
                    downloadSelectedPatients();
                    mode.finish();
                    break;
                case R.id.close_context_menu:
                    isLongClicked = false;
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
            actionMode = null;
        }
    };

    public void selectAll() {
        for (PatientData patientData : patientDataArrayList) {
            if (patientData.isDownloadable()) {
                patientData.setSelected(true);
            }
        }
    }

    public void unselectAll() {
        for (PatientData patientData : patientDataArrayList) {
            if (patientData.isDownloadable()) {
                patientData.setSelected(false);
            }
        }
    }


    public void downloadSelectedPatients() {
        for (PatientData patientData : patientDataArrayList) {
            if (patientData.isDownloadable() && patientData.isSelected) {
                downloadPatient(patientData.patient);
                disableCheckBox(patientData.holder);
                patientData.downloaded();
            }
        }
        updatePatientsInDatabase();
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
                        downloadPatient(patient);
                        updatePatientsInDatabase();
                        disableCheckBox(holder);
                    }
                }
            });
        } else {
            disableCheckBox(holder);
        }
    }

    private void downloadPatient(Patient patient) {
        long patientId = new PatientDAO().savePatient(patient);
        ToastUtil.showShortToast(mContext, ToastUtil.ToastType.NOTICE, R.string.download_started);
        new VisitsManager().findVisitsByPatientUUID(
                VisitsHelper.createVisitsByPatientUUIDListener(patient.getUuid(), patientId, (ACBaseActivity) mContext));
    }

    private void updatePatientsInDatabase() {
        if (mContext instanceof FindPatientsActivity) {
            FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
            FindPatientInDatabaseFragment fragment = (FindPatientInDatabaseFragment) fm
                    .getFragments().get(FindPatientsActivity.TabHost.DOWNLOADED_TAB_POS);

            fragment.updatePatientsInDatabaseList();
        }
    }

    public void disableCheckBox(ViewHolder holder) {
        holder.mAvailableOfflineCheckbox.setChecked(true);
        holder.mAvailableOfflineCheckbox.setClickable(false);
        holder.mAvailableOfflineCheckbox.setButtonDrawable(R.drawable.ic_offline);
    }
}
