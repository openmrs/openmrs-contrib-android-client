/**
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

package org.openmrs.mobile.bundle;

public class VisitsManagerBundle extends FieldsBundle {
    public static final String PATIENT_UUID_KEY = "b_patient_uuid";
    public static final String PATIENT_ID_KEY = "b_patient_id";
    public static final String VISIT_UUID_KEY = "b_visits_uuid";
    public static final String VISIT_ID_KEY = "b_visits_id";

    public String getPatientUUID() {
        return getStringField(PATIENT_UUID_KEY);
    }

    public Long getPatientID() {
        return getLongField(PATIENT_ID_KEY);
    }

    public String getVisitUUID() {
        return getStringField(VISIT_UUID_KEY);
    }

    public Long getVisitID() {
        return getLongField(VISIT_ID_KEY);
    }
}
