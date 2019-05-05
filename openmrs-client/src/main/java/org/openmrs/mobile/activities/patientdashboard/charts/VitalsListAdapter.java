/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.patientdashboard.charts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.DayAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VitalsListAdapter extends BaseExpandableListAdapter {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private Context mContext;
    private List<ViewGroup> mChildLayouts;
    private JSONObject mObservationList;
    private List<String> mVitalNameList;

    public VitalsListAdapter(Context context, JSONObject observationList) {
        this.mContext = context;
        this.mObservationList = observationList;
        Iterator<String> keys = mObservationList.keys();
        this.mVitalNameList = Lists.newArrayList(keys);
        this.mChildLayouts = generateChildLayouts();
    }


    private List<ViewGroup> generateChildLayouts() {
        List<ViewGroup> layouts = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        for (String vitalName : this.mVitalNameList) {
            ViewGroup convertView = (ViewGroup) inflater.inflate(R.layout.line_chart, null);
            try {
                JSONObject chartData = mObservationList.getJSONObject(vitalName);
                Iterator<String> dates = chartData.keys();
                ArrayList<String> dateList = Lists.newArrayList(dates);
                //Sorting the date
                Collections.sort(dateList, (lhs, rhs) -> {
                    if (DateUtils.getDateFromString(lhs).getTime() < DateUtils.getDateFromString(rhs).getTime())
                        return -1;
                    else if (DateUtils.getDateFromString(lhs).getTime() == DateUtils.getDateFromString(rhs).getTime())
                        return 0;
                    else
                        return 1;
                });
                for (Integer j = 0; j < dateList.size(); j++) {
                    JSONArray dataArray = chartData.getJSONArray(dateList.get(j));
                    LineChart chart = convertView.findViewById(R.id.linechart);
                    List<Entry> entries = new ArrayList<>();
                    for (Integer i = 0; i < dataArray.length(); i++) {
                        entries.add(new Entry(j, Float.parseFloat((String) dataArray.get(i))));
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                    dataSet.setCircleColor(R.color.green);
                    dataSet.setValueTextSize(12);
                    List<ILineDataSet> ILdataSet = new ArrayList<>();
                    ILdataSet.add(dataSet);
                    dateList.add(DateUtils.getCurrentDateTime());
                    LineData lineData = new LineData(ILdataSet);
                    chart.setData(lineData);
                    //Styling the graph
                    chart.getLegend().setEnabled(false);
                    chart.getDescription().setEnabled(false);
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawAxisLine(true);
                    xAxis.setGranularity(1);
                    xAxis.setAxisMinimum(0);
                    xAxis.setAxisMaximum(dateList.size() -1);
                    xAxis.setValueFormatter(new DayAxisValueFormatter(dateList));

                    YAxis rightAxis = chart.getAxisRight();
                    rightAxis.setEnabled(false);


                    chart.invalidate();
                    layouts.add(convertView);
                }
            } catch (JSONException e) {
                OpenMRS.getInstance().getOpenMRSLogger().e(e.toString());
            } catch (NumberFormatException e) {
                OpenMRS.getInstance().getOpenMRSLogger().e(e.toString());
            }

        }

        return layouts;
    }


    @Override
    public int getGroupCount() {
        return mVitalNameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mVitalNameList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildLayouts.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_vital_group, null);
        }
        final TextView vitalName = rowView.findViewById(R.id.listVisitGroupVitalName);
        final TextView detailsSelector = rowView.findViewById(R.id.listVisitGroupDetailsSelector);
        String vitalLabel = String.valueOf(mVitalNameList.get(groupPosition));
        vitalName.setText(vitalLabel);
        if (isExpanded) {
            detailsSelector.setText(mContext.getString(R.string.list_vital_selector_hide));
            bindDrawableResources(R.drawable.exp_list_hide_details, detailsSelector, RIGHT);
        } else {
            detailsSelector.setText(mContext.getString(R.string.list_vital_selector_show));
            bindDrawableResources(R.drawable.exp_list_show_details, detailsSelector, RIGHT);
        }
        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return (ViewGroup) getChild(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
