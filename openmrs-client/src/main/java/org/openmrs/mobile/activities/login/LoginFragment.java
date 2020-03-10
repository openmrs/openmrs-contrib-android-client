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

package org.openmrs.mobile.activities.login;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dashboard.DashboardActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.api.FormListService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;

public class LoginFragment extends ACBaseFragment<LoginContract.Presenter> implements LoginContract.View {

    private static String mLastCorrectURL = "";
    private static List<Location> mLocationsList;
    final private String initialUrl = OpenMRS.getInstance().getServerUrl();
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    private View mRootView;
    private TextView mForgotPass;
    private TextInputEditText mUrl;
    private TextInputEditText mUsername;
    private TextInputEditText mPassword;
    private TextInputLayout mUrlInput;
    private TextInputLayout mUsernameInput;
    private TextInputLayout mPasswordInput;
    private MaterialButton mLoginButton;
    private ProgressBar mSpinner;
    private Spinner mDropdownLocation;
    private LinearLayout mLoginFormView;
    private AppCompatImageView mLoginSyncButton;
    private TextView mSyncStateLabel;
    private SparseArray<Bitmap> mBitmapCache;
    private ProgressBar mLocationLoadingProgressBar;
    private ImageView openMRSLogo;
    private TextView mAboutUsTextView;

    private LoginValidatorWatcher loginValidatorWatcher;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_login, container, false);

        initViewFields(mRootView);
        initListeners();
        if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
            mUrl.setText(OpenMRS.getInstance().getServerUrl());
            mLastCorrectURL = OpenMRS.getInstance().getServerUrl();
        } else {
            mUrl.setText(mLastCorrectURL);
        }
        hideURLDialog();

        // Font config
        FontsUtil.setFont(this.getActivity().findViewById(android.R.id.content));

        return mRootView;
    }

    private void initListeners() {
        mLoginSyncButton.setOnClickListener(view -> {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
            boolean syncState = prefs.getBoolean("sync", true);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit();
            editor.putBoolean("sync", !syncState);
            editor.apply();
            setSyncButtonState(!syncState);
        });

        loginValidatorWatcher = new LoginValidatorWatcher(mUrl, mUsername, mPassword, mDropdownLocation, mLoginButton);

        mUrl.setOnFocusChangeListener((view, hasFocus) -> {
            if (StringUtils.notEmpty(mUrl.getText().toString())
                    && !view.isFocused()
                    && loginValidatorWatcher.isUrlChanged()
                    || (loginValidatorWatcher.isUrlChanged() && !view.isFocused()
                    && loginValidatorWatcher.isLocationErrorOccurred())
                    || (!loginValidatorWatcher.isUrlChanged() && !view.isFocused())) {
                ((LoginFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.loginContentFrame))
                        .setUrl(mUrl.getText().toString());
                loginValidatorWatcher.setUrlChanged(false);
            }

            if (hasFocus) {
                mUrl.setHint("");
                mUrlInput.setHint(Html.fromHtml(getString(R.string.login_url_hint)));
            } else if (mUrl.getText().toString().equals("")) {
                mUrl.setHint(Html.fromHtml(getString(R.string.login_url_hint) + getString(R.string.req_star)));
                mUrlInput.setHint("");
            }
        });

        mUsername.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mUsernameInput.setHint(Html.fromHtml(getString(R.string.login_username_hint)));
            } else if (mUsername.getText().toString().equals("")) {
                mUsernameInput.setHint(Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star)));
                mUsernameInput.setHintAnimationEnabled(true);
            }
        });

        mPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mPasswordInput.setHint(Html.fromHtml(getString(R.string.login_password_hint)));
            } else if (mPassword.getText().toString().equals("")) {
                mPasswordInput.setHint(Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star)));
                mPasswordInput.setHintAnimationEnabled(true);
            }
        });

        mLoginButton.setOnClickListener(view -> mPresenter.login(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString(),
                initialUrl));

        mForgotPass.setOnClickListener(view -> forgotPassword());

        mAboutUsTextView.setOnClickListener(view -> openAboutPage());
    }

    private void initViewFields(View root) {
        mUrl = root.findViewById(R.id.loginUrlField);
        mUrlInput = root.findViewById(R.id.textInputLayoutLoginURL);


        mUsername = root.findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mUsernameInput = root.findViewById(R.id.textInputLayoutUsername);

        // If we have no cached username from previous sessions, we want the hint to be set
        // directly at the EditText. Otherwise, we set it on the TextInputLayout which will
        // be floating above the saved entry for the username.

        mPassword = root.findViewById(R.id.loginPasswordField);
        mPasswordInput = root.findViewById(R.id.textInputLayoutPassword);

        TextView mRequired = root.findViewById(R.id.loginRequiredLabel);
        mRequired.setText(Html.fromHtml(getString(R.string.req_star) + getString(R.string.login_required)));

        mLoginButton = root.findViewById(R.id.loginButton);
        mSpinner = root.findViewById(R.id.loginLoading);
        mLoginFormView = root.findViewById(R.id.loginFormView);
        mLoginSyncButton = root.findViewById(R.id.loginSyncButton);
        mSyncStateLabel = root.findViewById(R.id.syncLabel);
        mDropdownLocation = root.findViewById(R.id.locationSpinner);
        mForgotPass = root.findViewById(R.id.forgotPass);
        mLocationLoadingProgressBar = root.findViewById(R.id.locationLoadingProgressBar);
        mPasswordInput.setHint(Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star)));
        mUsernameInput.setHint(Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star)));
        mAboutUsTextView = root.findViewById(R.id.aboutUsTextView);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        boolean syncState = prefs.getBoolean("sync", true);
        setSyncButtonState(syncState);
        hideUrlLoadingAnimation();

        bindDrawableResources();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public void hideSoftKeys() {
        View view = this.getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(this.getActivity());
        }
        InputMethodManager inputMethodManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openAboutPage(){
        String userGuideUrl = ApplicationConstants.USER_GUIDE;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(userGuideUrl));
        startActivity(intent);
    }

    private void setSyncButtonState(boolean syncEnabled) {
        if (syncEnabled) {
            mSyncStateLabel.setText(getString(R.string.login_online));
        } else {
            mSyncStateLabel.setText(getString(R.string.login_offline));
        }
        mLoginSyncButton.setSelected(syncEnabled);
    }

    public void forgotPassword() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.forgot_dialog_title));
        bundle.setTextViewMessage(getString(R.string.forgot_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.forgot_button_ok));
        ((LoginActivity) this.getActivity()).createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG);
    }

    @Override
    public void showWarningDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.warning_dialog_title));
        bundle.setTextViewMessage(getString(R.string.warning_lost_data_dialog));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        ((LoginActivity) this.getActivity()).createAndShowDialog(bundle, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG);
    }

    @Override
    public void showLoadingAnimation() {
        mLoginFormView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingAnimation() {
        mLoginFormView.setVisibility(View.VISIBLE);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void showLocationLoadingAnimation() {
        mLoginButton.setEnabled(false);
        mLocationLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUrlLoadingAnimation() {
        mLocationLoadingProgressBar.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void finishLoginActivity() {
        getActivity().finish();
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<>();
        ImageView openMrsLogoImage = getActivity().findViewById(R.id.openmrsLogo);
        createImageBitmap(R.drawable.openmrs_logo, openMrsLogoImage.getLayoutParams());
        openMrsLogoImage.setImageBitmap(mBitmapCache.get(R.drawable.openmrs_logo));
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }

    public void initLoginForm(List<Location> locationsList, String serverURL) {
        setLocationErrorOccurred(false);
        mLastCorrectURL = serverURL;
        mUrl.setText(serverURL);
        mLocationsList = locationsList;
        List<String> items = getLocationStringList(locationsList);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
        mDropdownLocation.setAdapter(adapter);
        mLoginButton.setEnabled(false);
        mSpinner.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
        showOpenMRSLogo();
        if (locationsList.isEmpty()) {
            mDropdownLocation.setVisibility(View.GONE);
            mLoginButton.setEnabled(true);
        } else {
            mDropdownLocation.setVisibility(View.VISIBLE);
            mLoginButton.setEnabled(false);
        }
    }

    @Override
    public void userAuthenticated() {
        Intent intent = new Intent(mOpenMRS.getApplicationContext(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mOpenMRS.getApplicationContext().startActivity(intent);
        Intent formListServiceIntent = new Intent(mOpenMRS.getApplicationContext(), FormListService.class);
        mOpenMRS.getApplicationContext().startService(formListServiceIntent);
        mPresenter.saveLocationsToDatabase(mLocationsList, mDropdownLocation.getSelectedItem().toString());
    }

    @Override
    public void startFormListService() {
        Intent i = new Intent(getContext(), FormListService.class);
        getActivity().startService(i);
    }

    @Override
    public void showInvalidURLSnackbar(String message) {
        createSnackbar(message)
                .setAction(getResources().getString(R.string.snackbar_edit), view -> {
                    mUrl.requestFocus();
                    mUrl.selectAll();
                })
                .show();
    }

    @Override
    public void showInvalidLoginOrPasswordSnackbar() {
        String message = getResources().getString(R.string.invalid_login_or_password_message);
        createSnackbar(message)
                .setAction(getResources().getString(R.string.snackbar_edit), view -> {
                    mPassword.requestFocus();
                    mPassword.selectAll();
                })
                .show();
    }

    private Snackbar createSnackbar(String message) {
        return Snackbar
                .make(mRootView, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public void setLocationErrorOccurred(boolean errorOccurred) {
        this.loginValidatorWatcher.setLocationErrorOccurred(errorOccurred);
        mDropdownLocation.setVisibility(View.GONE);
        mLoginButton.setEnabled(!errorOccurred);
    }

    @Override
    public void showToast(String message, ToastUtil.ToastType toastType) {
        if (getActivity() != null) {
            ToastUtil.showShortToast(getActivity(), toastType, message);
        }
    }

    @Override
    public void showToast(int textId, ToastUtil.ToastType toastType) {
        if (getActivity() != null) {
            ToastUtil.showShortToast(getActivity(), toastType, getResources().getString(textId));
        }
    }


    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void setUrl(String url) {
        URLValidator.ValidationResult result = URLValidator.validate(url);
        if (result.isURLValid()) {
            mPresenter.loadLocations(result.getUrl());
        } else {
            showInvalidURLSnackbar("Invalid URL");
        }
    }

    public void hideURLDialog() {
        if (mLocationsList == null) {
            mPresenter.loadLocations(mLastCorrectURL);
        } else {
            initLoginForm(mLocationsList, mLastCorrectURL);
        }
    }

    public void showOpenMRSLogo() {
        openMRSLogo = mRootView.findViewById(R.id.openmrsLogo);
        createImageBitmap(R.drawable.openmrs_logo, openMRSLogo.getLayoutParams());
        openMRSLogo.setImageBitmap(mBitmapCache.get(R.drawable.openmrs_logo));
        openMRSLogo.setVisibility(View.VISIBLE);
    }

    public void login() {
        mPresenter.authenticateUser(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString());
    }

    public void login(boolean wipeDatabase) {
        mPresenter.authenticateUser(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString(), wipeDatabase);
    }

}
