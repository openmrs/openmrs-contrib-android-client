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

package org.openmrs.mobile.activities.formadmission;

import java.util.List;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Provider;

public interface FormAdmissionContract {

    interface View extends BaseView<Presenter> {

        void updateProviderAdapter(List<Provider> providerList);

        void showToast(String error);

        void updateLocationAdapter(List<Location> results);

        void enableSubmitButton(boolean value);

        void quitFormEntry();
    }

    interface Presenter extends BasePresenterContract {

        void getProviders(FormAdmissionFragment formAdmissionFragment);

        void updateViews(List<Provider> providerList);

        void getLocation(String url);

        void createEncounter(String admittedByPerson, String admittedToPerson);
    }
}
