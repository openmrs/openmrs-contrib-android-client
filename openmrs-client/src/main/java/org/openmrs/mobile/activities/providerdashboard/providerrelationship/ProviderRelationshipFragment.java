package org.openmrs.mobile.activities.providerdashboard.providerrelationship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.openmrs.mobile.R;

public class ProviderRelationshipFragment extends Fragment {

    public ProviderRelationshipFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_provider_relationship,null);
        return root;
    }
}
