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
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Person;
import org.openmrs.client.models.mappers.PatientMapper;

import java.util.ArrayList;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindPatientsManager extends BaseManager {
    private static final String RESULTS_KEY = "results";
    private static final String UUID_KEY = "uuid";
    private static final String DISPLAY_KEY = "display";
    private static final String PATIENT_QUERY = "patient?q=";

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();
    private ArrayList<Patient> patientsList;

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

                    patientsList = new ArrayList<Patient>();

                    for (int i = 0; i < patientsJSONList.length(); i++) {
                        JSONObject patientJSON = patientsJSONList.getJSONObject(i);
                        Patient patient = new Patient();
                        patient.setUuid(patientJSON.getString(UUID_KEY));
                        patient.setDisplay(patientJSON.getString(DISPLAY_KEY));
                        FindPatientsManager.this.getFullPatientData(patientJSON.getString(UUID_KEY));
                        patientsList.add(patient);
                    }

                    ((FindPatientsSearchActivity) mContext).setPatientsList(patientsList);

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
                Person person = PatientMapper.map(response);
                ((FindPatientsSearchActivity) mContext).updatePatientsData();
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }
}
