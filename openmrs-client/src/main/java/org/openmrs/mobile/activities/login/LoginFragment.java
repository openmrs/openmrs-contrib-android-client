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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.dialog.DialogActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.api.FormListService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoginFragment extends Fragment implements LoginContract.View{

    private LoginContract.Presenter mPresenter;

    private TextView mForgotPass;
    private EditText mUrl;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressBar mSpinner;
    private Spinner mDropdownLocation;
    private LinearLayout mLoginFormView;
    private SwitchCompat mLoginNetworkingSwitch;
    CustomDialogBundle urlDialog = null;
    private SparseArray<Bitmap> mBitmapCache;

    private static boolean mErrorOccurred;
    private static String mLastCorrectURL = "";
    private static String mLastURL = "";
    private static boolean urlChanged = false;
    private static List<Location> mLocationsList;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        initViewFields(root);
        initListeners();

        if (mErrorOccurred || OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
            showURLDialog();
        } else {
            if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
                mUrl.setText(OpenMRS.getInstance().getServerUrl());
                mLastCorrectURL = OpenMRS.getInstance().getServerUrl();
            } else {
                mUrl.setText(mLastCorrectURL);
            }
            hideURLDialog();
        }

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        return root;
    }

    private void initListeners() {



        mLoginNetworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit();
                editor.putBoolean("sync", isChecked);
                editor.commit();
            }
        });

        mUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (StringUtils.notEmpty(mUrl.getText().toString()) && urlChanged) {
                    ((LoginFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.loginContentFrame))
                            .setUrl(mUrl.getText().toString());
                    urlChanged = false;
                }
            }
        });

        mUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!OpenMRS.getInstance().getServerUrl().equals(editable.toString()) && StringUtils.notEmpty(editable.toString())) {
                    urlChanged = true;
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(mUsername.getText().toString(),
                        mPassword.getText().toString(),
                        mUrl.getText().toString());
            }
        });
        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void initViewFields(View root) {
        mUrl = (EditText) root.findViewById(R.id.loginUrlField);
        mUsername = (EditText) root.findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mPassword = (EditText) root.findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) root.findViewById(R.id.loginButton);
        mSpinner = (ProgressBar) root.findViewById(R.id.loginLoading);
        mLoginFormView = (LinearLayout) root.findViewById(R.id.loginFormView);
        mLoginNetworkingSwitch = ((SwitchCompat) root.findViewById(R.id.loginsyncswitch));
        mDropdownLocation = (Spinner) root.findViewById(R.id.locationSpinner);
        mForgotPass = (TextView)root.findViewById(R.id.forgotPass);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        bindDrawableResources();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (urlDialog != null) {
            // saving last typed URL
            mLastURL = ((LoginActivity)getActivity()).getDialogEditTextValue();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        this.mPresenter = checkNotNull(presenter);
    }

    public void forgotPassword() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.forgot_dialog_title));
        bundle.setTextViewMessage(getString(R.string.forgot_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.forgot_button_ok));
        ((LoginActivity) this.getActivity()).createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG);
    }

    public void showURLDialog() {
        urlDialog = new CustomDialogBundle();
        urlDialog.setTitleViewMessage(getString(R.string.login_dialog_title));
        String serverURL = OpenMRS.getInstance().getServerUrl();
        if (!mLastURL.equals(ApplicationConstants.EMPTY_STRING)) {
            urlDialog.setEditTextViewMessage(mLastURL);
        } else if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING) &&
                !serverURL.equals(ApplicationConstants.EMPTY_STRING)) {
            urlDialog.setEditTextViewMessage(serverURL);
        } else {
            urlDialog.setEditTextViewMessage(mLastCorrectURL);
        }
        urlDialog.setRightButtonText(getString(R.string.dialog_button_done));
        urlDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.SET_URL);
        if (!OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
            urlDialog.setLeftButtonText(getString(R.string.dialog_button_cancel));
            urlDialog.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS_URL_DIALOG);
        }
        ((LoginActivity) this.getActivity()).createAndShowDialog(urlDialog, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    private void showInvalidURLDialog() {
        mErrorOccurred = true;
        Intent i = new Intent(this.getContext(), DialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG);
        startActivity(i);
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
    public void showToast(ToastUtil.ToastType toastType, int message) {
        ToastUtil.showShortToast(getActivity(), toastType, message);
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
    public void finishLoginActivity() {
        getActivity().finish();
    }

    @Override
    public void sendIntentBroadcast(String message) {
        getActivity().sendBroadcast(new Intent(message));
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<>();
        ImageView openMrsLogoImage = (ImageView) getActivity().findViewById(R.id.openmrsLogo);
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
        mErrorOccurred = false;
        mLastCorrectURL = serverURL;
        mLastURL = ApplicationConstants.EMPTY_STRING;
        mUrl.setText(serverURL);
        mLocationsList = locationsList;
        List<String> items = getLocationStringList(locationsList);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
        mDropdownLocation.setAdapter(adapter);

        mDropdownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean mInitialized;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mInitialized && position >= 0 && id >= 1) {
                    mInitialized = true;
                    adapter.notifyDataSetChanged();
                    mLoginButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mLoginButton.setEnabled(false);
        mSpinner.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    @Override
    public void userAuthenticated() {
        mPresenter.saveLocationsToDatabase(mLocationsList, mDropdownLocation.getSelectedItem().toString());
    }

    @Override
    public void startFormListService() {
        Intent i=new Intent(getContext(), FormListService.class);
        getActivity().startService(i);
    }

    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void setUrl(String url) {
        URLValidator.ValidationResult result = URLValidator.validate(url);
        mLastURL = result.getUrl();
        if (result.isURLValid()) {
            mSpinner.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);
            mPresenter.loadLocations(result.getUrl());
        } else {
            showInvalidURLDialog();
        }
    }

    public void setErrorOccurred(boolean errorOccurred) {
        mErrorOccurred = errorOccurred;
    }

    public void hideURLDialog() {
        if (mLocationsList == null) {
            mPresenter.loadLocations(mLastCorrectURL);
        } else {
            initLoginForm(mLocationsList, mLastCorrectURL);
        }
    }

    public void login(){
        mPresenter.authenticateUser(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString());
    }

}
