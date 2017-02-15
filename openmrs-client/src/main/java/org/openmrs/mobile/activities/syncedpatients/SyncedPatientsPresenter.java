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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.utilities.FilterUtil;
import org.openmrs.mobile.utilities.StringUtils;

import rx.android.schedulers.AndroidSchedulers;

public class SyncedPatientsPresenter extends BasePresenter implements SyncedPatientsContract.Presenter {

    // View
    @NonNull
    private final SyncedPatientsContract.View syncedPatientsView;
    private PatientDAO patientDAO;

    // Query for data filtering
    @Nullable
    private String mQuery;

    public SyncedPatientsPresenter(@NonNull SyncedPatientsContract.View syncedPatientsView, String mQuery) {
        this.syncedPatientsView = syncedPatientsView;
        this.syncedPatientsView.setPresenter(this);
        this.mQuery = mQuery;
        this.patientDAO = new PatientDAO();
    }

    public SyncedPatientsPresenter(@NonNull SyncedPatientsContract.View syncedPatientsView) {
        this.patientDAO = new PatientDAO();
        this.syncedPatientsView = syncedPatientsView;
        this.syncedPatientsView.setPresenter(this);
    }

    public SyncedPatientsPresenter(@NonNull SyncedPatientsContract.View syncedPatientsView, PatientDAO patientDAO) {
        this.patientDAO= patientDAO;
        this.syncedPatientsView = syncedPatientsView;
        this.syncedPatientsView.setPresenter(this);
    }

    /**
     * Used to display initial data on activity trigger
     */
    @Override
    public void subscribe() {
        updateLocalPatientsList();
    }

    /**
     * Sets query used to filter (used by Activity's ActionBar)
     */
    @Override
    public void setQuery(String query) {
        mQuery = query;
    }

    /**
     * Used to update local patients list
     * It handles search events and replaces View's data to display
     */
    @Override
    public void updateLocalPatientsList() {
        addSubscription(patientDAO.getAllPatients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientList -> {
                    boolean isFiltering = StringUtils.notNull(mQuery) && !mQuery.isEmpty();

                    if (isFiltering) {
                        patientList = FilterUtil.getPatientsFilteredByQuery(patientList, mQuery);
                        if (patientList.isEmpty()) {
                            syncedPatientsView.updateListVisibility(false, mQuery);
                        } else {
                            syncedPatientsView.updateListVisibility(true);
                        }
                    } else {
                        if (patientList.isEmpty()) {
                            syncedPatientsView.updateListVisibility(false);
                        } else {
                            syncedPatientsView.updateListVisibility(true);
                        }
                    }
                    syncedPatientsView.updateAdapter(patientList);
                }));

    }

}
