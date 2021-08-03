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
package org.openmrs.mobile.activities.formentrypatientlist

import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.StringUtils
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers

class FormEntryPatientListPresenter(private var mFormEntryPatientListView: FormEntryPatientListContract.View,
                                    private var patientDAO: PatientDAO) : BasePresenter(), FormEntryPatientListContract.Presenter {

    constructor(view: FormEntryPatientListContract.View) : this(view, PatientDAO())
    private var mQuery: String? = null

    init {
        mFormEntryPatientListView.setPresenter(this)
    }

    override fun subscribe() {
        updatePatientsList()
    }

    /**
     * Sets query used to filter (used by Activity's ActionBar)
     */
    override fun setQuery(query: String?) {
        mQuery = query
    }

    /**
     * Used to update local patients list
     * It handles search events and replaces View's data to display
     */
    override fun updatePatientsList() {
        addSubscription(patientDAO.allPatients
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { patientList: List<Patient?> ->
                    val noStringId = R.string.last_vitals_none_label
                    val isFiltering = StringUtils.notNull(mQuery)

                    if (isFiltering) {
                        val patientListMutable: List<Patient?> = FilterUtil.getPatientsFilteredByQuery(patientList, mQuery)
                        if (patientListMutable.isEmpty()) {
                            mFormEntryPatientListView.updateListVisibility(false, R.string.search_patient_no_result_for_query, mQuery)
                        } else {
                            mFormEntryPatientListView.updateListVisibility(true, noStringId, null)
                        }
                    } else {
                        if (patientList.isEmpty()) {
                            mFormEntryPatientListView.updateListVisibility(false, R.string.search_patient_no_results, null)
                        } else {
                            mFormEntryPatientListView.updateListVisibility(true, noStringId, null)
                        }
                    }
                    mFormEntryPatientListView.updateAdapter(patientList)
                })
    }
}