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
package org.openmrs.mobile.activities.activevisits

import com.openmrs.android_sdk.library.models.Visit
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import com.openmrs.android_sdk.library.dao.VisitDAO
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers

class ActiveVisitPresenter(private var mActiveVisitsView: ActiveVisitsContract.View, private var visitDAO: VisitDAO) : BasePresenter(), ActiveVisitsContract.Presenter {

    constructor(mActiveVisitsView: ActiveVisitsContract.View) : this(mActiveVisitsView, VisitDAO())

    init {
        this.mActiveVisitsView.setPresenter(this)
    }

    override fun subscribe() {
        updateVisitsInDatabaseList()
    }

    override fun updateVisitsInDatabaseList() {
        mActiveVisitsView.setEmptyListText(R.string.search_visits_no_results)
        addSubscription(visitDAO.activeVisits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit?>? -> mActiveVisitsView.updateListVisibility(visits) },
                        { mActiveVisitsView.setEmptyListText(R.string.search_visits_no_results) }
                ))
    }

    override fun updateVisitsInDatabaseList(query: String?) {
        mActiveVisitsView.setEmptyListText(R.string.search_patient_no_result_for_query, query)
        addSubscription(visitDAO.activeVisits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit?>? ->
                            var visitList: List<Visit?>?
                            visitList = FilterUtil.getPatientsWithActiveVisitsFilteredByQuery(visits, query)
                            mActiveVisitsView.updateListVisibility(visitList)
                        },
                        {
                            mActiveVisitsView.setEmptyListText(R.string.search_patient_no_result_for_query, query)
                        }
                ))
    }
}