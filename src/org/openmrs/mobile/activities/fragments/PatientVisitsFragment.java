package org.openmrs.mobile.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.activities.VisitDashboardActivity;
import org.openmrs.mobile.adapters.PatientVisitsArrayAdapter;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class PatientVisitsFragment extends ACBaseFragment {

    private List<Visit> mPatientVisits;
    private VisitsManager mVisitsManager;
    private Patient mPatient;

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
        mVisitsManager = new VisitsManager(getActivity());
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE);
        mPatientVisits = new VisitDAO().getVisitsByPatientID(((Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE)).getId());

        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_visit, null, false);
        ListView visitList = (ListView) fragmentLayout.findViewById(R.id.patientVisitList);
        visitList.setAdapter(new PatientVisitsArrayAdapter(getActivity(), mPatientVisits));
        return fragmentLayout;
    }

    public void startVisit() {
        ((PatientDashboardActivity) getActivity())
                .showProgressDialog(R.string.action_start_visit, PatientDashboardActivity.DialogAction.ADD_VISIT);
        mVisitsManager.createVisit(mPatient);

    }

    public void visitStarted(long visitID, boolean errorOccurred) {
        ((PatientDashboardActivity) getActivity()).stopLoader(errorOccurred);
        if (!errorOccurred) {
            Intent intent = new Intent(getActivity(), VisitDashboardActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, visitID);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_NAME, mPatient.getDisplay());
            this.startActivity(intent);

        }
    }

}
