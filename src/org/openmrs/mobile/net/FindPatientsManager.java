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

package org.openmrs.mobile.net;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.FindPatientsSearchActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.mappers.PatientMapper;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {

    private static final String PATIENT_LAST_VIEWED_QUERY = "patient?lastviewed=" + API.FULL_VERSION_NEXT_PARAM;
    private static final String SENDING_REQUEST = "Sending request to : ";

    public FindPatientsManager(Context context) {
        super(context);
    }

    public void findPatient(final String query, final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.PATIENT_QUERY + query + API.FULL_VERSION_NEXT_PARAM;
        logger.d(SENDING_REQUEST + patientsURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                ArrayList<Patient> patientsList = new ArrayList<Patient>();
                try {
                    JSONArray patientsJSONList = response.getJSONArray(RESULTS_KEY);

                    for (int i = 0; i < patientsJSONList.length(); i++) {
                        patientsList.add(PatientMapper.map(patientsJSONList.getJSONObject(i)));
                    }

                    if (mOpenMRS.getCurrentActivity() instanceof  FindPatientsSearchActivity) {
                        ((FindPatientsSearchActivity) mOpenMRS.getCurrentActivity()).updatePatientsData(searchId, patientsList);
                    }

                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((FindPatientsSearchActivity) mContext).stopLoader(searchId);
                    }
                }
        );
        queue.add(jsObjRequest);
    }

    public void getLastViewedPatient() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + PATIENT_LAST_VIEWED_QUERY;
        logger.d(SENDING_REQUEST + patientsURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                List<Patient> patientsList = new ArrayList<Patient>();
                try {
                    JSONArray patientsJSONList = response.getJSONArray(RESULTS_KEY);

                    for (int i = 0; i < patientsJSONList.length(); i++) {
                        patientsList.add(PatientMapper.map(patientsJSONList.getJSONObject(i)));
                    }

                    FindPatientLastViewedFragment.setLastViewedPatientList(patientsList);

                    if (mOpenMRS.getCurrentActivity() instanceof FindPatientsActivity) {
                        FragmentManager fm = ((FindPatientsActivity) mOpenMRS.getCurrentActivity()).getSupportFragmentManager();
                        FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                                .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                        if (fragment != null) {
                            fragment.updatePatientsData();
                        }
                    }
                    FindPatientLastViewedFragment.setRefreshing(false);
                } catch (JSONException e) {
                    logger.d(e.toString());
                    if (mOpenMRS.getCurrentActivity() instanceof FindPatientsActivity) {
                        FragmentManager fm = ((FindPatientsActivity) mOpenMRS.getCurrentActivity()).getSupportFragmentManager();
                        FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                                .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                        if (fragment != null) {
                            fragment.stopLoader();
                        }
                    }
                    FindPatientLastViewedFragment.setLastViewedPatientList(new ArrayList<Patient>());
                    FindPatientLastViewedFragment.setRefreshing(false);
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (mOpenMRS.getCurrentActivity() instanceof FindPatientsActivity) {
                    FragmentManager fm = ((FindPatientsActivity) mOpenMRS.getCurrentActivity()).getSupportFragmentManager();
                    FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                            .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                    if (fragment != null) {
                        fragment.stopLoader();
                    }
                }
                FindPatientLastViewedFragment.setLastViewedPatientList(new ArrayList<Patient>());
                FindPatientLastViewedFragment.setRefreshing(false);
            }
        }
        );
        queue.add(jsObjRequest);
    }

    public void getFullPatientData(final String patientUUID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.PATIENT_DETAILS + File.separator
                + patientUUID + API.FULL_VERSION;
        logger.d(SENDING_REQUEST + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                if (mContext instanceof PatientDashboardActivity) {
                    ((PatientDashboardActivity) mContext).updatePatientDetailsData(PatientMapper.map(response));
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (mContext instanceof PatientDashboardActivity) {
                            ((PatientDashboardActivity) mContext).stopLoader(true);
                        }
                    }
                }
        );
        queue.add(jsObjRequest);
    }
}
