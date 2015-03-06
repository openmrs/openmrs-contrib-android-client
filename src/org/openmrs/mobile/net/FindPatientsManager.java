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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.openmrs.mobile.activities.listeners.FindPatientListener;
import org.openmrs.mobile.activities.listeners.FullPatientDataListener;
import org.openmrs.mobile.activities.listeners.LastViewedPatientListener;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import java.io.File;
import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {
    private static final String PATIENT_LAST_VIEWED_QUERY = String.format("patient?lastviewed=%s", API.FULL_VERSION_NEXT_PARAM);
    private static final String BASE_PATIENT_MANAGER_URL = OpenMRS.getInstance().getServerUrl() + API.REST_ENDPOINT;
    private static final String BASE_FINDPATIENTURL = BASE_PATIENT_MANAGER_URL + API.PATIENT_QUERY;
    private static final String BASE_GETFULLPATIENTDATA = BASE_PATIENT_MANAGER_URL + API.PATIENT_DETAILS + File.separator;
    private static final String SENDING_REQUEST = "Sending request to : ";
    private static String sFindPatientURL = BASE_FINDPATIENTURL;
    private static final String LAST_VIEWED_PATIENT_URL = BASE_PATIENT_MANAGER_URL + PATIENT_LAST_VIEWED_QUERY;
    private static String sFullPatientDataURL = BASE_GETFULLPATIENTDATA;

    public void findPatient(FindPatientListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        sFindPatientURL = BASE_FINDPATIENTURL + listener.getLastQuery() + API.FULL_VERSION_NEXT_PARAM;
        mLogger.d(SENDING_REQUEST + sFindPatientURL);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sFindPatientURL, null, listener, listener);
        queue.add(jsObjRequest);
    }

    public void getLastViewedPatient(LastViewedPatientListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        mLogger.d(SENDING_REQUEST + LAST_VIEWED_PATIENT_URL);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                LAST_VIEWED_PATIENT_URL, null, listener, listener);
        queue.add(jsObjRequest);
    }

    public void getFullPatientData(FullPatientDataListener listener) {
        RequestQueue queue = Volley.newRequestQueue(getCurrentContext());
        sFullPatientDataURL = BASE_GETFULLPATIENTDATA + listener.getPatientUUID() + API.FULL_VERSION;
        mLogger.d(SENDING_REQUEST + sFullPatientDataURL);
        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sFullPatientDataURL, null, listener, listener);
        queue.add(jsObjRequest);
        queue.start();
    }
}
