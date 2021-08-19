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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.community.contact.ContactUsActivity;
import org.openmrs.mobile.activities.dashboard.DashboardActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.services.FormListService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databinding.FragmentLoginBinding;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.utilities.URLValidator;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends ACBaseFragment<LoginContract.Presenter> implements LoginContract.View {
    private static String mLastCorrectURL = "";
    private static List<LocationEntity> mLocationsList;
    final private String initialUrl = OpenmrsAndroid.getServerUrl();
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    private FragmentLoginBinding binding;
    private View mRootView;
    private LoginValidatorWatcher loginValidatorWatcher;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        initViewFields();
        initListeners();
        if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
            binding.loginUrlField.setText(OpenmrsAndroid.getServerUrl());
            mLastCorrectURL = OpenmrsAndroid.getServerUrl();
        } else {
            binding.loginUrlField.setText(mLastCorrectURL);
        }
        hideURLDialog();
        return mRootView;
    }

    private void initListeners() {
        binding.loginSyncButton.setOnClickListener(view -> {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
            boolean syncState = prefs.getBoolean("sync", true);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit();
            editor.putBoolean("sync", !syncState);
            editor.apply();
            setSyncButtonState(!syncState);
        });

        loginValidatorWatcher = new LoginValidatorWatcher(binding.loginUrlField, binding.loginUsernameField,
                binding.loginPasswordField, binding.locationSpinner, binding.loginButton);

        binding.loginUrlField.setOnFocusChangeListener((view, hasFocus) -> {
            if (StringUtils.notEmpty(binding.loginUrlField.getText().toString())
                    && !view.isFocused()
                    && loginValidatorWatcher.isUrlChanged()
                    || (loginValidatorWatcher.isUrlChanged() && !view.isFocused()
                    && loginValidatorWatcher.isLocationErrorOccurred())
                    || (!loginValidatorWatcher.isUrlChanged() && !view.isFocused())) {
                ((LoginFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.loginContentFrame))
                        .setUrl(binding.loginUrlField.getText().toString());
                loginValidatorWatcher.setUrlChanged(false);
            }
        });

        binding.loginUsernameField.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.textInputLayoutUsername.setHint(Html.fromHtml(getString(R.string.login_username_hint)));
            } else if (binding.loginUsernameField.getText().toString().equals("")) {
                binding.textInputLayoutUsername.setHint(Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star)));
                binding.textInputLayoutUsername.setHintAnimationEnabled(true);
            }
        });

        binding.loginPasswordField.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.textInputLayoutPassword.setHint(Html.fromHtml(getString(R.string.login_password_hint)));
            } else if (binding.loginPasswordField.getText().toString().equals("")) {
                binding.textInputLayoutPassword.setHint(Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star)));
                binding.textInputLayoutPassword.setHintAnimationEnabled(true);
            }
        });

        binding.loginButton.setOnClickListener(view -> mPresenter.login(binding.loginUsernameField.getText().toString(),
                binding.loginPasswordField.getText().toString(),
                binding.loginUrlField.getText().toString(),
                initialUrl));

        binding.forgotPass.setOnClickListener(view -> startActivity(new Intent(getContext(), ContactUsActivity.class)));

        binding.aboutUsTextView.setOnClickListener(view -> openAboutPage());
    }

    private void initViewFields() {
        binding.textInputLayoutPassword.setHint(Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star)));
        binding.textInputLayoutUsername.setHint(Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star)));
        binding.loginUrlField.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_grey_8x)));
        binding.textInputLayoutUsername.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_grey_8x)));
        binding.textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_grey_8x)));
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        boolean syncState = prefs.getBoolean("sync", true);
        setSyncButtonState(syncState);
        hideUrlLoadingAnimation();
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

    public void openAboutPage() {
        String userGuideUrl = ApplicationConstants.USER_GUIDE;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(userGuideUrl));
        startActivity(intent);
    }

    private void setSyncButtonState(boolean syncEnabled) {
        if (syncEnabled) {
            binding.syncLabel.setText(getString(R.string.login_online));
        } else {
            binding.syncLabel.setText(getString(R.string.login_offline));
        }
        binding.loginSyncButton.setChecked(syncEnabled);
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
        binding.loginFormView.setVisibility(View.GONE);
        binding.loginLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingAnimation() {
        binding.loginFormView.setVisibility(View.VISIBLE);
        binding.loginLoading.setVisibility(View.GONE);
    }

    @Override
    public void showLocationLoadingAnimation() {
        binding.loginButton.setEnabled(false);
        binding.locationLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUrlLoadingAnimation() {
        binding.locationLoadingProgressBar.setVisibility(View.GONE);
        binding.loginLoading.setVisibility(View.GONE);
    }

    @Override
    public void finishLoginActivity() {
        getActivity().finish();
    }

    public void initLoginForm(List<LocationEntity> locationsList, String serverURL) {
        setLocationErrorOccurred(false);
        mLastCorrectURL = serverURL;
        binding.loginUrlField.setText(serverURL);
        mLocationsList = locationsList;
        if (isActivityNotNull()) {
            List<String> items = getLocationStringList(locationsList);
            final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
            binding.locationSpinner.setAdapter(adapter);
            binding.loginButton.setEnabled(false);
            binding.loginLoading.setVisibility(View.GONE);
            binding.loginFormView.setVisibility(View.VISIBLE);
            if (locationsList.isEmpty()) {
                binding.loginButton.setEnabled(true);
            } else {
                binding.loginButton.setEnabled(false);
            }
        }
    }

    @Override
    public void userAuthenticated() {
        Intent intent = new Intent(mOpenMRS.getApplicationContext(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mOpenMRS.getApplicationContext().startActivity(intent);
        Intent formListServiceIntent = new Intent(mOpenMRS.getApplicationContext(), FormListService.class);
        mOpenMRS.getApplicationContext().startService(formListServiceIntent);
        mPresenter.saveLocationsToDatabase(mLocationsList, binding.locationSpinner.getSelectedItem().toString());
    }

    @Override
    public void startFormListService() {
        if (isActivityNotNull()) {
            Intent i = new Intent(getContext(), FormListService.class);
            getActivity().startService(i);
        }
    }

    @Override
    public void showInvalidURLSnackbar(String message) {
        if (isActivityNotNull()) {
            createSnackbar(message)
                    .setAction(getResources().getString(R.string.snackbar_choose), view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_server_list)));
                        startActivity(intent);
                    })
                    .show();
        }
    }

    @Override
    public void showInvalidURLSnackbar(int messageID) {
        if (isActivityNotNull()) {
            createSnackbar(getString(messageID))
                    .setAction(getResources().getString(R.string.snackbar_choose), view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_server_list)));
                        startActivity(intent);
                    })
                    .show();
        }
    }

    @Override
    public void showInvalidLoginOrPasswordSnackbar() {
        String message = getResources().getString(R.string.invalid_login_or_password_message);
        if (isActivityNotNull()) {
            createSnackbar(message)
                    .setAction(getResources().getString(R.string.snackbar_edit), view -> {
                        binding.loginPasswordField.requestFocus();
                        binding.loginPasswordField.selectAll();
                    })
                    .show();
        }
    }

    private Snackbar createSnackbar(String message) {
        return Snackbar
                .make(mRootView, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public void setLocationErrorOccurred(boolean errorOccurred) {
        this.loginValidatorWatcher.setLocationErrorOccurred(errorOccurred);
        binding.loginButton.setEnabled(!errorOccurred);
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

    private List<String> getLocationStringList(List<LocationEntity> locationList) {
        List<String> list = new ArrayList<>();
        //If spinner is at start option, append a red * to signify requirement
		list.add(Html.fromHtml(getString(R.string.login_location_select) + getString(R.string.req_star)).toString());
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
            showInvalidURLSnackbar(getResources().getString(R.string.invalid_URL_message));
        }
    }

    public void hideURLDialog() {
        if (mLocationsList == null) {
            mPresenter.loadLocations(mLastCorrectURL);
        } else {
            initLoginForm(mLocationsList, mLastCorrectURL);
        }
    }

    public void login() {
        mPresenter.authenticateUser(binding.loginUsernameField.getText().toString(),
                binding.loginPasswordField.getText().toString(),
                binding.loginUrlField.getText().toString());
    }

    public void login(boolean wipeDatabase) {
        mPresenter.authenticateUser(binding.loginUsernameField.getText().toString(),
                binding.loginPasswordField.getText().toString(),
                binding.loginUrlField.getText().toString(), wipeDatabase);
    }

    private boolean isActivityNotNull() {
        return (isAdded() && getActivity() != null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}