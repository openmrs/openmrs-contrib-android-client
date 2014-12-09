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

package org.openmrs.client.net;

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
import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.client.models.mappers.PatientMapper;
import org.openmrs.client.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.client.utilities.PatientCacheHelper;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {

    private static final String PATIENT_LAST_VIEWED_QUERY = "patient?lastviewed";
    private static final String SENDING_REQUEST = "Sending request to : ";

    public FindPatientsManager(Context context) {
        super(context);
    }

    public void findPatient(final String query, final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.PATIENT_QUERY + query;
        logger.d(SENDING_REQUEST + patientsURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray patientsJSONList = response.getJSONArray(RESULTS_KEY);

                    if (patientsJSONList.length() > 0) {
                        for (int i = 0; i < patientsJSONList.length(); i++) {
                            getFullPatientData(patientsJSONList.getJSONObject(i).getString(UUID_KEY), searchId);
                        }
                    } else {
                        ((FindPatientsSearchActivity) mContext).updatePatientsData(searchId);
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

    public void getLastViewedPatient(final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + PATIENT_LAST_VIEWED_QUERY;
        logger.d(SENDING_REQUEST + patientsURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray patientsJSONList = response.getJSONArray(RESULTS_KEY);

                    if (patientsJSONList.length() > 0) {
                        for (int i = 0; i < patientsJSONList.length(); i++) {
                            getFullLastViewedPatientData(patientsJSONList.getJSONObject(i).getString(UUID_KEY), searchId);
                        }
                    } else {
                        FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
                        FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                                .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                        if (fragment != null) {
                            fragment.updatePatientsData(searchId);
                        }
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
                FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
                FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                        .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                if (fragment != null) {
                    fragment.stopLoader(searchId);
                }
            }
        }
        );
        queue.add(jsObjRequest);
    }

    public void getFullPatientData(final String patientUUID) {
        //method called by PatientDashboardActivity. searchId is never used
       getFullPatientData(patientUUID, null);
    }

    public void getFullPatientData(final String patientUUID, final Integer searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.PATIENT_DETAILS
                + patientUUID + API.FULL_VERSION;
        logger.d(SENDING_REQUEST + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                if (mContext instanceof PatientDashboardActivity) {
                    ((PatientDashboardActivity) mContext).updatePatientDetailsData(PatientMapper.map(response));
                } else {
                    if (PatientCacheHelper.getId() == searchId) {
                        PatientCacheHelper.addPatient(PatientMapper.map(response));
                    }
                    ((FindPatientsSearchActivity) mContext).updatePatientsData(searchId);
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (mContext instanceof PatientDashboardActivity) {
                            ((PatientDashboardActivity) mContext).stopLoader(true);
                        } else {
                            ((FindPatientsSearchActivity) mContext).stopLoader(searchId);
                        }
                    }
                }
        );
        queue.add(jsObjRequest);
        queue.start();
    }

    public void getFullLastViewedPatientData(final String patientUUID, final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.PATIENT_DETAILS
                + patientUUID + API.FULL_VERSION;
        logger.d(SENDING_REQUEST + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                if (PatientCacheHelper.getId() == searchId) {
                    PatientCacheHelper.addPatient(PatientMapper.map(response));
                }
                FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
                FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                        .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                if (fragment != null) {
                    fragment.updatePatientsData(searchId);
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                FragmentManager fm = ((FindPatientsActivity) mContext).getSupportFragmentManager();
                FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                        .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                if (fragment != null) {
                    fragment.stopLoader(searchId);
                }
            }
        }
        );
        queue.add(jsObjRequest);
        queue.start();
    }
}
