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

package org.openmrs.mobile.activities.patientdashboard.details;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.DownloadPatientCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.NetworkUtils;

public class PatientDashboardDetailsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDetailsPresenter {

    private PatientDashboardContract.ViewPatientDetails mPatientDetailsView;
    private VisitApi visitApi;
    private PatientApi patientApi;
    private PatientDAO patientDAO;

    public PatientDashboardDetailsPresenter(String id,
                                            PatientDashboardContract.ViewPatientDetails mPatientDetailsView) {
        this.mPatientDetailsView = mPatientDetailsView;
        this.visitApi = new VisitApi();
        this.patientApi = new PatientApi();
        this.patientDAO = new PatientDAO();
        this.mPatient = patientDAO.findPatientByID(id);
        this.mPatientDetailsView.setPresenter(this);
    }

    public PatientDashboardDetailsPresenter(Patient mPatient, PatientDAO patientDAO,
                                            PatientDashboardContract.ViewPatientDetails mPatientDetailsView,
                                            VisitApi visitApi, PatientApi patientApi) {
        this.mPatientDetailsView = mPatientDetailsView;
        this.visitApi = visitApi;
        this.patientApi = patientApi;
        this.patientDAO = patientDAO;
        this.mPatient = mPatient;
        this.mPatientDetailsView.setPresenter(this);
    }

    @Override
    public void synchronizePatient() {
        if(NetworkUtils.isOnline()) {
            mPatientDetailsView.showDialog(R.string.action_synchronize_patients);
            syncDetailsData();
            syncVisitsData();
            syncVitalsData();
        }
        else {
            reloadPatientData(mPatient);
            mPatientDetailsView.showToast(R.string.synchronize_patient_network_error, true);
        }
      }

    public void updatePatientDataFromServer(){
        if(NetworkUtils.isOnline()) {
            syncDetailsData();
            syncVisitsData();
            syncVitalsData();
        }
    }

    private void updatePatientData(final Patient patient) {
        if (patientDAO.updatePatient(mPatient.getId(), patient)) {
            mPatient = patientDAO.findPatientByUUID(patient.getUuid());
            reloadPatientData(mPatient);
        } else {
            mPatientDetailsView.showToast(R.string.get_patient_from_database_error, true);
        }
    }

    @Override
    public void reloadPatientData(Patient patient) {
        mPatientDetailsView.resolvePatientDataDisplay(patient);
    }

    @Override
    public void subscribe() {
        updatePatientDataFromServer();
        mPatient = patientDAO.findPatientByID(mPatient.getId().toString());
        mPatientDetailsView.setMenuTitle(mPatient.getPerson().getName().getNameString(), mPatient.getIdentifier().getIdentifier());
        mPatientDetailsView.resolvePatientDataDisplay(patientDAO.findPatientByID(mPatient.getId().toString()));
        if (!NetworkUtils.isOnline()) {
            mPatientDetailsView.attachSnackbarToActivity();
        }

    }

    /*
    * Sync Vitals
    */
    private void syncVitalsData() {
        visitApi.syncLastVitals(mPatient.getUuid());
    }

    /*
    * Sync Visits
    */
    private void syncVisitsData() {
        visitApi.syncVisitsData(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mPatientDetailsView.showToast(R.string.synchronize_patient_successful, false);
                mPatientDetailsView.dismissDialog();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientDetailsView.showToast(R.string.synchronize_patient_error, true);
                mPatientDetailsView.dismissDialog();
            }
        });
    }

    /*
    * Download Patient
    */
    private void syncDetailsData() {
        patientApi.downloadPatientByUuid(mPatient.getUuid(), new DownloadPatientCallbackListener() {
            @Override
            public void onPatientDownloaded(Patient patient) {
                updatePatientData(patient);
            }

            @Override
            public void onPatientPhotoDownloaded(Patient patient) {
                updatePatientData(patient);
            }

            @Override
            public void onResponse() {
                // This method is intentionally empty
            }
            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientDetailsView.showToast(R.string.synchronize_patient_error, true);
            }
        });
    }

}
