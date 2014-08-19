package org.openmrs.client.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.SettingsArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.SettingsListItemDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ACBaseActivity {

    private ListView mSettingsListView;
    private List<SettingsListItemDTO> mListItem = new ArrayList<SettingsListItemDTO>();
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logger.d("Started onCreate SettingsActivity");
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
        String filename = mOpenMRS.getOpenMRSDir() + logger.getLogFilename();
        try {
            File file = new File(filename);
            size = file.length();
            size = size / 1024;
            logger.i("File Path : " + file.getPath() + ", File size: " + size + " KB");
        } catch (Exception e) {
            logger.w("File not found");
        }

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logs),
                                              filename,
                                              String.valueOf(size)));

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_about),
                                              getResources().getString(R.string.app_name),
                                              "version 0.1")); //TODO get version from manifest

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logout),
                                              "",
                                              ""));
    }
}
