package org.openmrs.client.net;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.Patient;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {

    private static final String RESULTS_KEY = "results";
    private static final String UUID_KEY = "uuid";
    private static final String DISPLAY_KEY = "display";

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();
    private ArrayList<Patient> patientsList;

    public FindPatientsManager(Context context) {
        this.mContext = context;
    }

    public void findPatient(final String query) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientsURL = mOpenMRS.getServerUrl() + API.COMMON_PART + "patient?q=" + query;
        logger.d("Trying connect witch: " + patientsURL);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                patientsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray patientsJSONList = response.getJSONArray(RESULTS_KEY);

                    patientsList = new ArrayList<Patient>();

                    for (int i = 0; i < patientsJSONList.length(); i++) {
                        JSONObject patientJSON = patientsJSONList.getJSONObject(i);

                        PatientManager patientManager = new PatientManager(mContext);
                        Patient patient = new Patient();
                        patient.setUuid(patientJSON.getString(UUID_KEY));
                        patient.setDisplay(patientJSON.getString(DISPLAY_KEY));
                        patientManager.getPatientData(patient);
                        patientsList.add(patient);
                    }

                    ((FindPatientsSearchActivity) mContext).setPatientsList(patientsList);

                } catch (JSONException e) {
                    logger.d(e.toString());
                }

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isConnectionTimeout(error.toString())) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST));
                } else if (isAuthorizationFailure(error.toString())) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST));
                } else if (isServerUnavailable(error.toString())) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST));
                } else if (isNoInternetConnection(error.toString())) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST));
                } else {
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mOpenMRS.getAuthorisation());
                return params;
            }
        };
        queue.add(jsObjRequest);
    }
}
