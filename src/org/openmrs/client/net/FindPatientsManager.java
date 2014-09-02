package org.openmrs.client.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.mappers.PatientMapper;
import org.openmrs.client.utilities.PatientCacheHelper;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {
    private static final String RESULTS_KEY = "results";
    private static final String UUID_KEY = "uuid";
    private static final String PATIENT_QUERY = "patient?q=";

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    public FindPatientsManager(Context context) {
        this.mContext = context;
    }

    public void findPatient(final String query) {
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

                    for (int i = 0; i < patientsJSONList.length(); i++) {
                        getFullPatientData(patientsJSONList.getJSONObject(i).getString(UUID_KEY));
                    }

                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }

    public void getFullPatientData(final String patientUUID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.PATIENT_DETAILS
                + patientUUID + API.FULL_VERSION;
        logger.d("Sending request to : " + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                PatientCacheHelper.addPatient(PatientMapper.map(response));
                ((FindPatientsSearchActivity) mContext).updatePatientsData();
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
        queue.start();
    }
}
