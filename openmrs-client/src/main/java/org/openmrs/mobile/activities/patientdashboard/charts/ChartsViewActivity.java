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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.DateUtils;
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
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.DayAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChartsViewActivity extends ACBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.charts_view_toolbar_title));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle mBundle = this.getIntent().getBundleExtra(ApplicationConstants.BUNDLE);
        try {
            JSONObject chartData = new JSONObject(mBundle.getString(ApplicationConstants.VITAL_NAME));
            Iterator<String> dates = chartData.keys();
            ArrayList<String> dateList = Lists.newArrayList(dates);
            //Sorting the date
            Collections.sort(dateList, (lhs, rhs) -> {
                if (DateUtils.getDateFromString(lhs).getTime() < DateUtils.getDateFromString(rhs).getTime()) {
                    return -1;
                } else if (DateUtils.getDateFromString(lhs).getTime() == DateUtils.getDateFromString(rhs).getTime()) {
                    return 0;
                } else {
                    return 1;
                }
            });
            for (Integer j = 0; j < dateList.size(); j++) {
                JSONArray dataArray = chartData.getJSONArray(dateList.get(j));
                LineChart chart = findViewById(R.id.linechart);
                List<Entry> entries = new ArrayList<>();
                for (Integer i = 0; i < dataArray.length(); i++) {
                    entries.add(new Entry(j, Float.parseFloat((String) dataArray.get(i))));
                }
                LineDataSet dataSet = new LineDataSet(entries, getString(R.string.dataset_entry_label)); // add entries to dataset
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
                xAxis.setAxisMaximum(dateList.size() - 1);
                xAxis.setValueFormatter(new DayAxisValueFormatter(dateList));

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                chart.invalidate();
            }
        } catch (JSONException e) {
            OpenmrsAndroid.getOpenMRSLogger().e(e.toString());
        } catch (NumberFormatException e) {
            OpenmrsAndroid.getOpenMRSLogger().e(e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                return true;
        }
        return true;
    }
}
