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

import org.openmrs.mobile.R;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.FilterUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

public class SyncedPatientsPresenter implements SyncedPatientsContract.Presenter {

    // View
    @NonNull
    private final SyncedPatientsContract.View syncedPatientsView;

    // Query for data filtering
    @Nullable
    private String mQuery;

    public SyncedPatientsPresenter(@NonNull SyncedPatientsContract.View syncedPatientsView, String mQuery) {
        this.syncedPatientsView = syncedPatientsView;
        this.syncedPatientsView.setPresenter(this);
        this.mQuery = mQuery;
    }

    public SyncedPatientsPresenter(@NonNull SyncedPatientsContract.View syncedPatientsView) {
        this.syncedPatientsView = syncedPatientsView;
        this.syncedPatientsView.setPresenter(this);
    }

    /**
     * Used to display initial data on activity trigger
     */
    @Override
    public void start() {
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
        List<Patient> patientList = new PatientDAO().getAllPatients();
        final int NO_STRING_ID = R.string.last_vitals_none_label;
        boolean isFiltering = StringUtils.notNull(mQuery) && !mQuery.isEmpty();

        if (isFiltering) {
            patientList = FilterUtil.getPatientsFilteredByQuery(patientList, mQuery);
            if (patientList.isEmpty()) {
                syncedPatientsView.updateListVisibility(false, R.string.search_patient_no_result_for_query, mQuery);
            }
            else {
                syncedPatientsView.updateListVisibility(true, NO_STRING_ID, null);
            }
        }
        else {
            if (patientList.isEmpty()) {
                syncedPatientsView.updateListVisibility(false, R.string.search_patient_no_results, null);
            }
            else {
                syncedPatientsView.updateListVisibility(true, NO_STRING_ID, null);
            }
        }
        syncedPatientsView.updateAdapter(patientList);
    }

}
