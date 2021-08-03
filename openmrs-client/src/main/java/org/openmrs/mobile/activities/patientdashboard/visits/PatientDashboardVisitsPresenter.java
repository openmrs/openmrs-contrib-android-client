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

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.dao.VisitDAO;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import com.openmrs.android_sdk.library.api.repository.VisitRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.StartVisitResponseCallback;

import rx.android.schedulers.AndroidSchedulers;

public class PatientDashboardVisitsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientVisitsPresenter {
    private PatientDashboardContract.ViewPatientVisits mPatientVisitsView;
    private VisitDAO visitDAO;
    private VisitRepository visitRepository;

    public PatientDashboardVisitsPresenter(String id, PatientDashboardContract.ViewPatientVisits mPatientVisitsView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientVisitsView = mPatientVisitsView;
        this.mPatientVisitsView.setPresenter(this);
        this.visitDAO = new VisitDAO();
        this.visitRepository = new VisitRepository();
    }

    public PatientDashboardVisitsPresenter(Patient patient,
                                           PatientDashboardContract.ViewPatientVisits mPatientVisitsView,
                                           VisitDAO visitDAO,
                                           VisitRepository visitRepository) {
        this.mPatient = patient;
        this.mPatientVisitsView = mPatientVisitsView;
        this.visitRepository = visitRepository;
        this.visitDAO = visitDAO;
        this.mPatientVisitsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mPatientVisitsView.setPatient(mPatient);
        addSubscription(visitDAO.getVisitsByPatientID(mPatient.getId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(patientVisits -> {
                if (patientVisits != null && patientVisits.isEmpty()) {
                    mPatientVisitsView.toggleRecyclerListVisibility(false);
                } else {
                    mPatientVisitsView.toggleRecyclerListVisibility(true);
                    mPatientVisitsView.setVisitsToDisplay(patientVisits);
                }
            }));
        getVisitFromDB();
        getVisitFromServer();
    }

    public void getVisitFromDB() {
        visitDAO.getVisitsByPatientID(mPatient.getId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(patientVisits -> {
                if (patientVisits != null && patientVisits.isEmpty()) {
                    mPatientVisitsView.toggleRecyclerListVisibility(false);
                } else {
                    mPatientVisitsView.toggleRecyclerListVisibility(true);
                    mPatientVisitsView.setVisitsToDisplay(patientVisits);
                }
            });
    }

    public void getVisitFromServer() {
        if (NetworkUtils.isOnline()) {
            new VisitRepository().syncVisitsData(mPatient, new DefaultResponseCallback() {
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
                if (visit != null) {
                    mPatientVisitsView.showStartVisitDialog(false);
                } else if (!NetworkUtils.isOnline()) {
                    mPatientVisitsView.showErrorToast(R.string.offline_mode_not_supported);
                } else {
                    mPatientVisitsView.showStartVisitDialog(true);
                }
            }));
    }

    @Override
    public void syncVisits() {
        mPatientVisitsView.showStartVisitProgressDialog();
        visitRepository.syncVisitsData(mPatient, new DefaultResponseCallback() {
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
        visitRepository.startVisit(mPatient, new StartVisitResponseCallback() {
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
