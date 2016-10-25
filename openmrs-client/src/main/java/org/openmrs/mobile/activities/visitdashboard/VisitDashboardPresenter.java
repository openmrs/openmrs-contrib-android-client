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

package org.openmrs.mobile.activities.visitdashboard;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Visit;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitDashboardPresenter implements VisitDashboardContract.Presenter {

    public String visitStopDate;
    public String mPatientName;

    private List<Encounter> mVisitEncounters;
    private Visit mVisit;
    private Patient mPatient;

    private VisitDashboardContract.View mVisitDashboardView;

    public VisitDashboardPresenter(VisitDashboardContract.View mVisitDashboardView, Long id) {
        this.mVisitDashboardView = mVisitDashboardView;
        mVisitDashboardView.setPresenter(this);
        mVisit = new VisitDAO().getVisitsByID(id);
        mPatient = new PatientDAO().findPatientByID(String.valueOf(mVisit.getPatient().getId()));
        mPatientName = mPatient.getPerson().getName().getNameString();
        mVisitEncounters = mVisit.getEncounters();
        visitStopDate = mVisit.getStopDatetime();
    }

    public void endVisitByUUID(final Visit visit) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        visit.setStopDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));

        Visit test = new Visit();
        test.setStopDatetime(visit.getStopDatetime());

        Call<Visit> call = restApi.endVisitByUUID(visit.getUuid(), test );

        call.enqueue(new Callback<Visit>() {
            @Override
            public void onResponse(Call<Visit> call, Response<Visit> response) {

                if (response.isSuccessful()) {
                    VisitDAO visitDAO = new VisitDAO();
                    Visit newVisit = visitDAO.getVisitsByID(visit.getId());
                    newVisit.setStopDatetime(response.body().getStopDatetime());
                    visitDAO.updateVisit(newVisit, newVisit.getId(), newVisit.getPatient().getId());
                    moveToPatientDashboard();
                }
                else {
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Visit> call, Throwable t) {
                ToastUtil.error(t.getMessage());
            }
        });
    }

    public void endVisit() {
        endVisitByUUID(mVisit);
    }

    private void startCaptureVitals() {
        mVisitDashboardView.startCaptureVitals(mPatient.getId());
    }

    @Override
    public void start() {
        mVisitEncounters = new VisitDAO().getVisitsByID(mVisit.getId()).getEncounters();
        if (!mVisitEncounters.isEmpty()) {
            mVisitDashboardView.setEmptyListVisibility(false);
        }
        mVisitDashboardView.updateList(mVisitEncounters);
    }

    @Override
    public void fillForm() {
        if(mPatient.getUuid()!=null)
        {
            startCaptureVitals();
        }
        else
            ToastUtil.error("Patient not yet registered, cannot create encounter.");
    }

    public void moveToPatientDashboard() {
        mVisitDashboardView.moveToPatientDashboard();
    }
}
