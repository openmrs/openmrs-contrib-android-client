package org.openmrs.client.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.VisitExpandableListAdapter;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Visit;
import org.openmrs.client.models.VisitItemDTO;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class VisitDashboardActivity extends ACBaseActivity {

    private ExpandableListView mExpandableListView;
    private VisitExpandableListAdapter mExpandableListAdapter;
    private List<Encounter> mVisitEncounters;
    private VisitItemDTO mVisitItemDTO;
    private TextView mEmptyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_dashboard);

        Bundle bundle = getIntent().getExtras();
        mVisitItemDTO = (VisitItemDTO) bundle.getSerializable(ApplicationConstants.BundleKeys.VISIT_ITEM);

        Visit visit = new VisitDAO().getVisitsByID(mVisitItemDTO.getVisitID());
        mVisitEncounters = visit.getEncounters();

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
        getSupportActionBar().setSubtitle(mVisitItemDTO.getPatientName());
        return true;
    }
}
