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

import org.openmrs.mobile.models.Patient;

import java.util.ArrayList;
import java.util.List;

public final class PatientCacheHelper {
    private static List<Patient> sCachedPatients = new ArrayList<Patient>();
    private static int searchId;

    private PatientCacheHelper() {

    }

    public static List<Patient> getCachedPatients() {
        return sCachedPatients;
    }

    public static void setId(int id) {
        searchId = id;
    }

    public static int getId() {
        return searchId;
    }

    public static void addPatient(Patient patient) {
        sCachedPatients.add(patient);
    }

    public static void clearCache() {
        sCachedPatients.clear();
    }
}
