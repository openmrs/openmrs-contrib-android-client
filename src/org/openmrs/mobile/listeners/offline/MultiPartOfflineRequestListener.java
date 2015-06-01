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

package org.openmrs.mobile.listeners.offline;

import com.android.volley.Response;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.OfflineRequestManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;

public class MultiPartOfflineRequestListener extends GeneralErrorListener implements Response.Listener<String> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final OfflineRequest mOfflineRequest;
    private final boolean mSendNext;
    private final OfflineRequestManager mManagerCaller;

    public MultiPartOfflineRequestListener(OfflineRequest offlineRequest, boolean sendNext, OfflineRequestManager managerCaller) {
        mOfflineRequest = offlineRequest;
        mSendNext = sendNext;
        mManagerCaller = managerCaller;
    }

    @Override
    public void onResponse(String response) {
        mLogger.d(response);

        String visitUUID = new VisitDAO().getVisitsByID(mOfflineRequest.getObjectID()).getUuid();
        Long patientID = new PatientDAO().findPatientByUUID(mOfflineRequest.getObjectUUID()).getId();
        new VisitsManager().findVisitByUUIDAfterCaptureVitals(
                VisitsHelper.createFindVisitCallbacksListener(patientID, visitUUID, this));
    }

    public void removeFromQueue() {
        mManagerCaller.removeFromQueue(mOfflineRequest, mSendNext);
    }
}
