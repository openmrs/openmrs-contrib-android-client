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

package org.openmrs.mobile.net.helpers;

import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.FindPatientsSearchActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.listeners.findPatients.FindPatientListener;
import org.openmrs.mobile.listeners.findPatients.FullPatientDataListener;
import org.openmrs.mobile.listeners.findPatients.LastViewedPatientListener;
import java.net.URLEncoder;

public final class FindPatientsHelper {
    private static OpenMRS mOpenMRS = OpenMRS.getInstance();
    private static OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();

    private FindPatientsHelper() {
    }

    public static FindPatientListener createFindPatientListener(String lastQuery, int searchId, FindPatientsSearchActivity caller) {
        try {
            lastQuery = URLEncoder.encode(lastQuery,"UTF-8").toString();
        } catch (Exception exception) {
            mLogger.e(exception.toString());
        }
        return new FindPatientListener(lastQuery, searchId, caller);
    }

    public static LastViewedPatientListener createLastViewedPatientListener(FindPatientsActivity caller) {
        return new LastViewedPatientListener(caller);
    }

    public static FullPatientDataListener createFullPatientDataListener(String patientUUID, PatientDashboardActivity caller) {
        return new FullPatientDataListener(patientUUID, caller);
    }

    public static FullPatientDataListener createFullPatientDataListener(String patientUUID, EncounterService caller) {
        return new FullPatientDataListener(patientUUID, caller);
    }
}
