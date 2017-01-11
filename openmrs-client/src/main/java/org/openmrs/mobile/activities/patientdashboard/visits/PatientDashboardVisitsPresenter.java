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

package org.openmrs.mobile.activities.patientdashboard.visits;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.StartVisitResponseListenerCallback;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import rx.android.schedulers.AndroidSchedulers;

public class PatientDashboardVisitsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientVisitsPresenter {

    private PatientDashboardContract.ViewPatientVisits mPatientVisitsView;
    private VisitDAO visitDAO;

    public PatientDashboardVisitsPresenter(String id, PatientDashboardContract.ViewPatientVisits mPatientVisitsView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientVisitsView = mPatientVisitsView;
        this.mPatientVisitsView.setPresenter(this);
        this.visitDAO = new VisitDAO();
    }

    @Override
    public void start() {
        visitDAO.getVisitsByPatientID(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientVisits -> {
                    if (patientVisits !=null && patientVisits.isEmpty()) {
                        mPatientVisitsView.toggleRecyclerListVisibility(false);
                    }
                    else {
                        mPatientVisitsView.toggleRecyclerListVisibility(true);
                        mPatientVisitsView.setVisitsToDisplay(patientVisits);
                    }
                });
    }

    @Override
    public void showStartVisitDialog() {
        new VisitDAO().getActiveVisitByPatientId(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    if(visit != null){
                        mPatientVisitsView.showStartVisitDialog(false);
                    } else if (!NetworkUtils.isOnline()) {
                        mPatientVisitsView.showErrorToast("Cannot start a visit manually in offline mode." +
                                "If you want to add encounters please do so in the Form Entry section, " +
                                "they will be synced with an automatic new visit.");
                    }
                    else {
                        mPatientVisitsView.showStartVisitDialog(true);
                    }
                });
    }

    @Override
    public void syncVisits() {
        new VisitApi().syncVisitsData(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                VisitDAO visitDAO = new VisitDAO();
                visitDAO.getVisitsByPatientID(mPatient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(visList -> {
                            mPatientVisitsView.setVisitsToDisplay(visList);
                            showStartVisitDialog();
                        });
            }
            @Override
            public void onErrorResponse(String errorMessage) {
                ToastUtil.error(errorMessage);
            }
        });
    }

    @Override
    public void startVisit() {
        new VisitApi().startVisit(mPatient, new StartVisitResponseListenerCallback() {
            @Override
            public void onStartVisitResponse(long id) {
                mPatientVisitsView.goToVisitDashboard(id);
                mPatientVisitsView.dismissStartVisitDialog();
            }
            @Override
            public void onResponse() {
                // This method is intentionally empty
            }
            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientVisitsView.dismissStartVisitDialog();
            }
        });
    }
}
