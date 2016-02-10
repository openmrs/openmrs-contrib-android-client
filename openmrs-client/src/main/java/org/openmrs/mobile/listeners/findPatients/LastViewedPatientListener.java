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

import android.support.v4.app.FragmentManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.mappers.PatientMapper;
import org.openmrs.mobile.net.BaseManager;
import org.openmrs.mobile.net.GeneralErrorListener;
import java.util.ArrayList;
import java.util.List;

public class LastViewedPatientListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private FragmentManager mFragmentManager;

    public LastViewedPatientListener(FindPatientsActivity caller) {
        mFragmentManager = caller.getSupportFragmentManager();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        refreshFragment(null, FindPatientsActivity.FragmentMethod.StopLoader);
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

            refreshFragment(patientsList, FindPatientsActivity.FragmentMethod.Update);

        } catch (JSONException e) {
            mLogger.d(e.toString());
            refreshFragment(null, FindPatientsActivity.FragmentMethod.StopLoader);
        }
    }

    private void refreshFragment(List<Patient> patientsList, FindPatientsActivity.FragmentMethod method) {
        List<Patient> localPatientsList;
        if (null == patientsList) {
            localPatientsList = new ArrayList<Patient>();
        } else {
            localPatientsList = new ArrayList<Patient>(patientsList);
        }

        FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) mFragmentManager
                .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

        FindPatientLastViewedFragment.setLastViewedPatientList(localPatientsList);
        if (fragment != null) {
            if (method.equals(FindPatientsActivity.FragmentMethod.StopLoader)) {
                fragment.stopLoader();
            } else if (method.equals(FindPatientsActivity.FragmentMethod.Update)) {
                fragment.updatePatientsData();
            }
        }

        FindPatientLastViewedFragment.setRefreshing(false);
    }
}
