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
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.net.GeneralErrorListener;

public class StartVisitListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final String mPatientUUID;
    private final long mPatientID;
    private PatientDashboardActivity mCaller;
    private EncounterService mService;
    private Encountercreate mEncountercreate;


    public StartVisitListener(String patientUUD, long patientID, PatientDashboardActivity caller) {
        this(patientUUD, patientID);
        mCaller = caller;
    }

    public StartVisitListener(long patientID, Encountercreate encountercreate, EncounterService service) {
        mPatientID = patientID;
        mPatientUUID=new PatientDAO().findPatientByID(Long.toString(mPatientID)).getUuid();
        mService = service;
        mEncountercreate = encountercreate;
    }

    private StartVisitListener(String patientUUID, long patientID) {
        mPatientUUID = patientUUID;
        mPatientID = patientID;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        if (null != mCaller) {
            mCaller.stopLoader(true);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        try {
            Visit visit=VisitMapper.map(response);
            long visitID = new VisitDAO().saveVisit(visit, mPatientID);
            if (null != mCaller) {
                mCaller.visitStarted(visitID, visitID <= 0);
            }
            else {
                mEncountercreate.setVisit(visit.getUuid());
                new EncounterService().syncEncounter(mEncountercreate);
            }
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public String getPatientUUID() {
        return mPatientUUID;
    }
}
