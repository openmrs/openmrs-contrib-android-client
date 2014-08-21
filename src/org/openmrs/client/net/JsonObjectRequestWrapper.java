package org.openmrs.client.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

/**
 @see com.android.volley.toolbox.JsonObjectRequest
 Wrapper class for JsonObjectRequest
 getHeaders method conatins Authorization Token
 **/
public class JsonObjectRequestWrapper extends JsonObjectRequest {
    public JsonObjectRequestWrapper(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequestWrapper(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ApplicationConstants.AUTHORIZATION_PARAM, OpenMRS.getInstance().getAuthorizationToken());
        return params;
    }
}
