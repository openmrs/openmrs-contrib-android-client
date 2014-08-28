package org.openmrs.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.Patient;

import java.util.List;

public class FindPatientArrayAdapter extends ArrayAdapter<Patient> {
    private Activity mContext;
    private List<Patient> mItems;

    class ViewHolder {
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;
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
            viewHolder.mIdentifier = (TextView) rowView.findViewById(R.id.patientIdentifier);
            viewHolder.mDisplayName = (TextView) rowView.findViewById(R.id.patientDisplayName);
            viewHolder.mGender = (TextView) rowView.findViewById(R.id.patientGender);
            viewHolder.mAge = (TextView) rowView.findViewById(R.id.patientAge);
            viewHolder.mBirthDate = (TextView) rowView.findViewById(R.id.patientBirthDate);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Patient patient = mItems.get(position);
        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier());
        }
        if (null != patient.getDisplay()) {
            holder.mDisplayName.setText(patient.getDisplay());
        }
        if (null != patient.getGender()) {
            holder.mGender.setText(patient.getGender());
        }
        if (null != patient.getAge()) {
            holder.mAge.setText(patient.getAge());
        }
        String birthDate = patient.getBirthDate();
        if (null != birthDate) {
            holder.mBirthDate.setText(birthDate.substring(0, birthDate.indexOf('T')));
        }
        return rowView;
    }
}
