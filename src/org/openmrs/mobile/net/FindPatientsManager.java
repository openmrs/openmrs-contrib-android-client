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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.openmrs.mobile.activities.FindPatientsActivity;
import org.openmrs.mobile.activities.FindPatientsSearchActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.volley.wrappers.JsonObjectRequestWrapper;
import java.io.File;
import java.util.ArrayList;


import static org.openmrs.mobile.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {

    private static final String PATIENT_LAST_VIEWED_QUERY = String.format("patient?lastviewed=%s", API.FULL_VERSION_NEXT_PARAM);
    private static final String BASE_PATIENT_MANAGER_URL = OpenMRS.getInstance().getServerUrl() + API.REST_ENDPOINT;
    private static final String BASE_FINDPATIENTURL = BASE_PATIENT_MANAGER_URL + API.PATIENT_QUERY;
    private static final String BASE_GETFULLPATIENTDATA = BASE_PATIENT_MANAGER_URL + API.PATIENT_DETAILS + File.separator;
    private static final String SENDING_REQUEST = "Sending request to : ";

    private static String sFindPatientURL = BASE_FINDPATIENTURL;
    private static String sLastViewedPatientURL = BASE_PATIENT_MANAGER_URL + PATIENT_LAST_VIEWED_QUERY;
    private static String sFullPatientDataURL = BASE_GETFULLPATIENTDATA;

    public FindPatientsManager(Context context) {
        super(context);
    }

    public void findPatient(final String query, FindPatientsSearchActivity.FindPatientsResponseListener findPatientRespListn) {
        final FindPatientsSearchActivity findPatientsSearchContext = findPatientRespListn.getFindPatientsWeakRef().get();
        final int searchId = findPatientRespListn.getSearchId();

        RequestQueue queue = Volley.newRequestQueue(findPatientsSearchContext);
        sFindPatientURL = BASE_FINDPATIENTURL + query + API.FULL_VERSION_NEXT_PARAM;
        logger.d(SENDING_REQUEST + sFindPatientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sFindPatientURL, null, findPatientRespListn,
                new GeneralErrorListenerImpl(findPatientsSearchContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        findPatientsSearchContext.stopLoader(searchId);
                    }
                }
        );
        queue.add(jsObjRequest);
    }

    public void getLastViewedPatient(final FindPatientsActivity.FindPatientsResponseListener lastViewedPatientRespList) {
        final FindPatientsActivity findPatientsContext = lastViewedPatientRespList.getFindPatientsWeakRef().get();
        RequestQueue queue = Volley.newRequestQueue(findPatientsContext);
        logger.d(SENDING_REQUEST + sLastViewedPatientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sLastViewedPatientURL, null, lastViewedPatientRespList,
                new GeneralErrorListenerImpl(findPatientsContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        FragmentManager fm = findPatientsContext.getSupportFragmentManager();
                        FindPatientLastViewedFragment fragment = (FindPatientLastViewedFragment) fm
                                .getFragments().get(FindPatientsActivity.TabHost.LAST_VIEWED_TAB_POS);

                        if (fragment != null) {
                            fragment.stopLoader();
                        }

                        FindPatientLastViewedFragment.setLastViewedPatientList(new ArrayList<Patient>());
                        FindPatientLastViewedFragment.setRefreshing(false);
                    }
                }
        );
        queue.add(jsObjRequest);
    }

    public void getFullPatientData(final String patientUUID, PatientDashboardActivity.PatientDashboardResponseListener fullPatientDataRespList) {
        final PatientDashboardActivity patientDashboardContext = fullPatientDataRespList.getPatientDashboardWeakRef().get();
        RequestQueue queue = Volley.newRequestQueue(patientDashboardContext);
        sFullPatientDataURL = BASE_GETFULLPATIENTDATA + patientUUID + API.FULL_VERSION;
        logger.d(SENDING_REQUEST + sFullPatientDataURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                sFullPatientDataURL, null, fullPatientDataRespList,
                new GeneralErrorListenerImpl(patientDashboardContext) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);

                        patientDashboardContext.stopLoader(true);
                    }
                }
        );
        queue.add(jsObjRequest);
        queue.start();
    }
}
