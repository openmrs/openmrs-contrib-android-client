package org.openmrs.client.net;

import org.openmrs.client.utilities.ApplicationConstants;

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
}
