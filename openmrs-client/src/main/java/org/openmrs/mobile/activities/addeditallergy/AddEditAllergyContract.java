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

package org.openmrs.mobile.activities.addeditallergy;

import androidx.fragment.app.Fragment;

import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.SystemProperty;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;

public interface AddEditAllergyContract {
    interface View extends BaseView<AddEditAllergyContract.Presenter> {

        void setConceptMembers(ConceptMembers conceptMembers, String reactions);

        void setSeverity(SystemProperty systemProperty);

        void showLoading(boolean loading, boolean exitScreen);

        void fillAllergyToUpdate(Allergy mAllergy);
    }

    interface Presenter extends BasePresenterContract {
        void fetchSystemProperties(Fragment fragment);

        void createAllergy(AllergyCreate allergyCreate);

        void updateAllergy(AllergyCreate allergyCreate);
    }
}

