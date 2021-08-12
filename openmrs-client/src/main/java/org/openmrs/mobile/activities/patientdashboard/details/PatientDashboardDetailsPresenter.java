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

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DownloadPatientCallback;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.NetworkUtils;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import com.openmrs.android_sdk.library.api.repository.VisitRepository;

public class PatientDashboardDetailsPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDetailsPresenter {
    private PatientDashboardContract.ViewPatientDetails mPatientDetailsView;
    private VisitRepository visitRepository;
    private PatientRepository patientRepository;
    private PatientDAO patientDAO;

    public PatientDashboardDetailsPresenter(String id,
                                            PatientDashboardContract.ViewPatientDetails mPatientDetailsView) {
        this.mPatientDetailsView = mPatientDetailsView;
        this.visitRepository = new VisitRepository();
        this.patientRepository = new PatientRepository();
        this.patientDAO = new PatientDAO();
        this.mPatient = patientDAO.findPatientByID(id);
        this.mPatientDetailsView.setPresenter(this);
    }

    public PatientDashboardDetailsPresenter(Patient mPatient, PatientDAO patientDAO,
                                            PatientDashboardContract.ViewPatientDetails mPatientDetailsView,
                                            VisitRepository visitRepository, PatientRepository patientRepository) {
        this.mPatientDetailsView = mPatientDetailsView;
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.patientDAO = patientDAO;
        this.mPatient = mPatient;
        this.mPatientDetailsView.setPresenter(this);
    }

    @Override
    public void synchronizePatient() {
        if (NetworkUtils.isOnline()) {
            mPatientDetailsView.showDialog(R.string.action_synchronize_patients);
            syncDetailsData();
            syncVisitsData();
            syncVitalsData();
        } else {
            reloadPatientData(mPatient);
            mPatientDetailsView.showToast(R.string.synchronize_patient_network_error, true);
        }
    }

    public void updatePatientDataFromServer() {
        if (NetworkUtils.isOnline()) {
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
        mPatientDetailsView.resolvePatientDataDisplay(patientDAO.findPatientByID(mPatient.getId().toString()));
        mPatientDetailsView.setMenuTitle(mPatient.getName().getNameString(), mPatient.getIdentifier().getIdentifier());
        if (!NetworkUtils.isOnline()) {
            mPatientDetailsView.attachSnackbarToActivity();
        }
    }

    /*
     * Sync Vitals
     */
    private void syncVitalsData() {
        visitRepository.syncLastVitals(mPatient.getUuid());
    }

    /*
     * Sync Visits
     */
    private void syncVisitsData() {
        visitRepository.syncVisitsData(mPatient, new DefaultResponseCallback() {
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
        patientRepository.downloadPatientByUuid(mPatient.getUuid(), new DownloadPatientCallback() {
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
