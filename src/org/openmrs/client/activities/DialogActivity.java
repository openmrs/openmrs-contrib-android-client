package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.utilities.ApplicationConstants;

import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_SOCKET_EXCEPTION_BROADCAST;
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
        if (ACTION_SERVER_ERROR_BROADCAST.equals(intent.getAction())) {
            showServerErrorDialog();
        }
        if (ACTION_SOCKET_EXCEPTION_BROADCAST.equals(intent.getAction())) {
            showSocketExceptionErrorDialog();
        }
        if (ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG.equals(intent.getAction())) {
            showInvalidURLDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ACTION_UNAUTHORIZED_BROADCAST.equals(getIntent().getAction())) {
            moveUnauthorizedUserToLoginScreen();
        }
    }

    private void showInvalidURLDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.invalid_url_dialog_title));
        bundle.setTextViewMessage(getString(R.string.invalid_url_dialog_message));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG);
    }
}
