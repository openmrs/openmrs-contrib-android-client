package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.VisitExpandableListAdapter;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Visit;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class VisitDashboardActivity extends ACBaseActivity {

    private ExpandableListView mExpandableListView;
    private VisitExpandableListAdapter mExpandableListAdapter;
    private List<Encounter> mVisitEncounters;
    private TextView mEmptyListView;
    private Visit mvisit;
    private String mPatientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_dashboard);

        Intent intent = getIntent();
        mvisit = new VisitDAO().getVisitsByID(intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0));
        mPatientName = intent.getStringExtra(ApplicationConstants.BundleKeys.PATIENT_NAME);
        mVisitEncounters = mvisit.getEncounters();

        mEmptyListView = (TextView) findViewById(R.id.visitDashboardEmpty);
        FontsUtil.setFont(mEmptyListView, FontsUtil.OpenFonts.OPEN_SANS_BOLD);
        mExpandableListView = (ExpandableListView) findViewById(R.id.visitDashboardExpList);
        mExpandableListView.setEmptyView(mEmptyListView);
    }

    @Override
    protected void onResume() {
        if (!mVisitEncounters.isEmpty()) {
            mEmptyListView.setVisibility(View.GONE);
            mExpandableListAdapter = new VisitExpandableListAdapter(this, mVisitEncounters);
            mExpandableListView.setAdapter(mExpandableListAdapter);
            mExpandableListView.setGroupIndicator(null);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setSubtitle(mPatientName);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
