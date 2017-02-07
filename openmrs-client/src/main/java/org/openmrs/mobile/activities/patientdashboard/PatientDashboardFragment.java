package org.openmrs.mobile.activities.patientdashboard;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.PatientDashboardMainPresenter> implements PatientDashboardContract.ViewPatientMain {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patient_dashboard_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionDelete:
                ((ACBaseActivity) this.getActivity()).showDeletePatientDialog();
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static PatientDashboardFragment newInstance() {
        return new PatientDashboardFragment();
    }
}
