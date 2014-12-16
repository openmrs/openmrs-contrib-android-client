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

package org.openmrs.mobile.listeners.visit;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.net.GeneralErrorListener;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.utilities.DateUtils;

public class EndVisitByUUIDListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final VisitDAO visitDAO = new VisitDAO();
    private final VisitDashboardActivity mCaller;
    private final String mVisitUUID;
    private final long mPatientID;
    private final long mVisitID;


    public EndVisitByUUIDListener(String visitUUID, long patientID, long visitID, VisitDashboardActivity caller) {
        mVisitUUID = visitUUID;
        mPatientID = patientID;
        mVisitID = visitID;
        mCaller = caller;
    }

    @Override
    public void onResponse(final JSONObject response) {
        mLogger.d(response.toString());
        try {
            Visit visit = visitDAO.getVisitsByID(mVisitID);
            visit.setStopDate(DateUtils.convertTime(response.getString(VisitsManager.STOP_DATE_TIME)));
            visitDAO.updateVisit(visit, mVisitID, mPatientID);
            mCaller.moveToPatientDashboard();
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public void offlineAction(long time) {
        Visit visit = visitDAO.getVisitsByID(mVisitID);
        visit.setStopDate(time);
        visitDAO.updateVisit(visit, mVisitID, mPatientID);
        mCaller.moveToPatientDashboard();
    }

    public String getVisitUUID() {
        return mVisitUUID;
    }

    public long getVisitID() {
        return mVisitID;
    }
}
