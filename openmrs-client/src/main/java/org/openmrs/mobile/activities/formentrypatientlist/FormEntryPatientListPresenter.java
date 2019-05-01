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

package org.openmrs.mobile.activities.formentrypatientlist;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.utilities.FilterUtil;
import org.openmrs.mobile.utilities.StringUtils;

import rx.android.schedulers.AndroidSchedulers;

public class FormEntryPatientListPresenter extends BasePresenter implements FormEntryPatientListContract.Presenter {

    private final FormEntryPatientListContract.View mFormEntryPatientListView;
    private PatientDAO patientDAO;
    private String mQuery;

    public FormEntryPatientListPresenter(FormEntryPatientListContract.View view) {
        this.mFormEntryPatientListView = view;
        this.mFormEntryPatientListView.setPresenter(this);
        this.patientDAO = new PatientDAO();
    }

    public FormEntryPatientListPresenter(FormEntryPatientListContract.View view, PatientDAO patientDAO) {
        this.mFormEntryPatientListView = view;
        this.mFormEntryPatientListView.setPresenter(this);
        this.patientDAO = patientDAO;
    }

    @Override
    public void subscribe() {
        updatePatientsList();
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
    public void updatePatientsList() {
        addSubscription(patientDAO.getAllPatients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientList -> {
                    final int NO_STRING_ID = R.string.last_vitals_none_label;
                    boolean isFiltering = StringUtils.notNull(mQuery);

                    if (isFiltering) {
                        patientList = FilterUtil.getPatientsFilteredByQuery(patientList, mQuery);
                        if (patientList.isEmpty()) {
                            mFormEntryPatientListView.updateListVisibility(false, R.string.search_patient_no_result_for_query, mQuery);
                        } else {
                            mFormEntryPatientListView.updateListVisibility(true, NO_STRING_ID, null);
                        }
                    } else {
                        if (patientList.isEmpty()) {
                            mFormEntryPatientListView.updateListVisibility(false, R.string.search_patient_no_results, null);
                        } else {
                            mFormEntryPatientListView.updateListVisibility(true, NO_STRING_ID, null);
                        }
                    }
                    mFormEntryPatientListView.updateAdapter(patientList);
                }));
    }
}
