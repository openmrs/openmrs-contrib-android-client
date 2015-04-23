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

import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.CaptureVitalsActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.bundle.VisitsManagerBundle;
import org.openmrs.mobile.intefaces.VisitDashboardCallbackListener;
import org.openmrs.mobile.listeners.offline.MultiPartOfflineRequestListener;
import org.openmrs.mobile.listeners.visit.CheckVisitBeforeStartListener;
import org.openmrs.mobile.listeners.visit.EndVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.FindVisitByUUIDAfterOfflineCaptureVitalsListener;
import org.openmrs.mobile.listeners.visit.FindVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.FindVisitsByPatientUUIDListener;
import org.openmrs.mobile.listeners.visit.LastVitalsListener;
import org.openmrs.mobile.listeners.visit.StartVisitListener;
import org.openmrs.mobile.listeners.visit.VisitTypeListener;

public final class VisitsHelper {

    private VisitsHelper() {
    }

    public static LastVitalsListener createLastVitalsListener(Long patientID) {
        return new LastVitalsListener(patientID);
    }

    public static VisitsManagerBundle createBundle(String patientUUID, Long patientID) {
        VisitsManagerBundle bundle = new VisitsManagerBundle();
        bundle.putStringField(VisitsManagerBundle.PATIENT_UUID_KEY, patientUUID);
        bundle.putLongField(VisitsManagerBundle.PATIENT_ID_KEY, patientID);
        return bundle;
    }

    public static FindVisitsByPatientUUIDListener createVisitsByPatientUUIDListener(String patientUUID, Long patientID, ACBaseActivity caller) {
        return new FindVisitsByPatientUUIDListener(patientUUID, patientID, caller);
    }

    public static CheckVisitBeforeStartListener createCheckVisitsBeforeStartListener(String patientUUID, long patientID, CaptureVitalsActivity callerAdapter) {
        return new CheckVisitBeforeStartListener(patientUUID, patientID, callerAdapter);
    }

    public static CheckVisitBeforeStartListener createCheckVisitsBeforeStartListener(String patientUUID, long patientID, PatientDashboardActivity callerAdapter) {
        return new CheckVisitBeforeStartListener(patientUUID, patientID, callerAdapter);
    }

    public static FindVisitByUUIDListener createFindVisitCallbacksListener(Long patientID, String visitUUID, VisitDashboardCallbackListener callbackListener) {
        return new FindVisitByUUIDListener(patientID, visitUUID, callbackListener);
    }

    public static FindVisitByUUIDAfterOfflineCaptureVitalsListener createFindVisitCallbacksListener(Long patientID, String visitUUID, MultiPartOfflineRequestListener callbackListener) {
        return new FindVisitByUUIDAfterOfflineCaptureVitalsListener(patientID, visitUUID, callbackListener);
    }

    public static EndVisitByUUIDListener createEndVisitsByUUIDListener(String visitUUID, long patientID, long visitID, VisitDashboardActivity caller) {
        return new EndVisitByUUIDListener(visitUUID, patientID, visitID, caller);
    }

    public static StartVisitListener createStartVisitListener(String patientUUID, long patientID, CaptureVitalsActivity caller) {
        return new StartVisitListener(patientUUID, patientID, caller);
    }

    public static StartVisitListener createStartVisitListener(String patientUUID, long patientID, PatientDashboardActivity caller) {
        return new StartVisitListener(patientUUID, patientID, caller);
    }

    public static VisitTypeListener createVisitTypeListener() {
        return new VisitTypeListener();
    }
}
