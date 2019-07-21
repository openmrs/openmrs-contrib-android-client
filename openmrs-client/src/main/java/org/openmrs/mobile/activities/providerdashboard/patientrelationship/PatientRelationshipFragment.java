package org.openmrs.mobile.activities.providerdashboard.patientrelationship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openmrs.mobile.R;

public class PatientRelationshipFragment extends Fragment {

    public PatientRelationshipFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_relationship,null);
        return root;
    }
}
