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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.activities.fragments.FindPatientLastViewedFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public abstract class ACBaseActivity extends ActionBarActivity {
    public static final int CAPTURE_VITALS_REQUEST_CODE = 1;

    protected FragmentManager mFragmentManager;
    protected final OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected final OpenMRSLogger mOpenMRSLogger = mOpenMRS.getOpenMRSLogger();
    private CustomFragmentDialog mCurrentDialog;
    protected AuthorizationManager mAuthorizationManager;
    protected CustomFragmentDialog mCustomFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mAuthorizationManager = new AuthorizationManager();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(this instanceof LoginActivity || this instanceof DialogActivity)) {
            ActionBar actionBar = this.getSupportActionBar();
            if (!mAuthorizationManager.isUserLoggedIn()) {
                mAuthorizationManager.moveToLoginActivity();
            } else if (this instanceof DashboardActivity || this instanceof SettingsActivity
                    || this instanceof FindPatientsActivity || this instanceof FindActiveVisitsActivity) {
                actionBar.setSubtitle(getString(R.string.dashboard_logged_as, mOpenMRS.getUsername()));
            }
            setOfflineActionBarTitle(actionBar);
        }
    }

    private void setOfflineActionBarTitle(ActionBar actionBar) {
        String title = (String) actionBar.getTitle();
        if (title != null) {
            String offlineTitle = " (" + getString(R.string.settings_toggle_off) + ")";
            if (!mOpenMRS.getOnlineMode() && !(this instanceof PatientDashboardActivity)
                    && !title.endsWith(offlineTitle)) {
                actionBar.setTitle(title + offlineTitle);
            } else if (mOpenMRS.getOnlineMode() && title.endsWith(offlineTitle)) {
                actionBar.setTitle(title.replace(offlineTitle, ApplicationConstants.EMPTY_STRING));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSettings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
            case R.id.actionSearch:
                return true;
            case R.id.actionLogout:
                this.showLogoutDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout() {
        mOpenMRS.clearUserPreferencesData();
        FindPatientLastViewedFragment.clearLastViewedPatientList();
        mAuthorizationManager.moveToLoginActivity();
        OpenMRSDBOpenHelper.getInstance().closeDatabases();
    }

    private void showLogoutDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.logout_dialog_title));
        bundle.setTextViewMessage(getString(R.string.logout_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGOUT);
        bundle.setRightButtonText(getString(R.string.logout_dialog_button));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG);
    }

    protected void showConnectionTimeoutDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.conn_timeout_dialog_title));
        bundle.setTextViewMessage(getString(R.string.conn_timeout_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.CONN_TIMEOUT_DIALOG_TAG);
    }

    protected void showAuthenticationFailedDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.auth_failed_dialog_title));
        bundle.setTextViewMessage(getString(R.string.auth_failed_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.AUTH_FAILED_DIALOG_TAG);
    }

    protected void showNoInternetConnectionDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.no_internet_conn_dialog_title));
        bundle.setTextViewMessage(getString(R.string.no_internet_conn_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.NO_INTERNET_CONN_DIALOG_TAG);
    }

    protected void showServerUnavailableDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.server_unavailable_dialog_title));
        bundle.setTextViewMessage(getString(R.string.server_unavailable_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.SERVER_UNAVAILABLE_DIALOG_TAG);
    }

    protected void showUnauthorizedDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.unauthorized_dialog_title));
        bundle.setTextViewMessage(getString(R.string.unauthorized_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.UNAUTHORIZED);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.UNAUTHORIZED_DIALOG_TAG);
    }

    protected void showServerErrorDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.server_error_dialog_title));
        bundle.setTextViewMessage(getString(R.string.server_error_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.SOCKET_EXCEPTION_DIALOG_TAG);
    }

    protected void showSocketExceptionErrorDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.socket_exception_dialog_title));
        bundle.setTextViewMessage(getString(R.string.socket_exception_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.SERVER_ERROR_DIALOG_TAG);
    }

    public void showNoVisitDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.no_visit_dialog_title));
        bundle.setTextViewMessage(getString(R.string.no_visit_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.START_VISIT);
        bundle.setRightButtonText(getString(R.string.no_visit_dialog_button_accept));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.no_visit_dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.NO_VISIT_DIALOG_TAG);
    }

    public void showStartVisitImpossibleDialog(CharSequence title) {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.start_visit_impossible_dialog_title));
        bundle.setTextViewMessage(getString(R.string.start_visit_impossible_dialog_message, title));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_IMPOSSIBLE_DIALOG_TAG);
    }

    public void showStartVisitDialog(CharSequence title) {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.start_visit_dialog_title));
        bundle.setTextViewMessage(getString(R.string.start_visit_dialog_message, title));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.START_VISIT);
        bundle.setRightButtonText(getString(R.string.dialog_button_confirm));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_DIALOG_TAG);
    }

    public void createAndShowDialog(CustomDialogBundle bundle, String tag) {
        CustomFragmentDialog instance = CustomFragmentDialog.newInstance(bundle);
        instance.show(mFragmentManager, tag);
        mCurrentDialog = instance;
    }

    public synchronized CustomFragmentDialog getCurrentDialog() {
        return mCurrentDialog;
    }

    public void moveUnauthorizedUserToLoginScreen() {
        OpenMRSDBOpenHelper.getInstance().closeDatabases();
        mOpenMRS.clearUserPreferencesData();
        FindPatientLastViewedFragment.clearLastViewedPatientList();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    public void showProgressDialog(int dialogMessageId) {
        showProgressDialog(getString(dialogMessageId));
    }

    protected void showProgressDialog(String dialogMessage) {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setProgressViewMessage(getString(R.string.progress_dialog_message));
        bundle.setProgressDialog(true);
        bundle.setTitleViewMessage(dialogMessage);
        mCustomFragmentDialog = CustomFragmentDialog.newInstance(bundle);
        mCustomFragmentDialog.setCancelable(false);
        mCustomFragmentDialog.setRetainInstance(true);
        mCustomFragmentDialog.show(mFragmentManager, dialogMessage);
    }

    public void dismissProgressDialog(boolean errorOccurred, Integer successMessageId, Integer errorMessageId) {
        mCustomFragmentDialog.dismiss();

        if (!errorOccurred && successMessageId != null) {
            ToastUtil.showShortToast(this,
                    ToastUtil.ToastType.SUCCESS,
                    successMessageId);
        } else if (errorMessageId != null) {
            ToastUtil.showShortToast(this,
                    ToastUtil.ToastType.ERROR,
                    errorMessageId);
        }
    }

    public AuthorizationManager getAuthorizationManager() {
        return mAuthorizationManager;
    }
}
