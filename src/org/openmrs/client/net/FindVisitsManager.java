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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Visit;
import org.openmrs.client.models.mappers.VisitMapper;
import org.openmrs.client.net.volley.wrappers.JsonObjectRequestWrapper;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindVisitsManager extends BaseManager {
    private static final String VISIT_QUERY = "visit?patient=";

    private int mExpectedResponses;
    private boolean mErrorOccurred;

    public FindVisitsManager(Context context) {
        super(context);
    }

    public void findActiveVisitsForPatientByUUID(final String patientUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + VISIT_QUERY + patientUUID;
        logger.d("Sending request to : " + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray visitResultJSON = response.getJSONArray(RESULTS_KEY);
                    if (mContext instanceof PatientDashboardActivity) {
                        mExpectedResponses = visitResultJSON.length();
                    }
                    if (visitResultJSON.length() > 0) {
                        for (int i = 0; i < visitResultJSON.length(); i++) {
                            findVisitByUUID(visitResultJSON.getJSONObject(i).getString(UUID_KEY), patientID);
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
                if (mContext instanceof PatientDashboardActivity) {
                    ((PatientDashboardActivity) mContext).stopLoader(true);
                }
            }
        });
        queue.add(jsObjRequest);
    }

    public void findVisitByUUID(final String visitUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.VISIT_DETAILS + visitUUID
                + API.FULL_VERSION;
        logger.d("Sending request to : " + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                try {
                    if (mContext instanceof PatientDashboardActivity) {
                        Thread thread = new Thread()
                        {
                            @Override
                            public void run() {
                                try {
                                    Visit visit = VisitMapper.map(response);
                                    long visitId = new VisitDAO().getVisitsIDByUUID(visit.getUuid());

                                    if (visitId > 0) {
                                        new VisitDAO().updateVisit(visit, visitId, patientID);
                                    } else {
                                        new VisitDAO().saveVisit(visit, patientID);
                                    }
                                } catch (JSONException e) {
                                    logger.d(e.toString());
                                }
                            }
                        };
                        thread.start();
                        subtractExpectedResponses(false);
                    } else {
                        new VisitDAO().saveVisit(VisitMapper.map(response), patientID);
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
                subtractExpectedResponses(true);
            }
        }
        );
        queue.add(jsObjRequest);
    }

    public void subtractExpectedResponses(boolean errorOccurred) {
        mExpectedResponses--;
        if (errorOccurred) {
            mErrorOccurred = errorOccurred;
        }
        if (mExpectedResponses == 0) {
            ((PatientDashboardActivity) mContext).updatePatientVisitsData(mErrorOccurred);
        }
    }
}
