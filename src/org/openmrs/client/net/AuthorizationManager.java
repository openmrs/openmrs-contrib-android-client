package org.openmrs.client.net;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class AuthorizationManager extends BaseManager {

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String AUTHENTICATION_KEY = "authenticated";

    public AuthorizationManager(Context context) {
        super(context);
    }

    public void login(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        encodeAuthorizationToken(username, password);
        String loginURL = mOpenMRS.getServerUrl() + API.COMMON_PART + API.AUTHORISATION_END_POINT;

        logger.i("Sending request to : " + loginURL);

        JsonObjectRequestWrapper jsObjRequest = new JsonObjectRequestWrapper(Request.Method.GET,
                loginURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                logger.d(response.toString());
                try {
                    String sessionToken = response.getString(SESSION_ID_KEY);
                    Boolean isAuthenticated = Boolean.parseBoolean(response.getString(AUTHENTICATION_KEY));

                    if (isAuthenticated) {
                        mOpenMRS.setSessionToken(sessionToken);
                        mOpenMRS.setUsername(username);
                        ((LoginActivity) mContext).finish();
                    } else {
                        mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST));
                    }
                } catch (JSONException e) {
                    logger.d(e.toString());
                }
            }
        }
                , new GeneralErrorListenerImpl(mContext)) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(ApplicationConstants.AUTHORIZATION_PARAM, mOpenMRS.getAuthorizationToken());
                return params;
            }
        };
        queue.add(jsObjRequest);
    }

    public boolean isUserLoggedIn() {
        return !ApplicationConstants.EMPTY_STRING.equals(mOpenMRS.getSessionToken());
    }

    public void moveToLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
