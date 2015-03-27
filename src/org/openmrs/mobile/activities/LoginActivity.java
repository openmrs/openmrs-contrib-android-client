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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.bundle.AuthorizationManagerBundle;
import org.openmrs.mobile.adapters.LocationArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.net.LocationManager;
import org.openmrs.mobile.net.helpers.AuthorizationHelper;
import org.openmrs.mobile.net.helpers.LocationHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ACBaseActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressBar mSpinner;
    private Spinner mDropdownLocation;
    private LinearLayout mLoginFormView;
    private SparseArray<Bitmap> mBitmapCache;
    private static boolean mErrorOccurred;
    private static String mLastCorrectURL = "";
    private static String mLastURL = "";
    private static List<Location> mLocationsList;
    private TextView mUrlTextView;
    private RelativeLayout mUrlField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view_layout);
        mUrlField = (RelativeLayout) findViewById(R.id.urlField);
        mUsername = (EditText) findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mPassword = (EditText) findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginFields()) {
                    if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                            !mOpenMRS.getUsername().equals(mUsername.getText().toString())) ||
                            ((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                                    !mOpenMRS.getServerUrl().equals(mUrlTextView.getText().toString())))) {
                        showWarningDialog();
                    } else {
                        login();
                    }
                } else {
                    ToastUtil.showShortToast(getApplicationContext(),
                            ToastUtil.ToastType.ERROR,
                            R.string.login_dialog_login_or_password_empty);
                }
            }
        });
        mSpinner = (ProgressBar) findViewById(R.id.loginLoading);
        mLoginFormView = (LinearLayout) findViewById(R.id.loginFormView);
        mDropdownLocation = (Spinner) findViewById(R.id.locationSpinner);
        mUrlTextView = (TextView) findViewById(R.id.urlText);
        if (mErrorOccurred || OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
            showURLDialog();
        } else {
            if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
                mUrlTextView.setText(OpenMRS.getInstance().getServerUrl());
                mLastCorrectURL = OpenMRS.getInstance().getServerUrl();
            } else {
                mUrlTextView.setText(mLastCorrectURL);
            }
            mUrlField.setVisibility(View.VISIBLE);
            hideURLDialog();
        }
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        bindDrawableResources();
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

    public void onEditUrlCallback(View v) {
        showURLDialog();
    }

    public void showURLDialog() {
        mUrlField.setVisibility(View.INVISIBLE);
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.login_dialog_title));
        if (!mLastURL.equals(ApplicationConstants.EMPTY_STRING)) {
            bundle.setEditTextViewMessage(mLastURL);
        } else if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
            bundle.setEditTextViewMessage(OpenMRS.getInstance().getServerUrl());
        } else {
            bundle.setEditTextViewMessage(mLastCorrectURL);
        }
        bundle.setRightButtonText(getString(R.string.dialog_button_done));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.SET_URL);
        if (!OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
            bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
            bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS_URL_DIALOG);
        }
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    private void showInvalidURLDialog() {
        mErrorOccurred = true;
        Intent i = new Intent(this, DialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG);
        startActivity(i);
    }

    private void showWarningDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.warning_dialog_title));
        bundle.setTextViewMessage(getString(R.string.warning_lost_data_dialog));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG);
    }

    public void login() {
        mLoginFormView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        AuthorizationManagerBundle bundle =
                AuthorizationHelper.createBundle(
                        mUsername.getText().toString(),
                        mPassword.getText().toString(),
                        mUrlTextView.getText().toString());
        mAuthorizationManager.login(
                AuthorizationHelper.createLoginListener(bundle, this));
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<Bitmap>();
        ImageView openMrsLogoImage = (ImageView) findViewById(R.id.openmrsLogo);
        createImageBitmap(R.drawable.openmrs_logo, openMrsLogoImage.getLayoutParams());
        ImageView urlEdit = (ImageView) findViewById(R.id.urlEdit);
        createImageBitmap(R.drawable.ico_edit, urlEdit.getLayoutParams());
        openMrsLogoImage.setImageBitmap(mBitmapCache.get(R.drawable.openmrs_logo));
        urlEdit.setImageBitmap(mBitmapCache.get(R.drawable.ico_edit));
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
        mUrlTextView.setText(serverURL);
        mUrlField.setVisibility(View.VISIBLE);
        mLocationsList = locationsList;
        List<String> items = getLocationStringList(locationsList);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this, items);
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

    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void saveLocationsToDatabase() {
        OpenMRS.getInstance().setLocation(mDropdownLocation.getSelectedItem().toString());
        new LocationDAO().deleteAllLocations();
        for (int i = 0; i < mLocationsList.size(); i++) {
            new LocationDAO().saveLocation(mLocationsList.get(i));
        }
    }

    public void setUrl(String url) {
        mLastURL = url;
        URLValidator.ValidationResult result = URLValidator.validate(url);
        if (result.isURLValid()) {
            mSpinner.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);
            new LocationManager().getAvailableLocation(
                    LocationHelper.createAvailableLocationListener(result.getUrl(), this));
        } else {
            showInvalidURLDialog();
        }
    }

    public void setErrorOccurred(boolean errorOccurred) {
        mErrorOccurred = errorOccurred;
    }

    public void hideURLDialog() {
        if (mLocationsList == null) {
            new LocationManager().getAvailableLocation(
                    LocationHelper.createAvailableLocationListener(mLastCorrectURL, this));
        } else {
            initLoginForm(mLocationsList, mLastCorrectURL);
        }
    }
}
