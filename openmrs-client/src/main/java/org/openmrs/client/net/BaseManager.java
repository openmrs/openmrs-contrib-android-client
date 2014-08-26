package org.openmrs.client.net;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.UnsupportedEncodingException;

public class BaseManager {

    protected boolean isConnectionTimeout(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECTION_TIMEOUT);
    }

    protected boolean isAuthorizationFailure(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.AUTHORISATION_FAILURE);
    }

    protected boolean isNoInternetConnection(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.UNKNOWN_HOST));
    }

    protected boolean isServerUnavailable(String errorMessage) {
        return (errorMessage.contains(ApplicationConstants.VolleyErrors.NO_CONNECTION)
                && errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECT_EXCEPTION));
    }

    protected static void encodeAuthorizationToken(String username, String password) {
        String auth = null;
        try {
            auth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
        OpenMRS.getInstance().setAuthorizationToken(auth);
    }

    protected class GeneralErrorListener implements Response.ErrorListener {
        private Context mContext;

        protected GeneralErrorListener(Context context) {
            this.mContext = context;
        }

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
    }
}
