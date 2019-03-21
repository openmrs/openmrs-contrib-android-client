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

package org.openmrs.mobile.activities.syncedpatients;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import rx.schedulers.Schedulers;

public class SyncedPatientsRecyclerViewAdapter extends RecyclerView.Adapter<SyncedPatientsRecyclerViewAdapter.PatientViewHolder> {
    private SyncedPatientsFragment mContext;
    private List<Patient> mItems;
    private boolean multiSelect = false;
    private ArrayList<Patient> selectedItems = new ArrayList<Patient>();

    private androidx.appcompat.view.ActionMode.Callback actionModeCallbacks = new androidx.appcompat.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext.getContext())
                    .setTitle("Delete Patients?")
                    .setMessage(String.format("Are you sure you want to delete %d patient(s)?", selectedItems.size()))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            for (Patient patientItem : selectedItems) {
                                mItems.remove(patientItem);
                                SyncedPatientsActivity activity = (SyncedPatientsActivity) mContext.getActivity();
                                if (activity != null) {
                                    activity.mPresenter.deletePatient(patientItem);
                                }
                            }
                            mode.finish();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mode.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            dialog.show();
            return true;
        }

        @Override
        public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };





    public SyncedPatientsRecyclerViewAdapter(SyncedPatientsFragment context, List<Patient> items){
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public SyncedPatientsRecyclerViewAdapter.PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_synced_patients_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SyncedPatientsRecyclerViewAdapter.PatientViewHolder holder, final int position) {
        holder.update(mItems.get(position));

        final Patient patient = mItems.get(position);

        if (null != patient.getIdentifier()) {
            String patientIdentifier = String.format(mContext.getResources().getString(R.string.patient_identifier),
                    patient.getIdentifier().getIdentifier());
            holder.mIdentifier.setText(patientIdentifier);
        }
        if (null != patient.getPerson().getName()) {
            holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
        }
        if (null != patient.getPerson().getGender()) {
            holder.mGender.setText(patient.getPerson().getGender());
        }
        try{
            holder.mBirthDate.setText(DateUtils.convertTime(DateUtils.convertTime(patient.getPerson().getBirthdate())));
        }
        catch (Exception e)
        {
            holder.mBirthDate.setText("");
        }

        /*holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!multiSelect) {
                    Intent intent = new Intent(mContext.getActivity(), PatientDashboardActivity.class);
                    intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getId());
                    mContext.startActivity(intent);
                } else{

                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;

        public PatientViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.syncedPatientIdentifier);
            mDisplayName = (TextView) itemView.findViewById(R.id.syncedPatientDisplayName);
            mGender = (TextView) itemView.findViewById(R.id.syncedPatientGender);
            mAge = (TextView) itemView.findViewById(R.id.syncedPatientAge);
            mBirthDate = (TextView) itemView.findViewById(R.id.syncedPatientBirthDate);
        }

        void selectItem(Patient item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    mRowLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    mRowLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void update(final Patient value) {
            //textView.setText(value + "");
            if (selectedItems.contains(value)) {
                mRowLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                mRowLayout.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!multiSelect) {
                        Intent intent = new Intent(mContext.getActivity(), PatientDashboardActivity.class);
                        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, value.getId());
                        mContext.startActivity(intent);
                    } else {
                        selectItem(value);
                    }

                }
            });
        }
    }
}
