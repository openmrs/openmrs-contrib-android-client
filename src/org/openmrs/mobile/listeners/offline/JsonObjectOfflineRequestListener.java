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

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.OfflineRequestManager;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class JsonObjectOfflineRequestListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final OfflineRequest mOfflineRequest;
    private final OfflineRequestManager mManagerCaller;
    private final boolean mSendNext;

    public JsonObjectOfflineRequestListener(OfflineRequest offlineRequest, boolean sendNext, OfflineRequestManager managerCaller) {
        mOfflineRequest = offlineRequest;
        mSendNext = sendNext;
        mManagerCaller = managerCaller;
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());

        boolean updateSuccessful = true;

        if (ApplicationConstants.OfflineRequests.START_VISIT.equals(mOfflineRequest.getActionName())) {
            try {
                Visit updatedVisit = VisitMapper.map(response);
                Visit visit = new VisitDAO().getVisitsByID(mOfflineRequest.getObjectID());
                visit.setUuid(updatedVisit.getUuid());
                new VisitDAO().updateVisit(visit, visit.getId(), visit.getPatientID());
            } catch (JSONException e) {
                mLogger.d(e.toString());
                updateSuccessful = false;
            }
        }

        if (updateSuccessful) {
            mManagerCaller.removeFromQueue(mOfflineRequest, mSendNext);
        }
    }
}
