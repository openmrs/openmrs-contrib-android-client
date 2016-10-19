/**
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

import com.google.gson.JsonObject;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitDashboardPresenter implements VisitDashboardContract.Presenter {

    public long visitStopDate;
    public String mPatientName;

    private List<Encounter> mVisitEncounters;
    private Visit mVisit;
    private Patient mPatient;

    private VisitDashboardContract.View mVisitDashboardView;

    public VisitDashboardPresenter(VisitDashboardContract.View mVisitDashboardView, Long id) {
        this.mVisitDashboardView = mVisitDashboardView;
        mVisitDashboardView.setPresenter(this);
        mVisit = new VisitDAO().getVisitsByID(id);
        mPatient = new PatientDAO().findPatientByID(String.valueOf(mVisit.getPatientID()));
        mPatientName = mPatient.getPerson().getName().getNameString();
        mVisitEncounters = mVisit.getEncounters();
        visitStopDate = mVisit.getStopDate();
    }



    public void endVisitByUUID(final Visit visitToFinish) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        JsonObject visit = new JsonObject();
        visit.addProperty("stopDatetime", DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
        Call<JsonObject> call = restApi.endVisitByUUID(visitToFinish.getUuid(), visit);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    VisitDAO visitDAO = new VisitDAO();
                    Visit visit = visitDAO.getVisitsByID(visitToFinish.getId());
                    visit.setStopDate(DateUtils.convertTime(response.body().get(VisitsManager.STOP_DATE_TIME).getAsString()));
                    visitDAO.updateVisit(visit, visit.getId(), visit.getPatientID());
                    moveToPatientDashboard();
                }
                else {
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
