package org.openmrs.client.activities;

import android.content.Intent;
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

        mURL = OpenMRS.getInstance().getServerUrl();

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuthorizationManager = new AuthorizationManager(this);
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
        CustomDialogBundle bundle = new CustomDialogBundle(getApplicationContext());
        bundle.setTitleViewMessage(getString(R.string.login_dialog_title));
        bundle.setEditTextViewMessage(mURL);
        bundle.setLeftButtonText(getString(R.string.login_dialog_button_done));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setRightButtonText(getString(R.string.login_dialog_button_cancel));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    public void login() {
            mAuthorizationManager.login(mUsername.getText().toString(), mPassword.getText().toString());
    }

}
