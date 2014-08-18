package org.openmrs.client.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.net.AuthorizationManager;
import org.openmrs.client.utilities.ApplicationConstants;

import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST;

public class LoginActivity extends ACBaseActivity {

    private AuthorizationManager mAuthorizationManager;

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;

    private String mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view_layout);

        mUsername = (EditText) findViewById(R.id.loginUsernameField);
        mPassword = (EditText) findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginFields()) {
                    showURLDialog();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.login_dialog_login_or_password_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerReceiver(mAuthFailedReceiver, new IntentFilter(ACTION_AUTH_FAILED_BROADCAST));
        registerReceiver(mConnectionTimeoutReceiver, new IntentFilter(ACTION_CONN_TIMEOUT_BROADCAST));
        registerReceiver(mNoInternetConnectionReceiver, new IntentFilter(ACTION_NO_INTERNET_CONNECTION_BROADCAST));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuthorizationManager = new AuthorizationManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAuthFailedReceiver);
        unregisterReceiver(mConnectionTimeoutReceiver);
        unregisterReceiver(mNoInternetConnectionReceiver);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private boolean validateLoginFields() {
        return !(ApplicationConstants.EMPTY_STRING.equals(mUsername.getText().toString())
                || ApplicationConstants.EMPTY_STRING.equals(mPassword.getText().toString()));
    }

    private void showURLDialog() {
        mURL = OpenMRS.getInstance().getServerUrl();
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.login_dialog_title));
        bundle.setEditTextViewMessage(mURL);
        bundle.setLeftButtonText(getString(R.string.dialog_button_done));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    private void showConnectionTimeoutDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.conn_timeout_dialog_title));
        bundle.setTextViewMessage(getString(R.string.conn_timeout_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.RETRY);
        bundle.setLeftButtonText(getString(R.string.dialog_button_retry));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.CONN_TIMEOUT_DIALOG_TAG);
    }

    private void showAuthenticationFailedDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.auth_failed_dialog_title));
        bundle.setTextViewMessage(getString(R.string.auth_failed_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.AUTH_FAILED_DIALOG_TAG);
    }

    private void showNoInternetConnectionDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.no_internet_conn_dialog_title));
        bundle.setTextViewMessage(getString(R.string.no_internet_conn_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.RETRY);
        bundle.setLeftButtonText(getString(R.string.dialog_button_retry));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.NO_INTERNET_CONN_DIALOG_TAG);
    }

    private void showLoadingDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setLoadingBar(true);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOADING_DIALOG_TAG);
    }

    public void login() {
        mAuthorizationManager.login(mUsername.getText().toString(), mPassword.getText().toString());
        showLoadingDialog();
    }

    private BroadcastReceiver mAuthFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoadingDialog();
            if (ACTION_AUTH_FAILED_BROADCAST.equals(intent.getAction())) {
                showAuthenticationFailedDialog();
            }
        }
    };

    private BroadcastReceiver mConnectionTimeoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoadingDialog();
            if (ACTION_CONN_TIMEOUT_BROADCAST.equals(intent.getAction())) {
                showConnectionTimeoutDialog();
            }
        }
    };

    private BroadcastReceiver mNoInternetConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoadingDialog();
            if (ACTION_NO_INTERNET_CONNECTION_BROADCAST.equals(intent.getAction())) {
                showNoInternetConnectionDialog();
            }
        }
    };

    private void dismissLoadingDialog() {
        if (null != getCurrentDialog()) {
            getCurrentDialog().dismiss();
        }
    }
}
