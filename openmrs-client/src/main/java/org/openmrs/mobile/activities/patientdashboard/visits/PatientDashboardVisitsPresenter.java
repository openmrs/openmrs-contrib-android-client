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
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.NetworkUtils;

import rx.android.schedulers.AndroidSchedulers;

public class PatientDashboardVisitsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientVisitsPresenter {

    private PatientDashboardContract.ViewPatientVisits mPatientVisitsView;
    private VisitDAO visitDAO;
    private VisitApi visitApi;

    public PatientDashboardVisitsPresenter(String id, PatientDashboardContract.ViewPatientVisits mPatientVisitsView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientVisitsView = mPatientVisitsView;
        this.mPatientVisitsView.setPresenter(this);
        this.visitDAO = new VisitDAO();
        this.visitApi = new VisitApi();
    }

    public PatientDashboardVisitsPresenter(Patient patient,
                                           PatientDashboardContract.ViewPatientVisits mPatientVisitsView,
                                           VisitDAO visitDAO,
                                           VisitApi visitApi) {
        this.mPatient = patient;
        this.mPatientVisitsView = mPatientVisitsView;
        this.visitApi = visitApi;
        this.visitDAO = visitDAO;
        this.mPatientVisitsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        addSubscription(visitDAO.getVisitsByPatientID(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientVisits -> {
                    if (patientVisits !=null && patientVisits.isEmpty()) {
                        mPatientVisitsView.toggleRecyclerListVisibility(false);
                    }
                    else {
                        mPatientVisitsView.toggleRecyclerListVisibility(true);
                        mPatientVisitsView.setVisitsToDisplay(patientVisits);
                    }
                }));
        getVisitFromDB();
        getVisitFromServer();
    }


    public void getVisitFromDB(){
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

    public void getVisitFromServer(){
        if (NetworkUtils.isOnline()) {
            new VisitApi().syncVisitsData(mPatient, new DefaultResponseCallbackListener() {
                @Override
                public void onResponse() {
                    getVisitFromDB();
                }

                @Override
                public void onErrorResponse(String errorMessage) {
                    mPatientVisitsView.showErrorToast(errorMessage);
                }
            });
        }

    }
    @Override
    public void showStartVisitDialog() {
        addSubscription(visitDAO.getActiveVisitByPatientId(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    if(visit != null){
                        mPatientVisitsView.showStartVisitDialog(false);
                    } else if (!NetworkUtils.isOnline()) {
                        mPatientVisitsView.showErrorToast("Cannot start a visit manually in offline mode." +
                                "If you want to add encounters please do so in the Form Entry section, " +
                                "they will be synced with an automatic new visit.");
                    } else {
                        mPatientVisitsView.showStartVisitDialog(true);
                    }
                }));
    }

    @Override
    public void syncVisits() {
        mPatientVisitsView.showStartVisitProgressDialog();
        visitApi.syncVisitsData(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                addSubscription(visitDAO.getVisitsByPatientID(mPatient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(visList -> {
                            mPatientVisitsView.dismissCurrentDialog();
                            mPatientVisitsView.setVisitsToDisplay(visList);
                            showStartVisitDialog();
                        }));
            }
            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientVisitsView.dismissCurrentDialog();
                mPatientVisitsView.showErrorToast(errorMessage);
            }
        });
    }

    @Override
    public void startVisit() {
        mPatientVisitsView.showStartVisitProgressDialog();
        visitApi.startVisit(mPatient, new StartVisitResponseListenerCallback() {
            @Override
            public void onStartVisitResponse(long id) {
                mPatientVisitsView.goToVisitDashboard(id);
                mPatientVisitsView.dismissCurrentDialog();
            }
            @Override
            public void onResponse() {
                // This method is intentionally empty
            }
            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientVisitsView.showErrorToast(errorMessage);
                mPatientVisitsView.dismissCurrentDialog();
            }
        });
    }
}
