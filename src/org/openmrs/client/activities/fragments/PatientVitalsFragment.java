package org.openmrs.client.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.application.OpenMRSInflater;
import org.openmrs.client.dao.EncounterDAO;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Observation;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.FontsUtil;

public class PatientVitalsFragment extends Fragment {

    private Encounter mVitalsEncounter;

    public PatientVitalsFragment() {
    }

    public static PatientVitalsFragment newInstance(Long patientID) {
        PatientVitalsFragment patientVitalsFragment = new PatientVitalsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patientID);
        patientVitalsFragment.setArguments(bundle);
        return patientVitalsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVitalsEncounter = new EncounterDAO().getLastEncounterForVisitByType(
                getArguments().getLong(ApplicationConstants.BundleKeys.PATIENT_BUNDLE),
                Encounter.EncounterType.VITALS);
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_vitals, null, false);
        LinearLayout content = (LinearLayout) fragmentLayout.findViewById(R.id.vitalsDetailsContent);
        TextView lastVitalsLabel = (TextView) fragmentLayout.findViewById(R.id.lastVitalsLabel);
        if (null == mVitalsEncounter) {
            lastVitalsLabel.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            TextView noneVitals = (TextView) fragmentLayout.findViewById(R.id.lastVitalsNoneLabel);
            noneVitals.setVisibility(View.VISIBLE);
            noneVitals.setText(getString(R.string.last_vitals_none_label));
            FontsUtil.setFont(noneVitals, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
        } else {
            TextView lastVitalsDate = (TextView) fragmentLayout.findViewById(R.id.lastVitalsDate);
            lastVitalsDate.setText(DateUtils.convertTime(mVitalsEncounter.getEncounterDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
            FontsUtil.setFont(lastVitalsLabel, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
            FontsUtil.setFont(lastVitalsDate, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);
            OpenMRSInflater openMRSInflater = new OpenMRSInflater(inflater);
            for (Observation obs : mVitalsEncounter.getObservations()) {
                openMRSInflater.addKeyValueStringView(content, obs.getDisplay(), obs.getDisplayValue());
            }
        }
        return fragmentLayout;
    }

}
