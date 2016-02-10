package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.mobile.R;

import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientDiagnosisFragment extends ACBaseFragment {

    private List<Encounter> mVisitNoteEncounters;

    public PatientDiagnosisFragment() {
    }

    public static PatientDiagnosisFragment newInstance(Long patientID) {
        PatientDiagnosisFragment detailsFragment = new PatientDiagnosisFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patientID);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVisitNoteEncounters = new EncounterDAO().getAllEncountersByType(
                getArguments().getLong(ApplicationConstants.BundleKeys.PATIENT_BUNDLE),
                Encounter.EncounterType.VISIT_NOTE);
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_diagnosis, null, false);
        ListView visitList = (ListView) fragmentLayout.findViewById(R.id.patientDiagnosisList);

        TextView emptyList = (TextView) fragmentLayout.findViewById(R.id.emptyDiagnosisListView);
        visitList.setEmptyView(emptyList);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getAllDiagnosis(mVisitNoteEncounters));
        visitList.setAdapter(adapter);

        FontsUtil.setFont(fragmentLayout, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);
        return fragmentLayout;
    }

    private List<String> getAllDiagnosis(List<Encounter> encounters) {
        List<String> diagnosis = new ArrayList<String>();

        for (Encounter encounter : encounters) {
            for (Observation obs : encounter.getObservations()) {
                if (obs.getDiagnosisList() != null
                        && !obs.getDiagnosisList().equals(ApplicationConstants.EMPTY_STRING)
                        && !diagnosis.contains(obs.getDiagnosisList())) {
                    diagnosis.add(obs.getDiagnosisList());
                }
            }
        }

        return diagnosis;
    }
}
