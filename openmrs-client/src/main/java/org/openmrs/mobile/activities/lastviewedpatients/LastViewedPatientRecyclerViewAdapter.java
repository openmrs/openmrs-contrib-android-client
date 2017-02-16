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

import android.app.Activity;
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
import android.widget.ProgressBar;
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

import rx.android.schedulers.AndroidSchedulers;

class LastViewedPatientRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Activity mContext;
    private List<Patient> patients;
    private Set<Integer> selectedPatientPositions;
    private boolean isAllSelected = false;
    private boolean isLongClicked = false;
    private boolean enableDownload = true;
    private ActionMode actionMode;
    private LastViewedPatientsContract.View view;

    LastViewedPatientRecyclerViewAdapter(Activity context, List<Patient> items, LastViewedPatientsContract.View view) {
        this.mContext = context;
        this.patients = items;
        this.selectedPatientPositions = new HashSet<>();
        this.view = view;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public Set<Integer> getSelectedPatientPositions() {
        return selectedPatientPositions;
    }

    public void setSelectedPatientPositions(Set<Integer> selectedPatientPositions) {
        this.selectedPatientPositions = selectedPatientPositions;
    }

    public void addPatients(List<Patient> patients){
        this.patients.addAll(patients);
        notifyDataSetChanged();
    }

    public void deleteLastItem(){
        patients.remove(getItemCount()-1);
        notifyItemRemoved(getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if( viewType == VIEW_TYPE_ITEM ) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_last_viewed_patients_row, parent, false);
            FontsUtil.setFont((ViewGroup) itemView);
            return new PatientViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            FontsUtil.setFont((ViewGroup) itemView);
            return new ProgressBarViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return patients.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PatientViewHolder) {
            final Patient patient = patients.get(position);

            ((PatientViewHolder) holder).setSelected(isPatientSelected(position));
            if (null != patient.getIdentifier()) {
                String patientIdentifier = String.format(mContext.getResources().getString(R.string.patient_identifier),
                        patient.getIdentifier().getIdentifier());
                ((PatientViewHolder) holder).mIdentifier.setText(patientIdentifier);
            }
            if (null != patient.getPerson().getName()) {
                ((PatientViewHolder) holder).mDisplayName.setText(patient.getPerson().getName().getNameString());
            }
            if (null != patient.getPerson().getGender()) {
                ((PatientViewHolder) holder).mGender.setText(patient.getPerson().getGender());
            }
            try {
                ((PatientViewHolder) holder).mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
            } catch (Exception e) {
                ((PatientViewHolder) holder).mBirthDate.setText(" ");
            }

            if (null !=  ((PatientViewHolder) holder).mAvailableOfflineCheckbox) {
                setUpCheckBoxLogic( ((PatientViewHolder) holder), patient);
            }
        } else {
            ((ProgressBarViewHolder) holder).progressBar.setIndeterminate(true);
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
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof PatientViewHolder) {
            ((PatientViewHolder) holder).clearAnimation();
        }
    }


    @Override
    public int getItemCount() {
        return patients == null ? 0 : patients.size();
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
                if (!enableDownload)
                    toggleDownloadButton();
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
                    startActionMode();
                }
                isLongClicked = true;
                setSelected(true);
                notifyDataSetChanged();
            }
            return true;
        }
    }

    class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public ProgressBarViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.recycleviewProgressbar);
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
        if (selectedPatientPositions.size() == 0 && isLongClicked && enableDownload)
                toggleDownloadButton();
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

    public void startActionMode() {
        actionMode = mContext.startActionMode(mActionModeCallback);
        isLongClicked = true;
    }

    public void finishActionMode(){
        if (actionMode != null) {
            actionMode.finish();
            isLongClicked = false;
        }
    }

    public void selectAll() {
        for(int i = 0; i < patients.size(); i++){
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
            downloadPatient(patients.get(selectedPatientPosition), false);
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
                        downloadPatient(patient, true);
                        disableCheckBox(holder);
                    }
                }
            });
        }
    }

    private void downloadPatient(final Patient patient, final Boolean showSnackBar) {
        new PatientApi().downloadPatientByUuid(patient.getUuid(), new DownloadPatientCallbackListener() {
            @Override
            public void onPatientDownloaded(Patient newPatient) {
                new PatientDAO().savePatient(newPatient)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> {
                            new VisitApi().syncVisitsData(newPatient);
                            new VisitApi().syncLastVitals(newPatient.getUuid());
                            patients.remove(patient);
                            notifyDataSetChanged();
                            if (showSnackBar) {
                                view.showOpenPatientSnackbar(newPatient.getId());
                            }
                        });
            }

            @Override
            public void onPatientPhotoDownloaded(Patient patient) {
                new PatientDAO().updatePatient(patient.getId(), patient);
            }

            @Override
            public void onResponse() {
                // This method is intentionally empty
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

    private void toggleDownloadButton() {
        if (actionMode != null) {
            MenuItem item = actionMode.getMenu().findItem(R.id.action_download);
            if (item.isEnabled()) {
                enableDownload = false;
                item.setEnabled(false);
                item.getIcon().setAlpha(128);
            } else {
                enableDownload = true;
                item.setEnabled(true);
                item.getIcon().setAlpha(255);
            }
        }
    }
}
