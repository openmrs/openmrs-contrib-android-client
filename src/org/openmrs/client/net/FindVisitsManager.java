package org.openmrs.client.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.mappers.VisitMapper;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class FindVisitsManager extends BaseManager {
    private static final String VISIT_QUERY = "visit?patient=";

    public FindVisitsManager(Context context) {
        super(context);
    }

    public void findActiveVisitsForPatientByUUID(final Patient patient) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + VISIT_QUERY + patient.getUuid();
        logger.d("Sending request to : " + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    JSONArray visitResultJSON = response.getJSONArray(RESULTS_KEY);
                    if (visitResultJSON.length() > 0) {
                        for (int i = 0; i < visitResultJSON.length(); i++) {
                            findVisitByUUID(visitResultJSON.getJSONObject(i).getString(UUID_KEY), patient.getId());
                        }
                    }
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }

    public void findVisitByUUID(String visitUUID, final long patientID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String visitURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.VISIT_DETAILS + visitUUID
                + API.FULL_VERSION;
        logger.d("Sending request to : " + visitURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                visitURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());

                try {
                    new VisitDAO().saveVisit(VisitMapper.map(response), patientID);
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext));
        queue.add(jsObjRequest);
    }

}
