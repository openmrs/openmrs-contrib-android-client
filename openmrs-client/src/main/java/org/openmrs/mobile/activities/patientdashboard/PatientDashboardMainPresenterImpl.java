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

package org.openmrs.mobile.activities.patientdashboard;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;

import rx.schedulers.Schedulers;

public abstract class PatientDashboardMainPresenterImpl extends BasePresenter implements PatientDashboardContract.PatientDashboardMainPresenter {

    protected Patient mPatient;

    @Override
    public void deletePatient() {
        new PatientDAO().deletePatient(mPatient.getId());
        addSubscription(new VisitDAO().deleteVisitsByPatientId(mPatient.getId())
                .observeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public long getPatientId() {
        return mPatient.getId();
    }
}
