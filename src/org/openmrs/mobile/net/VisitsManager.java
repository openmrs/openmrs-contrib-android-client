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

import com.android.volley.Request;
import org.json.JSONObject;

import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.visit.CheckVisitBeforeStartListener;
import org.openmrs.mobile.listeners.visit.FindVisitByUUIDAfterOfflineCaptureVitalsListener;
import org.openmrs.mobile.listeners.visit.FindVisitsByPatientUUIDListener;
import org.openmrs.mobile.listeners.visit.StartVisitListener;
import org.openmrs.mobile.listeners.visit.FindVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.EndVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.LastVitalsListener;
import org.openmrs.mobile.listeners.visit.VisitTypeListener;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.models.OfflineRequest;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.io.File;

import java.util.HashMap;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class VisitsManager extends BaseManager {
    private String mVisitBaseUrl = getBaseRestURL() + API.VISIT_DETAILS;
    private String mVisitsByPatientUuidBaseUrl = getBaseRestURL() + "visit?patient=";
    private static final String VISITS_BY_PATIENT_UUID_END_URL = "&v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";
    private String mLastVitalsBaseUrl =  getBaseRestURL() + API.ENCOUNTER_DETAILS + "?patient=";
    private static final String LAST_VITALS_END_URL = "&encounterType=" + ApplicationConstants.EncounterTypes.VITALS + "&v=custom:(obs:full)&limit=1&order=desc";
    private String mVisitsByUuidBaseUrl = mVisitBaseUrl + File.separator;
    private static final String VISIT_BY_UUID_END_URL = "?v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";
    private String mVisitTypeBaseUrl = getBaseRestURL() + API.VISIT_TYPE;
    public static final String START_DATE_TIME = "startDatetime";
    public static final String STOP_DATE_TIME = "stopDatetime";
    public static final String VISIT_TYPE = "visitType";
    public static final String LOCATION = "location";
    public static final String PATIENT = "patient";


    public void getLastVitals(LastVitalsListener listener) {
        String patientUUID = new PatientDAO().findPatientByID(listener.getPatientID()).getUuid();
        String url = mLastVitalsBaseUrl + patientUUID + LAST_VITALS_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void findVisitsByPatientUUID(FindVisitsByPatientUUIDListener listener) {
        String url = mVisitsByPatientUuidBaseUrl + listener.getPatientUUID() + VISITS_BY_PATIENT_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public  void checkVisitBeforeStart(CheckVisitBeforeStartListener listener) {
        String url = mVisitsByPatientUuidBaseUrl + listener.getPatientUUID() + VISITS_BY_PATIENT_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        if (mOnlineMode) {
        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
        } else {
            listener.offlineAction();
        }
    }

    public void findVisitByUUID(FindVisitByUUIDListener listener) {
        String url = mVisitsByUuidBaseUrl + listener.getVisitUUID() + VISIT_BY_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }


    public void findVisitByUUIDAfterCaptureVitals(FindVisitByUUIDAfterOfflineCaptureVitalsListener listener) {
        String url = mVisitsByUuidBaseUrl + listener.getVisitUUID() + VISIT_BY_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void endVisitByUUID(EndVisitByUUIDListener listener) {
        long currentTimeMillis = System.currentTimeMillis();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(STOP_DATE_TIME, DateUtils.convertTime(currentTimeMillis, DateUtils.OPEN_MRS_REQUEST_FORMAT));

        if (mOnlineMode) {
            String url = mVisitsByUuidBaseUrl + listener.getVisitUUID();
            mLogger.d(SENDING_REQUEST + url);

            JsonObjectRequestWrapper jsObjRequest =
                    new JsonObjectRequestWrapper(Request.Method.POST,
                            url, new JSONObject(params), listener, listener, DO_GZIP_REQUEST);
            mOpenMRS.addToRequestQueue(jsObjRequest);
        } else {
            listener.offlineAction(currentTimeMillis);
            OfflineRequest offlineRequest = new OfflineRequest(Request.Method.POST, new JSONObject(params), listener.getVisitID(), ApplicationConstants.OfflineRequests.INACTIVATE_VISIT);
            mOpenMRS.addToRequestQueue(offlineRequest);
        }
    }

    public void startVisit(StartVisitListener listener) {
        mLogger.d(SENDING_REQUEST + mVisitBaseUrl);
        long currentTimeMillis = System.currentTimeMillis();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PATIENT, listener.getPatientUUID());
        params.put(VISIT_TYPE, OpenMRS.getInstance().getVisitTypeUUID());
        params.put(START_DATE_TIME, DateUtils.convertTime(currentTimeMillis, DateUtils.OPEN_MRS_REQUEST_FORMAT));
        params.put(LOCATION, LocationDAO.findLocationByName(OpenMRS.getInstance().getLocation()).getParentLocationUuid());

        if (mOnlineMode) {
            JsonObjectRequestWrapper jsObjRequest =
                    new JsonObjectRequestWrapper(Request.Method.POST,
                            mVisitBaseUrl, new JSONObject(params), listener, listener, DO_GZIP_REQUEST);
            mOpenMRS.addToRequestQueue(jsObjRequest);
        } else {
            long visitID = listener.offlineAction(currentTimeMillis);

            if (visitID > 0) {
                OfflineRequest offlineRequest = new OfflineRequest(Request.Method.POST, mVisitBaseUrl, new JSONObject(params), visitID, ApplicationConstants.OfflineRequests.START_VISIT);
                OpenMRS.getInstance().addToRequestQueue(offlineRequest);
            }
        }
    }

    public void getVisitType(VisitTypeListener listener) {
        mLogger.d(SENDING_REQUEST + mVisitTypeBaseUrl);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        mVisitTypeBaseUrl, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }
}
