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
import org.openmrs.client.R;
import org.openmrs.client.activities.ACBaseActivity;
import org.openmrs.client.activities.FindPatientsActivity;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.activities.VisitDashboardActivity;
import org.openmrs.client.dao.ObservationDAO;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Observation;
import org.openmrs.client.activities.fragments.PatientVisitsFragment;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.dao.LocationDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;
import org.openmrs.client.models.mappers.ObservationMapper;
import org.openmrs.client.models.mappers.VisitMapper;
import org.openmrs.client.utilities.DateUtils;

import java.util.HashMap;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class VisitsManager extends BaseManager {
    private static final String VISIT_QUERY = "visit?patient=";
    private static final String SENDING_REQUEST = "Sending request to : ";

    private int mExpectedResponses;
    private boolean mErrorOccurred;

    public VisitsManager(Context context) {
        super(context);
    }

    public void findVisitsByPatientUUID(final String patientUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + VISIT_QUERY + patientUUID;
        logger.d(SENDING_REQUEST + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray visitResultJSON = response.getJSONArray(RESULTS_KEY);
                    mExpectedResponses = visitResultJSON.length();
                    if (visitResultJSON.length() > 0) {
                        for (int i = 0; i < visitResultJSON.length(); i++) {
                            findVisitByUUID(visitResultJSON.getJSONObject(i).getString(UUID_KEY), patientID);
                        }
                    }
                } catch (JSONException e) {
                    subtractExpectedResponses(true);
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                subtractExpectedResponses(true);
                super.onErrorResponse(error);
            }
        });
        queue.add(jsObjRequest);
    }

    public void findVisitByUUID(final String visitUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.VISIT_DETAILS + visitUUID
                + API.FULL_VERSION;
        logger.d(SENDING_REQUEST + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                try {
                    final Visit visit = VisitMapper.map(response);

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

                    for (Encounter encounter : visit.getEncounters()) {
                        if (Encounter.EncounterType.VISIT_NOTE.equals(encounter.getEncounterType())) {
                            for (Observation obs : encounter.getObservations()) {
                                getVisitDiagnosesByUUID(obs.getUuid(), patientID);
                                mExpectedResponses++;
                            }
                        }
                    }
                } catch (JSONException e) {
                    subtractExpectedResponses(true);
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

    public void getVisitDiagnosesByUUID(final String diagnosesUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String diagnoseURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.OBS_DETAILS + diagnosesUUID;
        logger.d(SENDING_REQUEST + diagnoseURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                diagnoseURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                try {
                    Observation observation = ObservationMapper.diagnosisMap(response);
                    Observation obsInDB = new ObservationDAO().getObservationByUUID(observation.getUuid());
                    new ObservationDAO().updateObservation(obsInDB.getId(), observation, obsInDB.getEncounterID());

                    subtractExpectedResponses(false);
                } catch (JSONException e) {
                    logger.d(e.toString());
                    subtractExpectedResponses(true);
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

    public void inactivateVisitByUUID(final String visitUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.VISIT_DETAILS + visitUUID;
        logger.d(SENDING_REQUEST + visitURL);

        final String currentDate = DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stopDatetime", currentDate);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.POST, visitURL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Visit visit = VisitMapper.map(response);
                            long visitId = new VisitDAO().getVisitsIDByUUID(visit.getUuid());
                            new VisitDAO().updateVisit(visit, visitId, patientID);
                            ((VisitDashboardActivity) mContext).moveToPatientDashboard();
                        } catch (JSONException e) {
                            logger.d(e.toString());
                        }
                    }
                };
                thread.start();
            }
        }
                , new GeneralErrorListenerImpl(mContext) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        }

        );

        queue.add(jsObjRequest);
    }

    public void createVisit(final Patient patient) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.VISIT_DETAILS;
        logger.d("Sending request to : " + visitURL);

        final String currentDate = DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("patient", patient.getUuid());
        params.put("visitType", OpenMRS.getInstance().getVisitTypeUUID());
        params.put("startDatetime", currentDate);
        params.put("location", LocationDAO.findLocationByName(OpenMRS.getInstance().getLocation()).getParentLocationUuid());

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.POST, visitURL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                try {
                    Visit visit = VisitMapper.map(response);
                    long visitID = new VisitDAO().saveVisit(visit, patient.getId());
                    FragmentManager fm = ((PatientDashboardActivity) mContext).getSupportFragmentManager();
                    PatientVisitsFragment fragment = (PatientVisitsFragment) fm
                            .getFragments().get(PatientDashboardActivity.TabHost.VISITS_TAB_POS);
                    fragment.visitStarted(visitID, visitID <= 0);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ((PatientDashboardActivity) mContext).stopLoader(true);
            }
        }

        ) {
        };

        queue.add(jsObjRequest);
    }

    public void getVisitType() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitTypeURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.VISIT_TYPE;
        logger.d("Sending request to : " + visitTypeURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET, visitTypeURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray visitTypesObj = response.getJSONArray("results");
                    String visitTypeUUID = ((JSONObject) visitTypesObj.get(0)).getString("uuid");
                    OpenMRS.getInstance().setVisitTypeUUID(visitTypeUUID);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        }

        ) {
        };

        queue.add(jsObjRequest);
    }

    public void subtractExpectedResponses(boolean errorOccurred) {
        mExpectedResponses--;
        if (errorOccurred) {
            mErrorOccurred = errorOccurred;
        }
        if (mExpectedResponses == 0) {
            if (mContext instanceof PatientDashboardActivity) {
                ((PatientDashboardActivity) mContext).updatePatientVisitsData(mErrorOccurred);
            } else if (mContext instanceof FindPatientsSearchActivity
                    || mContext instanceof FindPatientsActivity) {
                ((ACBaseActivity) mContext).dismissProgressDialog(errorOccurred,
                        R.string.find_patients_row_toast_patient_saved,
                        R.string.find_patients_row_toast_patient_save_error);
            }
        }
    }
}
