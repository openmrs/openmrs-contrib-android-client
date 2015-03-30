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

package org.openmrs.mobile.listeners.findPatients;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.FindPatientsSearchActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.mappers.PatientMapper;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;

import java.util.ArrayList;
import java.util.List;

public final class FindPatientListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final FindPatientsSearchActivity mActivityCaller;
    private final String mLastQuery;
    private final int mSearchId;

    public FindPatientListener(String lastQuery, int searchId, FindPatientsSearchActivity activityCaller) {
        mSearchId = searchId;
        mLastQuery = lastQuery;
        mActivityCaller = activityCaller;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        mActivityCaller.stopLoader(mSearchId);
    }

    @Override
    public void onResponse(JSONObject response) {
        List<Patient> patientsList = new ArrayList<Patient>();
        mLogger.d(response.toString());

        try {
            JSONArray patientsJSONList = response.getJSONArray(BaseManager.RESULTS_KEY);
            for (int i = 0; i < patientsJSONList.length(); i++) {
                patientsList.add(PatientMapper.map(patientsJSONList.getJSONObject(i)));
            }

            mActivityCaller.updatePatientsData(mSearchId, patientsList);

        } catch (JSONException e) {
            mLogger.d(e.toString());
        }
    }

    public String getLastQuery() {
        return mLastQuery;
    }
}
