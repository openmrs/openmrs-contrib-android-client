package org.openmrs.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.Visit;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.StringUtils;

import java.util.List;

public class ActiveVisitsArrayAdapter extends ArrayAdapter<List<Visit>> {

    private Context mContext;
    private int mResourceID;
    private List<Visit> mVisitList;

    public ActiveVisitsArrayAdapter(Context context, int resource, List<Visit> items) {
        super(context, resource);
        this.mContext = context;
        this.mResourceID = resource;
        this.mVisitList = items;
    }

    class ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mPatientID;
        private TextView mPatientName;
        private TextView mVisitPlace;
        private TextView mVisitDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResourceID, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mRowLayout = (LinearLayout) rowView;
            viewHolder.mPatientID = (TextView) rowView.findViewById(R.id.visitPatientID);
            viewHolder.mPatientName = (TextView) rowView.findViewById(R.id.visitPatientName);
            viewHolder.mVisitPlace = (TextView) rowView.findViewById(R.id.visitPlace);
            viewHolder.mVisitDate = (TextView) rowView.findViewById(R.id.visitStartDate);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Visit visit = mVisitList.get(position);
        holder.mPatientID.setText("#" + String.valueOf(visit.getPatientID()));
        holder.mPatientName.setText("TODO");
        holder.mVisitDate.setText(DateUtils.convertTime(visit.getStartDate()));
        holder.mVisitPlace.setText(visit.getDisplay());
        return rowView;
    }
}
