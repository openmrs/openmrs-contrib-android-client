package org.openmrs.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.activities.VisitDashboardActivity;
import org.openmrs.client.models.VisitItemDTO;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class ActiveVisitsArrayAdapter extends ArrayAdapter<VisitItemDTO> {

    private Context mContext;
    private int mResourceID;
    private List<VisitItemDTO> mVisitList;

    public ActiveVisitsArrayAdapter(Context context, int resource, List<VisitItemDTO> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mResourceID = resource;
        this.mVisitList = items;
    }

    class ViewHolder {
        private RelativeLayout mRelativeLayout;
        private TextView mPatientID;
        private TextView mPatientName;
        private TextView mVisitPlace;
        private TextView mVisitType;
        private TextView mVisitStart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResourceID, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mRelativeLayout = (RelativeLayout) rowView.findViewById(R.id.visitRow);
            viewHolder.mPatientID = (TextView) rowView.findViewById(R.id.visitPatientID);
            viewHolder.mPatientName = (TextView) rowView.findViewById(R.id.visitPatientName);
            viewHolder.mVisitPlace = (TextView) rowView.findViewById(R.id.patientVisitPlace);
            viewHolder.mVisitType = (TextView) rowView.findViewById(R.id.visitType);
            viewHolder.mVisitStart = (TextView) rowView.findViewById(R.id.patientVisitStartDate);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final VisitItemDTO visit = mVisitList.get(position);
        holder.mPatientName.setText(visit.getPatientName());
        holder.mPatientID.setText("#" + String.valueOf(visit.getPatientIdentifier()));
        holder.mVisitType.setText(visit.getVisitType());
        holder.mVisitPlace.setText("@ " + visit.getVisitPlace());
        holder.mPatientName.setText(visit.getPatientName());
        holder.mVisitStart.setText(DateUtils.convertTime(visit.getVisitStart()));
        FontsUtil.setFont((ViewGroup) rowView);

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VisitDashboardActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ITEM, mVisitList.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        return rowView;
    }
}
