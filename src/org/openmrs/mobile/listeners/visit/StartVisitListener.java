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
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.CaptureVitalsActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.GeneralErrorListener;

public class StartVisitListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final String mPatientUUID;
    private final long mPatientID;
    private PatientDashboardActivity mCallerPDA;
    private CaptureVitalsActivity mCallerCVA;

    public StartVisitListener(String patientUUD, long patientID, PatientDashboardActivity callerPDA) {
        this(patientUUD, patientID);
        mCallerPDA = callerPDA;
    }

    public StartVisitListener(String patientUUD, long patientID, CaptureVitalsActivity callerCVA) {
        this(patientUUD, patientID);
        mCallerCVA = callerCVA;
    }

    private StartVisitListener(String patientUUID, long patientID) {
        mPatientUUID = patientUUID;
        mPatientID = patientID;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        if (null != mCallerPDA) {
            mCallerPDA.stopLoader(true);
        } else {
            mCallerCVA.dismissProgressDialog(true, R.string.start_visit_successful,
                    R.string.start_visit_error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        try {
            long visitID = new VisitDAO().saveVisit(VisitMapper.map(response), mPatientID);
            callerAction(visitID);
        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public long offlineAction(long time) {
        Visit visit = new Visit(mPatientID, OpenMRS.getInstance().getVisitTypeDisplay(),
                LocationDAO.findLocationByName(OpenMRS.getInstance().getLocation()).getParentLocationDisplay(), time);
        long visitID = new VisitDAO().saveVisit(visit, mPatientID);

        callerAction(visitID);

        return visitID;
    }

    private void callerAction(long visitID) {
        if (null != mCallerPDA) {
            mCallerPDA.visitStarted(visitID, visitID <= 0);
        } else {
            mCallerCVA.dismissProgressDialog(false, R.string.start_visit_successful,
                    R.string.start_visit_error);
            mCallerCVA.startCheckedFormEntryForResult(mPatientUUID);
        }
    }

    public String getPatientUUID() {
        return mPatientUUID;
    }
}
