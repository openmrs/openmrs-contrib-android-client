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

import org.json.JSONObject;
import org.openmrs.client.activities.FindPatientsSearchActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.Patient;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class PatientManager extends BaseManager {

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    public PatientManager(Context context) {
        this.mContext = context;
    }

    public void getPatientData(final Patient patient) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + API.COMMON_PART + "patient/"
                                                    + patient.getUuid() + "?v=full";
        logger.d("Trying connect witch: " + patientURL);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                patient.patientMapper(response);
                ((FindPatientsSearchActivity) mContext).updatePatientsData();
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
