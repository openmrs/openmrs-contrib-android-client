package org.openmrs.client.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.utilities.ApplicationConstants;

import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_CONN_TIMEOUT_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_NO_INTERNET_CONNECTION_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_SERVER_UNAVAILABLE_BROADCAST;
import static org.openmrs.client.utilities.ApplicationConstants.CustomIntentActions.ACTION_UNAUTHORIZED_BROADCAST;

public abstract class ACBaseActivity extends ActionBarActivity {

    protected FragmentManager mFragmentManager;
    protected final OpenMRSLogger mOpenMRSLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private CustomFragmentDialog mCurrentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();

        registerReceiver(mAuthFailedReceiver, new IntentFilter(ACTION_AUTH_FAILED_BROADCAST));
        registerReceiver(mConnectionTimeoutReceiver, new IntentFilter(ACTION_CONN_TIMEOUT_BROADCAST));
        registerReceiver(mServerUnavailableReceiver, new IntentFilter(ACTION_SERVER_UNAVAILABLE_BROADCAST));
        registerReceiver(mNoInternetConnectionReceiver, new IntentFilter(ACTION_NO_INTERNET_CONNECTION_BROADCAST));
        registerReceiver(mUnauthorizedReceiver, new IntentFilter(ACTION_UNAUTHORIZED_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAuthFailedReceiver);
        unregisterReceiver(mConnectionTimeoutReceiver);
        unregisterReceiver(mNoInternetConnectionReceiver);
        unregisterReceiver(mServerUnavailableReceiver);
        unregisterReceiver(mUnauthorizedReceiver);
        mCurrentDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        this.getSupportActionBar().setSubtitle(getString(R.string.dashboard_logged_as, OpenMRS.getInstance().getUsername()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void createAndShowDialog(CustomDialogBundle bundle, String tag) {
        CustomFragmentDialog instance = CustomFragmentDialog.newInstance(bundle);
        instance.show(mFragmentManager, tag);
        mCurrentDialog = instance;
    }

    public synchronized CustomFragmentDialog getCurrentDialog() {
        return mCurrentDialog;
    }

    private BroadcastReceiver mAuthFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_AUTH_FAILED_BROADCAST.equals(intent.getAction())) {
                showAuthenticationFailedDialog();
            }
        }
    };

    private BroadcastReceiver mConnectionTimeoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CONN_TIMEOUT_BROADCAST.equals(intent.getAction())) {
                showConnectionTimeoutDialog();
            }
        }
    };

    private BroadcastReceiver mNoInternetConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_NO_INTERNET_CONNECTION_BROADCAST.equals(intent.getAction())) {
                showNoInternetConnectionDialog();
            }
        }
    };

    private BroadcastReceiver mServerUnavailableReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SERVER_UNAVAILABLE_BROADCAST.equals(intent.getAction())) {
                showServerUnavailableDialog();
            }
        }
    };

    private BroadcastReceiver mUnauthorizedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_UNAUTHORIZED_BROADCAST.equals(intent.getAction())) {
                showUnauthorizedDialog();
            }
        }
    };

    public void moveUnauthorizedUserToLoginScreen() {
        OpenMRSDBOpenHelper.getInstance().closeDatabases();
        OpenMRS.getInstance().clearUserPreferencesDataWhenUnauthorized();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
}
