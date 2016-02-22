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
import org.openmrs.mobile.listeners.findPatients.FindPatientListener;
import org.openmrs.mobile.listeners.findPatients.FullPatientDataListener;
import org.openmrs.mobile.listeners.findPatients.LastViewedPatientListener;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import java.io.File;
import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {
    private static final String PATIENT_LAST_VIEWED_QUERY = String.format("patient?lastviewed=%s", API.FULL_VERSION_NEXT_PARAM);
    private String mFullPatientDataBaseUrl = getBaseRestURL() + API.PATIENT_DETAILS + File.separator;
    private String mLastViewedPatientBaseUrl = getBaseRestURL() + PATIENT_LAST_VIEWED_QUERY;
    private String mFindPatientBaseUrl = getBaseRestURL() + API.PATIENT_QUERY;

    public void findPatient(FindPatientListener listener) {
        String url = mFindPatientBaseUrl + listener.getLastQuery() + API.FULL_VERSION_NEXT_PARAM;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void getLastViewedPatient(LastViewedPatientListener listener) {
        mLogger.d(SENDING_REQUEST + mLastViewedPatientBaseUrl);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        mLastViewedPatientBaseUrl, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }

    public void getFullPatientData(FullPatientDataListener listener) {
        String url = mFullPatientDataBaseUrl + listener.getPatientUUID() + API.FULL_VERSION;
        mLogger.d(SENDING_REQUEST + url);

        JsonObjectRequestWrapper jsObjRequest =
                new JsonObjectRequestWrapper(Request.Method.GET,
                        url, null, listener, listener, DO_GZIP_REQUEST);
        mOpenMRS.addToRequestQueue(jsObjRequest);
    }
}
