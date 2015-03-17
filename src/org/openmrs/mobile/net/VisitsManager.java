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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.CaptureVitalsActivity;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.FindPatientsSearchActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.activities.listeners.FindVisitByUUIDListener;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.mappers.ObservationMapper;
import org.openmrs.mobile.models.mappers.VisitMapper;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.io.File;
import java.util.HashMap;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class VisitsManager extends BaseManager {
    private static final String VISIT_QUERY = "visit?patient=";
    private static final String SENDING_REQUEST = "Sending request to : ";
    private int mExpectedResponses;
    private boolean mErrorOccurred;

    public VisitsManager(Context context) {
        super(context);
    }

    public void getLastVitals(final String patientUUID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.ENCOUNTER_DETAILS + "?patient=" + patientUUID
                + "&encounterType=" + ApplicationConstants.EncounterTypes.VITALS + "&v=custom:(obs:full)&limit=1&order=desc";
        mLogger.d(SENDING_REQUEST + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new EncounterDAO().saveLastVitalsEncounter(ObservationMapper.lastVitalsMap(response), patientUUID);
            }
        }
                , new GeneralErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        queue.add(jsObjRequest);
    }

    public void findVisitsByPatientUUID(final String patientUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + VISIT_QUERY + patientUUID
                + "&v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";

        mLogger.d(SENDING_REQUEST + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mLogger.d(response.toString());

                try {
                    JSONArray visitResultJSON = response.getJSONArray(RESULTS_KEY);
                    mExpectedResponses = visitResultJSON.length();
                    if (visitResultJSON.length() > 0) {
                        for (int i = 0; i < visitResultJSON.length(); i++) {
                            final Visit visit = VisitMapper.map(visitResultJSON.getJSONObject(i));

                            if (mContext instanceof PatientDashboardActivity) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        long visitId = new VisitDAO().getVisitsIDByUUID(visit.getUuid());

                                        if (visitId > 0) {
                                            new VisitDAO().updateVisit(visit, visitId, patientID);
                                        } else {
                                            new VisitDAO().saveVisit(visit, patientID);
                                        }
                                    }
                                };
                                thread.start();
                            } else {
                                new VisitDAO().saveVisit(visit, patientID);
                            }
                            subtractExpectedResponses(false);
                        }
                    } else {
                        subtractExpectedResponses(false);
                    }
                } catch (JSONException e) {
                    subtractExpectedResponses(true);
                    mLogger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                subtractExpectedResponses(true);
                super.onErrorResponse(error);
            }
        });
        queue.add(jsObjRequest);
    }

    public void findVisitByUUID(final String visitUUID, FindVisitByUUIDListener listener) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.VISIT_DETAILS + File.separator + visitUUID
                + "?v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";
        mLogger.d(SENDING_REQUEST + visitURL);
        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET, visitURL, null, listener, listener);
        queue.add(jsObjRequest);
    }

    public void inactivateVisitByUUID(final String visitUUID, final long patientID,  final long visitID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.VISIT_DETAILS + File.separator + visitUUID;
        mLogger.d(SENDING_REQUEST + visitURL);

        final String currentDate = DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stopDatetime", currentDate);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.POST, visitURL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                mLogger.d(response.toString());

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Visit visit = new VisitDAO().getVisitsByID(visitID);
                            visit.setStopDate(DateUtils.convertTime(response.getString("stopDatetime")));
                            new VisitDAO().updateVisit(visit, visitID, patientID);
                            ((VisitDashboardActivity) mContext).moveToPatientDashboard();
                        } catch (JSONException e) {
                            mLogger.d(e.toString());
                        }
                    }
                };
                thread.start();
            }
        }
                , new GeneralErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        }

        );

        queue.add(jsObjRequest);
    }

    public void createVisit(final String patientUUID, final Long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.VISIT_DETAILS;
        mLogger.d("Sending request to : " + visitURL);

        final String currentDate = DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("patient", patientUUID);
        params.put("visitType", OpenMRS.getInstance().getVisitTypeUUID());
        params.put("startDatetime", currentDate);
        params.put("location", LocationDAO.findLocationByName(OpenMRS.getInstance().getLocation()).getParentLocationUuid());

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.POST, visitURL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                mLogger.d(response.toString());

                try {
                    Visit visit = VisitMapper.map(response);
                    long visitID = new VisitDAO().saveVisit(visit, patientID);
                    if (mContext instanceof PatientDashboardActivity) {
                        ((PatientDashboardActivity) mContext).visitStarted(visitID, visitID <= 0);
                    } else if (mContext instanceof CaptureVitalsActivity) {
                        ((CaptureVitalsActivity) mContext).dismissProgressDialog(false, R.string.start_visit_successful,
                                R.string.start_visit_error);
                        ((CaptureVitalsActivity) mContext).startCheckedFormEntryForResult(
                                ((CaptureVitalsActivity) mContext).getSelectedPatientUUID()
                        );

                    }

                } catch (JSONException e) {
                    mLogger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (mContext instanceof PatientDashboardActivity) {
                    ((PatientDashboardActivity) mContext).stopLoader(true);
                } else if (mContext instanceof CaptureVitalsActivity) {
                    ((CaptureVitalsActivity) mContext).dismissProgressDialog(true, R.string.start_visit_successful,
                            R.string.start_visit_error);
                }
            }
        }

        ) {
        };

        queue.add(jsObjRequest);
    }

    public void getVisitType() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitTypeURL = mOpenMRS.getServerUrl() + API.REST_ENDPOINT + API.VISIT_TYPE;
        mLogger.d("Sending request to : " + visitTypeURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET, visitTypeURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                mLogger.d(response.toString());

                try {
                    JSONArray visitTypesObj = response.getJSONArray("results");
                    String visitTypeUUID = ((JSONObject) visitTypesObj.get(0)).getString("uuid");
                    OpenMRS.getInstance().setVisitTypeUUID(visitTypeUUID);
                } catch (JSONException e) {
                    mLogger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });

        queue.add(jsObjRequest);
    }

    public void subtractExpectedResponses(boolean errorOccurred) {
        mExpectedResponses--;
        if (errorOccurred) {
            mErrorOccurred = errorOccurred;
        }
        if (mExpectedResponses <= 0) {
            if (mContext instanceof PatientDashboardActivity) {
                ((PatientDashboardActivity) OpenMRS.getInstance().getCurrentActivity()).updatePatientVisitsData(mErrorOccurred);
            } else if (mContext instanceof FindPatientsSearchActivity
                    || mContext instanceof FindPatientsActivity) {
                ((ACBaseActivity) mContext).dismissProgressDialog(errorOccurred,
                        R.string.find_patients_row_toast_patient_saved,
                        R.string.find_patients_row_toast_patient_save_error);
            }
        }
    }
}
