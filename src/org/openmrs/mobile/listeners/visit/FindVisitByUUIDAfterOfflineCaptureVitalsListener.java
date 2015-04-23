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

package org.openmrs.mobile.listeners.visit;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.offline.MultiPartOfflineRequestListener;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.GeneralErrorListener;

public class FindVisitByUUIDAfterOfflineCaptureVitalsListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private VisitDAO visitDAO = new VisitDAO();
    private final String mVisitUUID;
    private final Long mPatientID;
    private final MultiPartOfflineRequestListener mManagerCaller;



    public FindVisitByUUIDAfterOfflineCaptureVitalsListener(Long patientID, String visitUUID, MultiPartOfflineRequestListener callbackListener) {
        mPatientID = patientID;
        mVisitUUID = visitUUID;
        mManagerCaller = callbackListener;
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        boolean updateSuccessful = true;
        try {
            Visit visit = VisitMapper.map(response);
            long visitId = visitDAO.getVisitsIDByUUID(visit.getUuid());
            if (visitId > 0) {
                visitDAO.updateVisitAfterOfflineCaptureVitals(visit, visitId);
            } else {
                visitDAO.saveVisit(visit, mPatientID);
            }
        } catch (JSONException e) {
            mLogger.d(e.toString());
            updateSuccessful = false;
        }

        if (updateSuccessful) {
            mManagerCaller.removeFromQueue();
        }
    }

    public String getVisitUUID() {
        return mVisitUUID;
    }
}
