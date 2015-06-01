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

package org.openmrs.mobile.bundle;

public class FormManagerBundle extends FieldsBundle {
    public static final String PATIENT_ID_KEY = "b_patientID";
    public static final String PATIENT_UUID_KEY = "b_patientUUID";
    public static final String VISIT_ID_KEY = "b_visitID";
    public static final String INSTANCE_PATH_KEY = "b_instancePath";

    public Long getPatientId() {
        return getLongField(PATIENT_ID_KEY);
    }

    public String getPatientUuid() {
        return getStringField(PATIENT_UUID_KEY);
    }

    public Long getVisitId() {
        return getLongField(VISIT_ID_KEY);
    }

    public String getInstancePath() {
        return getStringField(INSTANCE_PATH_KEY);
    }
}
