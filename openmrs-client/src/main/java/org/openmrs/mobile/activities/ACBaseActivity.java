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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.login.LoginActivity;
import org.openmrs.mobile.activities.settings.SettingsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ForceClose;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ACBaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager;
    protected final OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected final OpenMRSLogger mOpenMRSLogger = mOpenMRS.getOpenMRSLogger();
    protected AuthorizationManager mAuthorizationManager;
    protected CustomFragmentDialog mCustomFragmentDialog;
    private MenuItem mSyncbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ForceClose(this));
        mFragmentManager = getSupportFragmentManager();
        mAuthorizationManager = new AuthorizationManager();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Boolean flag = extras.getBoolean("flag");
            String errorReport = extras.getString("error");
            if (flag) {
                showAppCrashDialog(errorReport);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (!(this instanceof LoginActivity) && !mAuthorizationManager.isUserLoggedIn()) {
            mAuthorizationManager.moveToLoginActivity();
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
    public boolean onPrepareOptionsMenu(final Menu menu) {
        mSyncbutton = menu.findItem(R.id.syncbutton);
        MenuItem logoutMenuItem = menu.findItem(R.id.actionLogout);
        if (logoutMenuItem != null) {
            logoutMenuItem.setTitle(getString(R.string.action_logout) + " " + mOpenMRS.getUsername());
        }
        if(mSyncbutton !=null) {
            final Boolean syncState = NetworkUtils.isOnline();
            setSyncButtonState(syncState);
        }
        return true;
    }

    private void setSyncButtonState(boolean syncState) {
        if (syncState) {
            mSyncbutton.setIcon(R.drawable.ic_sync_on);
        }
        else {
            mSyncbutton.setIcon(R.drawable.ic_sync_off);
        }
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
                return true;
            case R.id.actionSearchLocal:
                return true;
            case R.id.actionLogout:
                this.showLogoutDialog();
                return true;
            case R.id.syncbutton:
                boolean syncState = OpenMRS.getInstance().getSyncState();
                if (syncState) {
                    OpenMRS.getInstance().setSyncState(false);
                    setSyncButtonState(false);
                } else if(NetworkUtils.hasNetwork()){
                    OpenMRS.getInstance().setSyncState(true);
                    setSyncButtonState(true);
                    Intent intent = new Intent("org.openmrs.mobile.intent.action.SYNC_PATIENTS");
                    getApplicationContext().sendBroadcast(intent);
                } else {
                    showNoInternetConnectionSnackbar();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNoInternetConnectionSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.no_internet_connection_message), Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void logout() {
        mOpenMRS.clearUserPreferencesData();
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

    public void showStartVisitImpossibleDialog(CharSequence title) {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.start_visit_unsuccessful_dialog_title));
        bundle.setTextViewMessage(getString(R.string.start_visit_unsuccessful_dialog_message, title));
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

    public void showDeletePatientDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.action_delete_patient));
        bundle.setTextViewMessage(getString(R.string.delete_patient_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DELETE_PATIENT);
        bundle.setRightButtonText(getString(R.string.dialog_button_confirm));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.DELET_PATIENT_DIALOG_TAG);
    }

    public void createAndShowDialog(CustomDialogBundle bundle, String tag) {
        CustomFragmentDialog instance = CustomFragmentDialog.newInstance(bundle);
        instance.show(mFragmentManager, tag);
    }

    public void moveUnauthorizedUserToLoginScreen() {
        OpenMRSDBOpenHelper.getInstance().closeDatabases();
        mOpenMRS.clearUserPreferencesData();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    public void showProgressDialog(int dialogMessageId) {
        showProgressDialog(getString(dialogMessageId));
    }

    public void dismissCustomFragmentDialog() {
        if (mCustomFragmentDialog != null) {
            mCustomFragmentDialog.dismiss();
        }
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

    public void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public void showAppCrashDialog(String error) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle(R.string.crash_dialog_title);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.crash_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.crash_dialog_positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.crash_dialog_negative_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNeutralButton(R.string.crash_dialog_neutral_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String filename = OpenMRS.getInstance().getOpenMRSDir()
                                + File.separator + mOpenMRSLogger.getLogFilename();
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_SUBJECT, R.string.error_email_subject_app_crashed);
                        email.putExtra(Intent.EXTRA_TEXT, error);
                        email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
                        //need this to prompts email client only
                        email.setType("message/rfc822");

                        startActivity(Intent.createChooser(email, getString(R.string.choose_a_email_client)));
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

