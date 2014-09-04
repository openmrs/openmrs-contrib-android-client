package org.openmrs.client.activities;


import android.content.Intent;
import android.os.Bundle;

import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST;

public class DialogActivity extends ACBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (ACTION_AUTH_FAILED_BROADCAST.equals(intent.getAction())) {
            showAuthenticationFailedDialog();
        }
        if (ACTION_CONN_TIMEOUT_BROADCAST.equals(intent.getAction())) {
            showConnectionTimeoutDialog();
        }
        if (ACTION_NO_INTERNET_CONNECTION_BROADCAST.equals(intent.getAction())) {
            showNoInternetConnectionDialog();
        }
        if (ACTION_SERVER_UNAVAILABLE_BROADCAST.equals(intent.getAction())) {
            showServerUnavailableDialog();
        }
        if (ACTION_UNAUTHORIZED_BROADCAST.equals(intent.getAction())) {
            showUnauthorizedDialog();
        }
    }
}
