package org.openmrs.mobile.activities.matchingPatients;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.PatientAndMatchesWrapper;
import org.openmrs.mobile.utilities.PatientAndMatchingPatients;

import java.util.ArrayList;

public class MatchingPatientsActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.matching_patients_toolbar_tittle));
            setSupportActionBar(toolbar);
        }

        // Create fragment
        MatchingPatientsFragment matchingPatientsFragment =
                (MatchingPatientsFragment) getSupportFragmentManager().findFragmentById(R.id.matchingPatientsContentFrame);
        if (matchingPatientsFragment == null) {
            matchingPatientsFragment = MatchingPatientsFragment.newInstance();
        }
        if (!matchingPatientsFragment.isAdded()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    matchingPatientsFragment, R.id.matchingPatientsContentFrame);
        }

        PatientAndMatchesWrapper patientAndMatchesWrapper = (PatientAndMatchesWrapper) getIntent().getSerializableExtra("PATIENTS_AND_MATCHES");

        // Create the presenter
        new MatchingPatientsPresenter(matchingPatientsFragment, patientAndMatchesWrapper.getMatchingPatients());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sync", false);
        editor.commit();
    }

}
