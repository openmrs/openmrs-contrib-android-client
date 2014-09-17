package org.openmrs.client.activities;

import android.content.Intent;
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
import org.openmrs.client.net.AuthorizationManager;
import org.openmrs.client.utilities.ApplicationConstants;

public abstract class ACBaseActivity extends ActionBarActivity {

    protected FragmentManager mFragmentManager;
    protected final OpenMRSLogger mOpenMRSLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private CustomFragmentDialog mCurrentDialog;
    protected AuthorizationManager mAuthorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mAuthorizationManager = new AuthorizationManager(this);
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
            if (!mAuthorizationManager.isUserLoggedIn()) {
                mAuthorizationManager.moveToLoginActivity();
            } else if (this instanceof DashboardActivity || this instanceof SettingsActivity) {
                this.getSupportActionBar().setSubtitle(getString(R.string.dashboard_logged_as, OpenMRS.getInstance().getUsername()));
            }
        }
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

    protected void showServerErrorDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.server_error_dialog_title));
        bundle.setTextViewMessage(getString(R.string.server_error_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.SERVER_ERROR_DIALOG_TAG);
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
        OpenMRS.getInstance().clearUserPreferencesDataWhenUnauthorized();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
}
