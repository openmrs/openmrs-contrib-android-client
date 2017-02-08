package org.openmrs.mobile.activities.matchingpatients;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public interface MatchingPatientsContract {

    interface View extends BaseView<Presenter>{

        void showPatientsData(Patient patient, List<Patient> matchingPatients);

        void enableMergeButton();

        void disableMergeButton();

        void notifyUser(int no_patient_selected);

        void finishActivity();

        void showErrorToast(String message);
    }

    interface Presenter extends BasePresenterContract {

        void setSelectedPatient(Patient patient);

        void removeSelectedPatient();

        void mergePatients();

        void registerNewPatient();
    }
}
