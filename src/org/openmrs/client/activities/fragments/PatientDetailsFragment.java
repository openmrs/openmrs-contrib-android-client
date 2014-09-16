package org.openmrs.client.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.models.Patient;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.StringUtils;
import org.openmrs.client.utilities.FontsUtil;

public class PatientDetailsFragment extends Fragment {

    private Patient mPatient;

    public PatientDetailsFragment() {
    }

    public static PatientDetailsFragment newInstance(Patient patient) {
        PatientDetailsFragment detailsFragment = new PatientDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE, patient);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(ApplicationConstants.BundleKeys.PATIENT_BUNDLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_patient_details, null, false);
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsDisplayName)).setText(mPatient.getDisplay());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsIdentifier)).setText("#" + mPatient.getIdentifier());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsGender)).setText(mPatient.getGender());
        ((TextView) fragmentLayout.findViewById(R.id.patientDetailsBirthDate)).setText(DateUtils.convertTime(mPatient.getBirthDate()));
        if (null != mPatient.getAddress()) {
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.addressLayout), R.id.addressDetailsStreet, mPatient.getAddress().toString());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.stateLayout), R.id.addressDetailsState, mPatient.getAddress().getState());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.countryLayout), R.id.addressDetailsCountry, mPatient.getAddress().getCountry());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.postalCodeLayout), R.id.addressDetailsPostalCode, mPatient.getAddress().getPostalCode());
            showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.cityLayout), R.id.addressDetailsCity, mPatient.getAddress().getCityVillage());
        }
        showAddressDetailsViewElement(fragmentLayout.findViewById(R.id.phoneNumberLayout), R.id.patientDetailsPhone, mPatient.getPhoneNumber());

        FontsUtil.setFont((ViewGroup) fragmentLayout);
        return fragmentLayout;
    }

    private void showAddressDetailsViewElement(View detailsLayout, int detailsViewId, String detailsText) {
        if (StringUtils.notNull(detailsText) && StringUtils.notEmpty(detailsText)) {
            ((TextView) detailsLayout.findViewById(detailsViewId)).setText(detailsText);
        } else {
            detailsLayout.setVisibility(View.GONE);
        }
    }
}
