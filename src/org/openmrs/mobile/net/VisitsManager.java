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
import org.openmrs.mobile.listeners.visit.FindVisitsByPatientUUIDListener;
import org.openmrs.mobile.listeners.visit.StartVisitListener;
import org.openmrs.mobile.listeners.visit.FindVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.EndVisitByUUIDListener;
import org.openmrs.mobile.listeners.visit.LastVitalsListener;
import org.openmrs.mobile.listeners.visit.VisitTypeListener;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.io.File;
import java.util.HashMap;

import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class VisitsManager extends BaseManager {
    private static final String VISIT_BASE_URL = getBaseRestURL() + API.VISIT_DETAILS;
    private static final String VISITS_BY_PATIENT_UUID_BASE_URL = getBaseRestURL() + "visit?patient=";
    private static final String VISITS_BY_PATIENT_UUID_END_URL = "&v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";
    private static final String LAST_VITALS_BASE_URL =  getBaseRestURL() + API.ENCOUNTER_DETAILS + "?patient=";
    private static final String LAST_VITALS_END_URL = "&encounterType=" + ApplicationConstants.EncounterTypes.VITALS + "&v=custom:(obs:full)&limit=1&order=desc";
    private static final String VISITS_BY_UUID_BASE_URL = VISIT_BASE_URL + File.separator;
    private static final String VISIT_BY_UUID_END_URL = "?v=custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";
    private static final String VISIT_TYPE_BASE_URL = getBaseRestURL() + API.VISIT_TYPE;
    public static final String START_DATE_TIME = "startDatetime";
    public static final String STOP_DATE_TIME = "stopDatetime";
    public static final String VISIT_TYPE = "visitType";
    public static final String LOCATION = "location";
    public static final String PATIENT = "patient";


    public void getLastVitals(LastVitalsListener listener) {
        String url = LAST_VITALS_BASE_URL + listener.getPatientUUID() + LAST_VITALS_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void findVisitsByPatientUUID(FindVisitsByPatientUUIDListener listener) {
        String url = VISITS_BY_PATIENT_UUID_BASE_URL + listener.getPatientUUID() + VISITS_BY_PATIENT_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void findVisitByUUID(FindVisitByUUIDListener listener) {
        String url = VISITS_BY_UUID_BASE_URL + listener.getPatientUUID() + VISIT_BY_UUID_END_URL;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void endVisitByUUID(EndVisitByUUIDListener listener) {
        String url = VISITS_BY_UUID_BASE_URL + listener.getVisitUUID();
        mLogger.d(SENDING_REQUEST + url);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(STOP_DATE_TIME, DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.POST,
                        url, new JSONObject(params), listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void startVisit(StartVisitListener listener) {
        mLogger.d(SENDING_REQUEST + VISIT_BASE_URL);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PATIENT, listener.getPatientUUID());
        params.put(VISIT_TYPE, OpenMRS.getInstance().getVisitTypeUUID());
        params.put(START_DATE_TIME, DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
        params.put(LOCATION, LocationDAO.findLocationByName(OpenMRS.getInstance().getLocation()).getParentLocationUuid());

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.POST,
                        VISIT_BASE_URL, new JSONObject(params), listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }


    public void getVisitType(VisitTypeListener listener) {
        mLogger.d(SENDING_REQUEST + VISIT_TYPE_BASE_URL);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        VISIT_TYPE_BASE_URL, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }
}
