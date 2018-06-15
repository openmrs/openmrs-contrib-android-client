package org.openmrs.mobile.activities.patientdashboard.charts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.json.JSONObject;
import org.openmrs.mobile.R;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Chathuranga on 15/06/2018.
 */

public class PatientChartsListAdapter extends BaseAdapter {
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private Context mContext;
    private JSONObject mObservationList;
    private List<String> mVitalNameList;

    PatientChartsListAdapter(Context context, JSONObject observationList) {
        this.mContext = context;
        this.mObservationList = observationList;
        Iterator<String> keys = mObservationList.keys();
        this.mVitalNameList = Lists.newArrayList(keys);
    }

    @Override
    public int getCount() {
        return mVitalNameList.size();
    }

    @Override
    public String getItem(int i) {
        return mVitalNameList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        View rowView = convertView;
        if (null == rowView) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_vital_group, null);
            holder.vitalName = rowView.findViewById(R.id.listVisitGroupVitalName);
            holder.detailsSelector = rowView.findViewById(R.id.listVisitGroupDetailsSelector);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        String vitalLabel = String.valueOf(mVitalNameList.get(i));
        holder.vitalName.setText(vitalLabel);


        holder.detailsSelector.setText(mContext.getString(R.string.list_vital_selector_show));
        bindDrawableResources(R.drawable.exp_list_show_details, holder.detailsSelector, RIGHT);

        return rowView;
    }


    private static class ViewHolder {
        private TextView vitalName, detailsSelector;

    }

    private void bindDrawableResources(int drawableID, TextView textView, int direction) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        Drawable image = mContext.getResources().getDrawable(drawableID);
        if (direction == LEFT) {
            image.setBounds(0, 0, (int) (40 * scale + 0.5f), (int) (40 * scale + 0.5f));
            textView.setCompoundDrawablePadding((int) (13 * scale + 0.5f));
            textView.setCompoundDrawables(image, null, null, null);
        } else {
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            textView.setCompoundDrawablePadding((int) (10 * scale + 0.5f));
            textView.setCompoundDrawables(null, null, image, null);
        }
    }
}
