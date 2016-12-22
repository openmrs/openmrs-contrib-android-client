package org.openmrs.mobile.activities.editpatient;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.retrofit.Patient;

public interface EditPatientContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void finishEditActivity();

        void setErrorsVisibility(boolean givenNameError,
                                 boolean familyNameError,
                                 boolean dayOfBirthError,
                                 boolean genderError,
                                 boolean addressError);

        void scrollToTop();

        void hideSoftKeys();

        void setProgressBarVisibility(boolean visibility);
    }

    interface Presenter extends BasePresenter {

        void confirm(Patient patient);

        void editPatient();

        String getPatientId();
    }
}
