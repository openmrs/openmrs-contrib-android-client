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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;

public class FindVisitsByPatientUUIDListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    protected final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    protected final String mPatientUUID;
    protected final long mPatientID;
    protected PatientDashboardActivity mCallerPDA;
    protected ACBaseActivity mCallerAdapter;
    private final VisitDAO visitDAO = new VisitDAO();
    private boolean mErrorOccurred;

    public FindVisitsByPatientUUIDListener(String patientUUID, long patientID, ACBaseActivity callerAdapter) {
        mPatientUUID = patientUUID;
        mPatientID = patientID;
        if (callerAdapter instanceof PatientDashboardActivity) {
            mCallerPDA = (PatientDashboardActivity) callerAdapter;
        } else {
            mCallerAdapter = callerAdapter;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mErrorOccurred = true;
        updateData();
        super.onErrorResponse(error);
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());

        try {
            JSONArray visitResultJSON = response.getJSONArray(BaseManager.RESULTS_KEY);
            if (visitResultJSON.length() > 0) {
                for (int i = 0; i < visitResultJSON.length(); i++) {
                    Visit visit = VisitMapper.map(visitResultJSON.getJSONObject(i));
                    long visitId = visitDAO.getVisitsIDByUUID(visit.getUuid());

                    if (visitId > 0) {
                        visitDAO.updateVisit(visit, visitId, mPatientID);
                    } else {
                        visitDAO.saveVisit(visit, mPatientID);
                    }
                }
            }
        } catch (JSONException e) {
            mErrorOccurred = true;
            mLogger.d(e.toString());
        } finally {
            updateData();
        }
    }

    public String getPatientUUID() {
        return mPatientUUID;
    }

    public long getPatientID() {
        return mPatientID;
    }

    public void updateData() {
            if (null != mCallerPDA) {
                mCallerPDA.updatePatientVisitsData(mErrorOccurred);
            } else {
                mCallerAdapter.dismissProgressDialog(mErrorOccurred,
                        R.string.find_patients_row_toast_patient_saved,
                        R.string.find_patients_row_toast_patient_save_error);
            }
    }
}
