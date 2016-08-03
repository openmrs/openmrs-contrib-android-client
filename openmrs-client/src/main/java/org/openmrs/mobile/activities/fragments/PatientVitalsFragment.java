package org.openmrs.mobile.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientVitalsFragment extends ACBaseFragment {

    private Encounter mVitalsEncounter;

    public PatientVitalsFragment() {
    }

    public static PatientVitalsFragment newInstance(String patientUUID) {
        PatientVitalsFragment patientVitalsFragment = new PatientVitalsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUUID);
        patientVitalsFragment.setArguments(bundle);
        return patientVitalsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_vitals, null, false);
        LinearLayout content = (LinearLayout) fragmentLayout.findViewById(R.id.vitalsDetailsContent);
        TextView lastVitalsLabel = (TextView) fragmentLayout.findViewById(R.id.lastVitalsLabel);
        String patientUUID = getArguments().getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);

        mVitalsEncounter = new EncounterDAO().getLastVitalsEncounter(patientUUID);

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
