package org.openmrs.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.SettingsListItemDTO;

import java.util.List;

public class SettingsArrayAdapter extends ArrayAdapter<SettingsListItemDTO> {
    private Activity context;
    private List<SettingsListItemDTO> items;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();
    private OpenMRSLogger logger = mOpenMRS.getOpenMRSLogger();

    class ViewHolder {
        private TextView title;
        private TextView desc1;
        private TextView desc2;
    }

    public SettingsArrayAdapter(Activity context, List<SettingsListItemDTO> items) {
        super(context, R.layout.activity_settings_row, items);
        this.context = context;
        this.items = items;
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

        SettingsListItemDTO s = items.get(position);

        holder.title.setText(s.getTitle());

        if (s.getTitle().startsWith(context.getResources().getString(R.string.settings_logs))) {
            holder.desc1.setText(s.getDesc1());
            if (Long.valueOf(s.getDesc2()) != 0) {
                holder.desc2.setText("Size: " + s.getDesc2() + "kB");
            }
        } else if (s.getTitle().startsWith(context.getResources().getString(R.string.settings_about))) {
            holder.desc1.setText(s.getDesc1());
            holder.desc2.setText(s.getDesc2());
        }

        return rowView;
    }
}