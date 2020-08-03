/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.addallergy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.databinding.FragmentAllergyInfoBinding;
import org.openmrs.mobile.databinding.FragmentPatientAllergyBinding;

public class AddEditAllergyFragment extends ACBaseFragment<AddEditAllergyContract.Presenter> implements AddEditAllergyContract.View {
    private FragmentAllergyInfoBinding patientAllergyBinding;

    public static AddEditAllergyFragment newInstance() {
        return new AddEditAllergyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        patientAllergyBinding = FragmentAllergyInfoBinding.inflate(inflater, container, false);
        View root = patientAllergyBinding.getRoot();
        return root;
    }

    @Override
    public void showSuccess(String nameString) {
        patientAllergyBinding.textview.setText(nameString);
    }
}
