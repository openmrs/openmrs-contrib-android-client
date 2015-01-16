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

package org.openmrs.mobile.utilities;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Mapping;

import java.util.ArrayList;
import java.util.List;

public final class MappingSolver {

    private static final String LOCATION_ID_QUESTION_TAG = "location_id";
    private static final String PROVIDER_ID_QUESTION_TAG = "provider_id";
    private static final String PATIENT_ID_QUESTION_TAG = "patient_id";

    private MappingSolver() {

    }

    public static List<Mapping> getFormMapping(String formName, String patientUUID) {
        List<Mapping> mapping = new ArrayList<Mapping>();

        if (FormsLoaderUtil.CAPTURE_VITALS_FORM_NAME.equals(formName)) {
            mapping.add(0, new Mapping(LOCATION_ID_QUESTION_TAG, OpenMRS.getInstance().getLocation()));
            mapping.add(1, new Mapping(PROVIDER_ID_QUESTION_TAG, OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_PERSON_NAME)));
            mapping.add(2, new Mapping(PATIENT_ID_QUESTION_TAG, patientUUID));
        }

        return mapping;
    }

}
