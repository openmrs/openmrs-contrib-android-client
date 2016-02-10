/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.utilities.ApplicationConstants;

import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_ERROR_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_NOT_SUPPORTED_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_SOCKET_EXCEPTION_BROADCAST;
import static org.openmrs.mobile.utilities.ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST;

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
        if (ACTION_SERVER_NOT_SUPPORTED_BROADCAST.equals(intent.getAction())) {
            showServerNotSupportedErrorDialog();
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

    private void showServerNotSupportedErrorDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.server_not_supported_dialog_title));
        bundle.setTextViewMessage(getString(R.string.server_not_supported_dialog_message));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.SERVER_NOT_SUPPORTED_DIALOG_TAG);
    }
}
