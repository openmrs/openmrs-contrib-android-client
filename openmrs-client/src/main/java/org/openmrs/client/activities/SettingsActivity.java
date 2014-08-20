package org.openmrs.client.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.SettingsArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.SettingsListItemDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ACBaseActivity {
    private static final int ONE_KB = 1024;

    private ListView mSettingsListView;
    private List<SettingsListItemDTO> mListItem = new ArrayList<SettingsListItemDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mOpenMRSLogger.d("onCreate");
        fillList();
        mSettingsListView = (ListView) findViewById(R.id.settingsListView);
        SettingsArrayAdapter mAdapter = new SettingsArrayAdapter(this, mListItem);
        mSettingsListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
