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

package org.openmrs.mobile.activities.lastviewedpatients;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DownloadPatientCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LastViewedPatientRecyclerViewAdapter extends RecyclerView.Adapter<LastViewedPatientRecyclerViewAdapter.PatientViewHolder> {
    private Activity mContext;
    private List<Patient> mItems;
    private Set<Integer> selectedPatientPositions;
    private boolean isAllSelected = false;
    private boolean isLongClicked = false;
    private ActionMode actionMode;

    LastViewedPatientRecyclerViewAdapter(Activity context, List<Patient> items) {
        this.mContext = context;
        this.mItems = items;
        this.selectedPatientPositions = new HashSet<>();
    }

    @Override
    public LastViewedPatientRecyclerViewAdapter.PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_last_viewed_patients_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LastViewedPatientRecyclerViewAdapter.PatientViewHolder holder, int position) {
        final Patient patient = mItems.get(position);

        holder.setSelected(isPatientSelected(position));
        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier().getIdentifier());
        }
        if (null != patient.getPerson().getName()) {
            holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
        }
        try {
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        } catch (Exception e) {
            holder.mBirthDate.setText(" ");
        }

        if (null != holder.mAvailableOfflineCheckbox) {
            setUpCheckBoxLogic(holder, patient);
        }
    }

    private boolean isPatientSelected(int position) {
        for (Integer selectedPatientPosition : selectedPatientPositions) {
            if(selectedPatientPosition.equals(position)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewDetachedFromWindow(PatientViewHolder holder) {
        holder.clearAnimation();
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mBirthDate;
        private CheckBox mAvailableOfflineCheckbox;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.lastViewedPatientIdentifier);
            mDisplayName = (TextView) itemView.findViewById(R.id.lastViewedPatientDisplayName);
            mGender = (TextView) itemView.findViewById(R.id.lastViewedPatientGender);
            mBirthDate = (TextView) itemView.findViewById(R.id.lastViewedPatientBirthDate);
            mAvailableOfflineCheckbox = (CheckBox) itemView.findViewById(R.id.offlineCheckbox);
            mRowLayout.setOnClickListener(this);
            mRowLayout.setOnLongClickListener(this);
        }

        public void clearAnimation() {
            mRowLayout.clearAnimation();
        }

        @Override
        public void onClick(View view) {
            if(isLongClicked){
                setSelected(!mRowLayout.isSelected());
            }
        }

        private void setSelected(boolean select) {
            if(select){
                selectedPatientPositions.add(getAdapterPosition());
                this.mRowLayout.setSelected(true);
            } else {
                removeIdFromSelectedIds(getAdapterPosition());
                this.mRowLayout.setSelected(false);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.isSelected()) {
                setSelected(false);
            } else {
                if (!isLongClicked) {
                    actionMode = mContext.startActionMode(mActionModeCallback);
                }
                isLongClicked = true;
                setSelected(true);
                notifyDataSetChanged();
            }
            return true;
        }
    }

    private void removeIdFromSelectedIds(Integer position) {
        Set<Integer> newSet = new HashSet<>();
        for (Integer selectedPatientsId : selectedPatientPositions) {
            if(!selectedPatientsId.equals(position)){
                newSet.add(selectedPatientsId);
            }
        }
        selectedPatientPositions = newSet;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(mContext.getString(R.string.download_multiple));
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.download_multiple, menu);
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
                    if (isAllSelected) {
                        unselectAll();
                    } else
                        selectAll();
                    break;
                case R.id.action_download:
                    downloadSelectedPatients();
                    finish(mode);
                    break;
                case R.id.close_context_menu:
                    unselectAll();
                    finish(mode);
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
            isLongClicked = false;
        }

        public void finish(ActionMode mode){
            mode.finish();
            isLongClicked = false;
        }
    };

    public void finishActionMode(){
        if (actionMode != null) {
            actionMode.finish();
            isLongClicked = false;
        }
    }

    public void selectAll() {
        for(int i = 0; i < mItems.size(); i++){
            selectedPatientPositions.add(i);
        }
        isAllSelected = true;
        notifyDataSetChanged();
    }

    public void unselectAll() {
        selectedPatientPositions.clear();
        isAllSelected = false;
        notifyDataSetChanged();
    }


    public void downloadSelectedPatients() {
        ToastUtil.showShortToast(mContext, ToastUtil.ToastType.NOTICE, R.string.download_started);
        for (Integer selectedPatientPosition : selectedPatientPositions) {
            downloadPatient(mItems.get(selectedPatientPosition));
        }
        notifyDataSetChanged();
    }

    public void setUpCheckBoxLogic(final PatientViewHolder holder, final Patient patient) {
        if (isLongClicked) {
            holder.mAvailableOfflineCheckbox.setVisibility(View.INVISIBLE);
        } else {
            holder.mAvailableOfflineCheckbox.setChecked(false);
            holder.mAvailableOfflineCheckbox.setVisibility(View.VISIBLE);
            holder.mAvailableOfflineCheckbox.setButtonDrawable(R.drawable.ic_download);
            holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_download_label));
            holder.mAvailableOfflineCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLongClicked && ((CheckBox) v).isChecked()) {
                        downloadPatient(patient);
                        disableCheckBox(holder);
                    }
                }
            });
        }
    }

    private void downloadPatient(final Patient patient) {
        new PatientApi().downloadPatientByUuid(patient.getUuid(), new DownloadPatientCallbackListener() {
            @Override
            public void onPatientDownloaded(Patient newPatient) {
                new PatientDAO().savePatient(newPatient);
                new PatientApi().syncPatient(newPatient);
                new VisitApi().syncVisitsData(newPatient);
                new VisitApi().syncLastVitals(newPatient.getUuid());
                mItems.remove(patient);
                notifyDataSetChanged();
            }

            @Override
            public void onResponse() {
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                ToastUtil.error("Failed to fetch patient data");
            }
        });
    }

    public void disableCheckBox(PatientViewHolder holder) {
        holder.mAvailableOfflineCheckbox.setChecked(true);
        holder.mAvailableOfflineCheckbox.setClickable(false);
        holder.mAvailableOfflineCheckbox.setButtonDrawable(R.drawable.ic_offline);
    }
}
