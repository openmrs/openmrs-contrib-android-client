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

import androidx.lifecycle.LifecycleOwner;

import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.Visit;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;

import java.util.List;

public interface PatientDashboardContract {

    /*
     * Views
     */

    interface ViewPatientMain extends BaseView<PatientDashboardMainPresenter> {
    }

    interface ViewPatientDetails extends ViewPatientMain {
        void attachSnackbarToActivity();

        void resolvePatientDataDisplay(Patient patient);

        void showDialog(int resId);

        void dismissDialog();

        void showToast(int stringRes, boolean error);

        void setMenuTitle(String nameString, String identifier);
    }

    interface ViewPatientDiagnosis extends ViewPatientMain {
        void setDiagnosesToDisplay(List<String> encounters);
    }

    interface ViewPatientVisits extends ViewPatientMain {
        void showErrorToast(String message);

        void showErrorToast(int messageId);

        void dismissCurrentDialog();

        void toggleRecyclerListVisibility(boolean isVisible);

        void setVisitsToDisplay(List<Visit> visits);

        void goToVisitDashboard(Long visitID);

        void showStartVisitDialog(boolean isVisitPossible);

        void showStartVisitProgressDialog();

        void setPatient(Patient mPatient);
    }

    interface ViewPatientVitals extends ViewPatientMain {
        void showNoVitalsNotification();

        void showEncounterVitals(Encounter encounter);

        void startFormDisplayActivity(Encounter lastVitalsEncounter);

        void showErrorToast(String errorMessage);
    }

    interface ViewPatientCharts extends ViewPatientMain {
        void populateList(List<Visit> visits);

        void setEmptyListVisibility(boolean visibility);
    }

    interface ViewPatientAllergy extends ViewPatientMain {
        void showAllergyList(List<Allergy> allergies);
    }

    /*
     * Presenters
     */
    interface PatientDashboardMainPresenter extends BasePresenterContract {
        void deletePatient();

        long getPatientId();
    }

    interface PatientDetailsPresenter extends PatientDashboardMainPresenter {
        void synchronizePatient();

        void updatePatientDataFromServer();

        void reloadPatientData(Patient patient);
    }

    interface PatientDiagnosisPresenter extends PatientDashboardMainPresenter {
        void loadDiagnosis();
    }

    interface PatientVisitsPresenter extends PatientDashboardMainPresenter {
        void showStartVisitDialog();

        void syncVisits();

        void startVisit();
    }

    interface PatientVitalsPresenter extends PatientDashboardMainPresenter {
        void startFormDisplayActivityWithEncounter();
    }

    interface PatientChartsPresenter extends PatientDashboardMainPresenter {
    }

    interface PatientAllergyPresenter extends PatientDashboardMainPresenter {
        void getAllergy(LifecycleOwner lifecycleOwner);

        void deleteAllergy(String uuid);
    }
}
