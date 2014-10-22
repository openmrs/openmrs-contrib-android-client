package org.openmrs.client.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.models.mappers.PatientMapper;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.PatientCacheHelper;

public class SynchronizePatientManager extends BaseManager {
    private static final String SENDING_REQUEST = "Sending request to : ";

    public SynchronizePatientManager(Context context) {
        super(context);
    }

    public void getFullPatientData(final String patientUUID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String patientURL = mOpenMRS.getServerUrl() + ApplicationConstants.API.COMMON_PART + ApplicationConstants.API.PATIENT_DETAILS
                + patientUUID + ApplicationConstants.API.FULL_VERSION;
        logger.d(SENDING_REQUEST + patientURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                patientURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                ((PatientDashboardActivity) mContext).updatePatientsData(PatientMapper.map(response));
            }
        }
                , new GeneralErrorListenerImpl(mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ((PatientDashboardActivity) mContext).stopLoader();
            }
        }
        );
        queue.add(jsObjRequest);
        queue.start();
    }
}

