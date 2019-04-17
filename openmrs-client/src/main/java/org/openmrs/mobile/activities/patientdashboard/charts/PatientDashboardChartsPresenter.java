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

package org.openmrs.mobile.activities.patientdashboard.charts;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;

import rx.android.schedulers.AndroidSchedulers;

public class PatientDashboardChartsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientChartsPresenter {

    private PatientDashboardContract.ViewPatientCharts mPatientChartsView;
    private VisitDAO visitDAO;

    public PatientDashboardChartsPresenter(String id, PatientDashboardContract.ViewPatientCharts mPatientChartsView) {
        this.mPatientChartsView = mPatientChartsView;
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.visitDAO = new VisitDAO();
        mPatientChartsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        addSubscription(visitDAO.getVisitsByPatientID(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visits -> mPatientChartsView.populateList(visits)
                ));

    }
}
