package org.openmrs.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.Patient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class FindPatientArrayAdapter extends ArrayAdapter<Patient> {
    private Activity mContext;
    private List<Patient> mItems;

    class ViewHolder {
        private TextView mPatientName;
        private TextView mPatientData;
    }

    public FindPatientArrayAdapter(Activity context, List<Patient> items) {
        super(context, R.layout.activity_find_patients_row, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.activity_find_patients_row, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mPatientName = (TextView) rowView.findViewById(R.id.find_patient_name);
            viewHolder.mPatientData = (TextView) rowView.findViewById(R.id.find_patient_data);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Patient patient = mItems.get(position);
        holder.mPatientName.setText("#" + patient.getDisplay());
        if (patient.getGender() != null) {
            holder.mPatientData.setText(" | " + patient.getGender());
        }
        if (patient.getBirthDate() != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            holder.mPatientData.setText(holder.mPatientData.getText()
                    + " | " + df.format(patient.getBirthDate()));
        }
        return rowView;
    }
}
