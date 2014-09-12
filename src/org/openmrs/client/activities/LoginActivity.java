package org.openmrs.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.net.AuthorizationManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.FontsUtil;
import org.openmrs.client.utilities.ImageUtils;
import org.openmrs.client.utilities.ToastUtil;
import org.openmrs.client.utilities.URLValidator;

public class LoginActivity extends ACBaseActivity {

    private AuthorizationManager mAuthorizationManager;

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressBar mSpinner;
    private LinearLayout mLoginFormView;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view_layout);

        mUsername = (EditText) findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mPassword = (EditText) findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginFields()) {
                    showURLDialog();
                } else {
                    ToastUtil.showShortToast(getApplicationContext(),
                            ToastUtil.ToastType.ERROR,
                            R.string.login_dialog_login_or_password_empty);
                }
            }
        });
        mSpinner = (ProgressBar) findViewById(R.id.loginLoading);
        mLoginFormView = (LinearLayout) findViewById(R.id.loginFormView);
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindDrawableResources();
        mAuthorizationManager = new AuthorizationManager(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private boolean validateLoginFields() {
        return !(ApplicationConstants.EMPTY_STRING.equals(mUsername.getText().toString())
                || ApplicationConstants.EMPTY_STRING.equals(mPassword.getText().toString()));
    }

    private void showURLDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.login_dialog_title));
        bundle.setEditTextViewMessage(OpenMRS.getInstance().getServerUrl());
        bundle.setRightButtonText(getString(R.string.dialog_button_done));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    private void showInvalidURLDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.invalid_url_dialog_title));
        bundle.setTextViewMessage(getString(R.string.invalid_url_dialog_message));
        bundle.setLeftButtonText(getString(R.string.dialog_button_ok));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG);
    }

    private void login() {
        mLoginFormView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mAuthorizationManager.login(mUsername.getText().toString(), mPassword.getText().toString());
    }

    public void login(boolean validateURL) {
        if (!validateURL) {
            login();
        } else {
            URLValidator.ValidationResult result = URLValidator.validate(OpenMRS.getInstance().getServerUrl());
            if (result.isURLValid()) {
                OpenMRS.getInstance().setServerUrl(result.getUrl());
                login();
            } else {
                showInvalidURLDialog();
            }
        }
    }

    private void bindDrawableResources() {
        ImageView openMrsLogoImage = (ImageView) findViewById(R.id.openmrsLogo);
        if (mBitmap == null) {
            mBitmap = ImageUtils.decodeBitmapFromResource(
                    getResources(),
                    R.drawable.openmrs_logo,
                    openMrsLogoImage.getLayoutParams().width,
                    openMrsLogoImage.getLayoutParams().height);
        }
        openMrsLogoImage.setImageBitmap(mBitmap);
    }

    private void unbindDrawableResources() {
        if (null != mBitmap) {
            mBitmap.recycle();
        }
    }
}
