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
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.retrofit.StartVisitResponseListenerCallback;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.models.retrofit.Visit;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDashboardVisitsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientVisitsPresenter {

    private PatientDashboardContract.ViewPatientVisits mPatientVisitsView;

    private List<Visit> mPatientVisits;

    public PatientDashboardVisitsPresenter(String id, PatientDashboardContract.ViewPatientVisits mPatientVisitsView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientVisitsView = mPatientVisitsView;
        this.mPatientVisitsView.setPresenter(this);
    }

    @Override
    public void start() {
        mPatientVisits = new VisitDAO().getVisitsByPatientID(mPatient.getId());
        if (mPatientVisits!=null && mPatientVisits.isEmpty()) {
            mPatientVisitsView.toggleRecyclerListVisibility(false);
        }
        else {
            mPatientVisitsView.toggleRecyclerListVisibility(true);
            mPatientVisitsView.setVisitsToDisplay(mPatientVisits);
        }
    }

    @Override
    public void showStartVisitDialog() {
        if (new VisitDAO().isPatientNowOnVisit(mPatient.getId())) {
            mPatientVisitsView.showStartVisitDialog(false);
        }
        else if (!NetworkUtils.isOnline()) {
            mPatientVisitsView.showErrorToast("Cannot start a visit manually in offline mode." +
                    "If you want to add encounters please do so in the Form Entry section, " +
                    "they will be synced with an automatic new visit.");
        }
        else {
            mPatientVisitsView.showStartVisitDialog(true);
        }
    }

    @Override
    public void syncVisits() {

        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Visit>> call = restApi.findVisitsByPatientUUID(mPatient.getUuid(), "full");
        call.enqueue(new Callback<Results<Visit>>() {
            @Override
            public void onResponse(Call<Results<Visit>> call, Response<Results<Visit>> response) {
                if (response.isSuccessful()) {
                    List<Visit> visits = response.body().getResults();
                    VisitDAO visitDAO = new VisitDAO();
                    for (Visit visit : visits) {

                        long visitId = visitDAO.getVisitsIDByUUID(visit.getUuid());

                        if (visitId > 0) {
                            visitDAO.updateVisit(visit, visitId, mPatient.getId());
                        } else {
                            visitDAO.saveVisit(visit, mPatient.getId());
                        }
                        mPatientVisitsView.setVisitsToDisplay(visits);
                    }
                    showStartVisitDialog();
                }
                else {
                    ToastUtil.error(response.message());
                }
                mPatientVisitsView.dismissStartVisitDialog();
            }

            @Override
            public void onFailure(Call<Results<Visit>> call, Throwable t) {
                ToastUtil.error(t.getMessage());
                mPatientVisitsView.dismissStartVisitDialog();
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
            public void onResponse() {}
            @Override
            public void onErrorResponse() {
                mPatientVisitsView.dismissStartVisitDialog();
            }
        });
    }
}
