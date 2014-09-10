package org.openmrs.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.Visit;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.ImageUtils;

import java.util.List;

public class PatientVisitsArrayAdapter extends ArrayAdapter<Visit> {
    private Context mContext;
    private List<Visit> mVisits;

    public PatientVisitsArrayAdapter(Context context, List<Visit> items) {
        super(context, R.layout.patient_visit_row, items);
        this.mContext = context;
        this.mVisits = items;
    }

    class ViewHolder {
        private TextView mVisitPlace;
        private TextView mVisitStart;
        private TextView mVisitEnd;
        private TextView mVisitStatus;
        private ImageView mVisitStatusIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.patient_visit_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mVisitStart = (TextView) rowView.findViewById(R.id.patientVisitStartDate);
            viewHolder.mVisitEnd = (TextView) rowView.findViewById(R.id.patientVisitEndDate);
            viewHolder.mVisitPlace = (TextView) rowView.findViewById(R.id.patientVisitPlace);
            viewHolder.mVisitStatusIcon = (ImageView) rowView.findViewById(R.id.visitStatusIcon);
            viewHolder.mVisitStatus = (TextView) rowView.findViewById(R.id.visitStatusLabel);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        Visit visit = mVisits.get(position);
        holder.mVisitStart.setText(DateUtils.convertTime(visit.getStartDate(), DateUtils.DATE_WITH_TIME_FORMAT));
        if (!DateUtils.ZERO.equals(visit.getStopDate())) {
            holder.mVisitEnd.setVisibility(View.VISIBLE);
            holder.mVisitEnd.setText(DateUtils.convertTime(visit.getStopDate(), DateUtils.DATE_WITH_TIME_FORMAT));

            holder.mVisitStatusIcon.setImageBitmap(
            ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.past_visit_dot,
                    holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.past_visit_label));
        } else {
            holder.mVisitStatusIcon.setImageBitmap(
                    ImageUtils.decodeBitmapFromResource(mContext.getResources(), R.drawable.active_visit_dot,
                    holder.mVisitStatusIcon.getLayoutParams().width, holder.mVisitStatusIcon.getLayoutParams().height));
            holder.mVisitStatus.setText(mContext.getString(R.string.active_visit_label));
        }
        holder.mVisitPlace.setText(mContext.getString(R.string.visit_in, visit.getVisitPlace()));
        return rowView;
    }
}
