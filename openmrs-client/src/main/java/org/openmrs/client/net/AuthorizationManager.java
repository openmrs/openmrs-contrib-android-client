package org.openmrs.client.net;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
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
import org.openmrs.client.R;
import org.openmrs.client.activities.LoginActivity;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.client.utilities.ApplicationConstants.API;

public class AuthorizationManager {
    private static final String TAG = AuthorizationManager.class.getSimpleName();

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String AUTHENTICATION_KEY = "authenticated";

    private Context mContext;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();

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
                Log.d(TAG, response.toString());
                try {
                    String sessionToken = response.getString(SESSION_ID_KEY);
                    Boolean isAuthenticated = Boolean.parseBoolean(response.getString(AUTHENTICATION_KEY));

                    if (isAuthenticated) {
                        mOpenMRS.setSessionToken(sessionToken);
                        ((LoginActivity) mContext).finish();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.login_dialog_auth_failed), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //TODO update with new logger
                    Log.d(TAG, e.toString());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String sError = error.toString();
                Log.d(TAG, "request error: " + sError);
                sError = sError.substring(sError.lastIndexOf(':') + 1);
                Toast.makeText(mContext, sError.toString(), Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = null;
                try {
                    auth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes("UTF-8"), Base64.NO_WRAP);
                } catch (UnsupportedEncodingException e) {
                   //TODO add missing logs
                    Log.d(TAG, e.toString());
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
