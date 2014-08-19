package org.openmrs.client.net;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class AuthorizationManager {

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String AUTHENTICATION_KEY = "authenticated";

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    public AuthorizationManager(Context context) {
        this.mContext = context;
    }

    public void login(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String loginURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.AUTHORISATION_END_POINT;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                loginURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                try {
                    String sessionToken = response.getString(SESSION_ID_KEY);
                    Boolean isAuthenticated = Boolean.parseBoolean(response.getString(AUTHENTICATION_KEY));

                    if (isAuthenticated) {
                        mOpenMRS.setSessionToken(sessionToken);
                        ((LoginActivity) mContext).getCurrentDialog().dismiss();
                        ((LoginActivity) mContext).finish();
                    } else {
                        mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST));
                    }
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().contains(ApplicationConstants.VolleyErrors.CONNECTION_TIMEOUT)) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST));
                } else if (error.toString().contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)) {
                    mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST));
                } else {
                    ((LoginActivity) mContext).getCurrentDialog().dismiss();
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = null;
                try {
                    auth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes("UTF-8"), Base64.NO_WRAP);
                } catch (UnsupportedEncodingException e) {
                    logger.d(e.toString());
                }
                params.put("Authorization", auth);
                return params;
            }
        };
        queue.add(jsObjRequest);
    }

    public boolean isUserLoggedIn() {
        return !ApplicationConstants.EMPTY_STRING.equals(mOpenMRS.getSessionToken());
    }
}
