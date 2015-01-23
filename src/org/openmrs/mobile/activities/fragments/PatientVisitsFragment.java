package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.adapters.PatientVisitsArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class PatientVisitsFragment extends ACBaseFragment {

    private List<Visit> mPatientVisits;
    private Patient mPatient;
    private VisitsManager mVisitsManager;

    public PatientVisitsFragment() {
    }

    public static PatientVisitsFragment newInstance(Patient patient) {
        PatientVisitsFragment detailsFragment = new PatientVisitsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patient);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVisitsManager = new VisitsManager();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patients_visit_tab_menu, menu);

        if (!OpenMRS.getInstance().getOnlineMode()) {
            menu.findItem(R.id.actionStartVisit).setTitle(R.string.action_start_visit_offline);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionStartVisit:
                showStartVisitDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE);
        mPatientVisits = new VisitDAO().getVisitsByPatientID(((Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE)).getId());

        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_visit, null, false);
        ListView visitList = (ListView) fragmentLayout.findViewById(R.id.patientVisitList);

        TextView emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyVisitsListView);
        visitList.setEmptyView(emptyList);


        visitList.setAdapter(new PatientVisitsArrayAdapter(getActivity(), mPatientVisits));
        return fragmentLayout;
    }

    public void startVisit() {
        ((PatientDashboardActivity) getActivity())
                .showProgressDialog(R.string.action_start_visit, PatientDashboardActivity.DialogAction.ADD_VISIT);
        mVisitsManager.checkVisitBeforeStart(
                VisitsHelper.createCheckVisitsBeforeStartListener(mPatient.getUuid(), mPatient.getId(), (PatientDashboardActivity) getActivity()));
    }

    private void showStartVisitDialog() {
        PatientDashboardActivity caller =  (PatientDashboardActivity) getActivity();
        if (new VisitDAO().isPatientNowOnVisit(mPatientVisits)) {
            caller.showStartVisitImpossibleDialog(caller.getSupportActionBar().getTitle());
        } else {
            caller.showStartVisitDialog(caller.getSupportActionBar().getTitle());
        }
    }
}
