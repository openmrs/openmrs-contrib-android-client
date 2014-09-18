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
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.models.mappers.PatientMapper;
import org.openmrs.client.utilities.PatientCacheHelper;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {
    private static final String PATIENT_QUERY = "patient?q=";

    public FindPatientsManager(Context context) {
        super(context);
    }

    public void findPatient(final String query, final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.COMMON_PART + PATIENT_QUERY + query;
        logger.d("Sending request to : " + patientsURL);

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

    public void getFullPatientData(final String patientUUID, final int searchId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.PATIENT_DETAILS
                + patientUUID + API.FULL_VERSION;
        logger.d("Sending request to : " + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                if (PatientCacheHelper.getId() == searchId) {
                    PatientCacheHelper.addPatient(PatientMapper.map(response));
                }
                ((FindPatientsSearchActivity) mContext).updatePatientsData(searchId);
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
        queue.start();
    }
}
