package org.openmrs.mobile.activities.patientdashboard.charts;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

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
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.DateUtils;
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
        Bundle mBundle = this.getIntent().getBundleExtra("bundle");
        try {
            JSONObject chartData = new JSONObject(mBundle.getString("vitalName"));
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
                LineChart chart = findViewById(R.id.linechart);
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
                xAxis.setAxisMaximum(dateList.size() - 1);
                xAxis.setValueFormatter(new DayAxisValueFormatter(dateList));

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);


                chart.invalidate();
            }
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().e(e.toString());
        } catch (NumberFormatException e) {
            OpenMRS.getInstance().getOpenMRSLogger().e(e.toString());
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
