package org.openmrs.client.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.adapters.SettingsArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.models.SettingsListItemDTO;
import org.openmrs.client.net.AuthorizationManager;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ACBaseActivity {
    private static final int LOGOUT_ITEM_ID = 2;
    private static final int ONE_KB = 1024;

    private ListView mSettingsListView;
    private List<SettingsListItemDTO> mListItem = new ArrayList<SettingsListItemDTO>();

    private AuthorizationManager mAuthorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mOpenMRSLogger.d("onCreate");
        fillList();
        mSettingsListView = (ListView) findViewById(R.id.settingsListView);
        SettingsArrayAdapter mAdapter = new SettingsArrayAdapter(this, mListItem);
        mSettingsListView.setAdapter(mAdapter);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LOGOUT_ITEM_ID == position) {
                    showLogoutDialog();
                }
            }
        };
        mSettingsListView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mAuthorizationManager) {
            mAuthorizationManager = new AuthorizationManager(this);
        }
    }

    private void showLogoutDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.logout_dialog_title));
        bundle.setTextViewMessage(getString(R.string.logout_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.LOGOUT);
        bundle.setLeftButtonText(getString(R.string.logout_dialog_button));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setRightButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG);
    }

    public void logout() {
        clearUserPreferencesData();
        this.finish();
        mAuthorizationManager.moveToLoginActivity();
    }

    private void clearUserPreferencesData() {
        SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.SESSION_TOKEN);
        editor.remove(ApplicationConstants.USER_NAME);
        editor.commit();
    }

    private void fillList() {
        long size = 0;
        String filename = OpenMRS.getInstance().getOpenMRSDir() + mOpenMRSLogger.getLogFilename();
        try {
            File file = new File(filename);
            size = file.length();
            size = size / ONE_KB;
            mOpenMRSLogger.i("File Path : " + file.getPath() + ", File size: " + size + " KB");
        } catch (Exception e) {
            mOpenMRSLogger.w("File not found");
        }

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logs),
                                              filename,
                                              "Size: " + size + "kB"));

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_about),
                                              getResources().getString(R.string.app_name),
                                              "version 1.0")); //TODO get version from manifest

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logout)));
    }
}
