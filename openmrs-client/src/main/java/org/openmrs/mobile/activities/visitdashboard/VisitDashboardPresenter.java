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

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class VisitDashboardPresenter extends BasePresenter implements VisitDashboardContract.Presenter {

    private RestApi restApi;
    private VisitDAO visitDAO;
    private Long visitId;

    private VisitDashboardContract.View mVisitDashboardView;

    public VisitDashboardPresenter(VisitDashboardContract.View mVisitDashboardView, Long id) {
        this.mVisitDashboardView = mVisitDashboardView;
        this.visitDAO = new VisitDAO();
        this.visitId = id;
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        mVisitDashboardView.setPresenter(this);
    }

    public VisitDashboardPresenter(RestApi restApi, VisitDAO visitDAO, Long visitId, VisitDashboardContract.View mVisitDashboardView) {
        this.mVisitDashboardView = mVisitDashboardView;
        this.visitDAO = visitDAO;
        this.visitId = visitId;
        this.restApi = restApi;
        mVisitDashboardView.setPresenter(this);
    }

    public void endVisitByUUID(final Visit visit) {
        visit.setStopDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));

        Visit test = new Visit();
        test.setStopDatetime(visit.getStopDatetime());

        Call<Visit> call = restApi.endVisitByUUID(visit.getUuid(), test );

        call.enqueue(new Callback<Visit>() {
            @Override
            public void onResponse(Call<Visit> call, Response<Visit> response) {
                if (response.isSuccessful()) {
                    addSubscription(visitDAO.getVisitByID(visit.getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(vis -> {
                                vis.setStopDatetime(response.body().getStopDatetime());
                                visitDAO.saveOrUpdate(vis, vis.getPatient().getId())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(id -> moveToPatientDashboard());
                            }));
                }
                else {
                    mVisitDashboardView.showErrorToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<Visit> call, Throwable t) {
                mVisitDashboardView.showErrorToast(t.getMessage());
            }
        });
    }

    public void endVisit() {
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::endVisitByUUID));
    }

    @Override
    public void subscribe() {
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    List<Encounter> encounters = visit.getEncounters();
                    if (!encounters.isEmpty()) {
                        mVisitDashboardView.setEmptyListVisibility(false);
                    }
                    mVisitDashboardView.updateList(encounters);
                }));

    }

    @Override
    public void fillForm() {
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    Patient patient = visit.getPatient();
                    if(patient.getUuid() != null) {
                        mVisitDashboardView.startCaptureVitals(patient.getId());
                    } else {
                        mVisitDashboardView.showErrorToast(R.string.patient_not_yet_registered);
                    }
                }));

    }

    @Override
    public void updatePatientName() {
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> mVisitDashboardView.setActionBarTitle(visit.getPatient().getPerson().getName().getNameString())));
    }

    @Override
    public void checkIfVisitActive() {
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(visit -> StringUtils.isBlank(visit.getStopDatetime()))
                .subscribe(visit -> mVisitDashboardView.setActiveVisitMenu()));
    }

    public void moveToPatientDashboard() {
        mVisitDashboardView.moveToPatientDashboard();
    }
}
