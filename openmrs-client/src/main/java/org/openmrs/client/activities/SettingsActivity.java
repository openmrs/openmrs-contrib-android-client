package org.openmrs.client.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;

import java.io.File;

public class SettingsActivity extends ACBaseActivity {

    private ListView mSettingsListView;
    private String[] mList;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logger.d("Started onCreate SettingsActivity");
        mList = new String[] {"Logs", "About", "Logout"};
        mSettingsListView = (ListView) findViewById(R.id.settingsListView);
        SettingsArrayAdapter mAdapter = new SettingsArrayAdapter(this, mList);
        mSettingsListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class SettingsArrayAdapter extends ArrayAdapter<String> {
        private Activity context;
        private String[] names;

        class ViewHolder {
            private TextView title;
            private TextView desc1;
            private TextView desc2;
        }

        public SettingsArrayAdapter(Activity context, String[] values) {
            super(context, R.layout.activity_settings_row, values);
            this.context = context;
            this.names = values.clone();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.activity_settings_row, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) rowView.findViewById(R.id.settings_title);
                viewHolder.desc1 = (TextView) rowView.findViewById(R.id.settings_desc1);
                viewHolder.desc2 = (TextView) rowView.findViewById(R.id.settings_desc2);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            String s = names[position];
            holder.title.setText(s);

            if (s.startsWith("Logs")) {
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

                holder.desc1.setText(filename);

                if (size != 0) {
                    holder.desc2.setText("Size: " + size + "kB");
                }

            } else if (s.startsWith("About")) {
                holder.desc1.setText(getResources().getString(R.string.app_name));
                holder.desc2.setText("Version: 0.1");  //TODO get version from manifest

            }

            return rowView;
        }
    }
}
