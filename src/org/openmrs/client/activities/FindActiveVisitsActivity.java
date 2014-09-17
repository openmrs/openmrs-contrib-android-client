package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.ActiveVisitsArrayAdapter;
import org.openmrs.client.dao.VisitDAO;

public class FindActiveVisitsActivity extends ACBaseActivity {
    private ActiveVisitsArrayAdapter mAdapter;
    private ListView mVisitListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visits);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVisitListView = (ListView) findViewById(R.id.visits_list_view);
        TextView emptyList = (TextView) findViewById(R.id.empty_visits_list_view);
        emptyList.setText(getString(R.string.search_visits_no_results));
        mVisitListView.setEmptyView(emptyList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new ActiveVisitsArrayAdapter(this, R.layout.find_visits_row, new VisitDAO().getAllActiveVisits());
        mVisitListView.setAdapter(mAdapter);
    }

}
