package org.openmrs.client.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.openmrs.client.R;
import org.openmrs.client.activities.PatientDashboardActivity;
import org.openmrs.client.activities.VisitDashboardActivity;
import org.openmrs.client.adapters.PatientVisitsArrayAdapter;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;
import org.openmrs.client.net.VisitsManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;

import java.util.List;

public class PatientVisitsFragment extends Fragment {

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
        mVisitsManager = new VisitsManager(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patients_visit_tab_menu, menu);
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
        mPatientVisits = new VisitDAO().getVisitsByPatientID(mPatient.getId());

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

    private void showStartVisitDialog() {
        boolean activeVisit = false;
        for (int i = 0; i < mPatientVisits.size(); i++) {
            if (DateUtils.ZERO.equals(mPatientVisits.get(i).getStopDate())) {
                activeVisit = true;
                break;
            }
        }
        if (!activeVisit) {
            CustomDialogBundle bundle = new CustomDialogBundle();
            bundle.setTitleViewMessage(getString(R.string.start_visit_dialog_title));
            bundle.setTextViewMessage(getString(R.string.start_visit_dialog_message,
                    ((PatientDashboardActivity) getActivity()).getSupportActionBar().getTitle()));
            bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.START_VISIT);
            bundle.setRightButtonText(getString(R.string.dialog_button_confirm));
            bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
            bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
            ((PatientDashboardActivity) getActivity()).
                    createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_DIALOG_TAG);
        } else {
            CustomDialogBundle bundle = new CustomDialogBundle();
            bundle.setTitleViewMessage(getString(R.string.start_visit_impossible_dialog_title));
            bundle.setTextViewMessage(getString(R.string.start_visit_impossible_dialog_message,
                    ((PatientDashboardActivity) getActivity()).getSupportActionBar().getTitle()));
            bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
            bundle.setRightButtonText(getString(R.string.dialog_button_ok));
            ((PatientDashboardActivity) getActivity()).
                    createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_IMPOSSIBLE_DIALOG_TAG);
        }
    }
}
