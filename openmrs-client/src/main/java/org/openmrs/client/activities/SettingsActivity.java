package org.openmrs.client.activities;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.openmrs.client.R;
import org.openmrs.client.applications.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends ACBaseActivity {

    private ListView mSettingsListView;
    private ArrayList<HashMap<String, String>> mList;
    private String[] mRowFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Logger.d("Started onCreate SettingsActivity");

        mSettingsListView = (ListView) findViewById(R.id.settingsListView);
        mRowFields = new String[]{"title", "desc1", "desc2"};
        mList = new ArrayList<HashMap<String, String>>();

        SimpleAdapter mAdapter = new SimpleAdapter(
                this,
                mList,
                R.layout.activity_settings_row,
                mRowFields,
                new int[] {R.id.settings_title, R.id.settings_desc1, R.id.settings_desc2}

        );
        fillSettingsList();

        mSettingsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO remove after implements actions
                Toast.makeText(view.getContext(), "You have chosen the " + position
                        + " position on the list", Toast.LENGTH_SHORT).show();
            }
        });
        mSettingsListView.setAdapter(mAdapter);
    }

    private void fillSettingsList() {
        long size = 0;
        String filename = Environment.getExternalStorageDirectory() + "/OpenMRS/OpenMRS.log";
        try {
            File file = new File(filename);
            size = file.length();
            size = size / 1024;
            Logger.i("File Path : " + file.getPath() + ", File size: " + size + " KB");
        } catch (Exception e) {
            Logger.w("File not found");
        }

        HashMap<String, String> temp = new HashMap<String, String>();
        temp.put(mRowFields[0], "Logs");
        temp.put(mRowFields[1], filename);
        if (size != 0) {
            temp.put(mRowFields[2], "Size: " + size + "kB");
        }
        mList.add(temp);

        temp = new HashMap<String, String>();
        temp.put(mRowFields[0], "About");
        temp.put(mRowFields[1], getResources().getString(R.string.app_name));
        temp.put(mRowFields[2], "Version: 0.1");  //TODO get version from manifest
        mList.add(temp);

        temp = new HashMap<String, String>();
        temp.put(mRowFields[0], "Logout");
        mList.add(temp);
    }
}
