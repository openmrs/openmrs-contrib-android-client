package org.openmrs.client.net;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.ToastUtil;

import java.io.UnsupportedEncodingException;

public class BaseManager {
    protected static final String RESULTS_KEY = "results";
    protected static final String UUID_KEY = "uuid";

    protected Context mContext;
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    public BaseManager(Context context) {
        this.mContext = context;
    }

    protected boolean isConnectionTimeout(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.CONNECTION_TIMEOUT);
    }

    protected boolean isUserUnauthorized(String errorMessage) {
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

    protected boolean isServerError(String errorMessage) {
        return errorMessage.contains(ApplicationConstants.VolleyErrors.SERVER_ERROR);
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

    private abstract class GeneralErrorListener implements Response.ErrorListener {
        private Context mContext;

        protected GeneralErrorListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (isConnectionTimeout(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST));
            } else if (isUserUnauthorized(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST));
            } else if (isServerUnavailable(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST));
            } else if (isNoInternetConnection(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST));
            } else if (isServerError(error.toString())) {
                mContext.sendBroadcast(new Intent(ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST));
            } else {
                ToastUtil.showShortToast(mContext, ToastUtil.ToastType.ERROR, error.toString());
                logger.e(error.toString());
            }
        }
    }

    public class GeneralErrorListenerImpl extends GeneralErrorListener {

        public GeneralErrorListenerImpl(Context context) {
            super(context);
        }
    }
}
