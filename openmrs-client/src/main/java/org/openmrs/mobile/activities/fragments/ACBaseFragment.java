package org.openmrs.mobile.activities.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class ACBaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}
