package org.openmrs.mobile.activities.patientdashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class PatientDashboardFragment extends Fragment implements PatientDashboardContract.ViewPatientMain {

    PatientDashboardContract.PatientDashboardMainPresenter mPresenter;

    @Override
    public void setPresenter(PatientDashboardContract.PatientDashboardMainPresenter presenter) {
        this.mPresenter = presenter;
    }

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
