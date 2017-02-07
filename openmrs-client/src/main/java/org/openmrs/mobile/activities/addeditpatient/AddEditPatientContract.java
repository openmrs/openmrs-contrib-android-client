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

package org.openmrs.mobile.activities.addeditpatient;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public interface AddEditPatientContract {

    interface View extends BaseView<Presenter> {

        void finishPatientInfoActivity();

        void setErrorsVisibility(boolean givenNameError,
                                 boolean familyNameError,
                                 boolean dayOfBirthError,
                                 boolean addressError,
                                 boolean countryError,
                                 boolean genderError);

        void scrollToTop();

        void hideSoftKeys();

        void setProgressBarVisibility(boolean visibility);

        void showSimilarPatientDialog(List<Patient> patients, Patient newPatient);

        void startPatientDashbordActivity(Patient patient);

        void showUpgradeRegistrationModuleInfo();
    }

    interface Presenter extends BasePresenterContract {

        Patient getPatientToUpdate();

        boolean isRegisteringPatient();

        void confirmRegister(Patient patient);

        void confirmUpdate(Patient patient);

        void finishPatientInfoActivity();

        void registerPatient();

        void updatePatient(Patient patient);
    }

}
