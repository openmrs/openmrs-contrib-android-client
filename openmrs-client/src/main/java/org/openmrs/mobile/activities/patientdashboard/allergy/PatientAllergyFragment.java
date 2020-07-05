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

package org.openmrs.mobile.activities.patientdashboard.allergy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.databinding.FragmentPatientAllergyBinding;
import org.openmrs.mobile.models.Allergy;

import java.util.List;

public class PatientAllergyFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientAllergy {
    private PatientDashboardActivity mPatientDashboardActivity;
    private FragmentPatientAllergyBinding binding;

    public static PatientAllergyFragment newInstance() {
        return new PatientAllergyFragment();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mPatientDashboardActivity = (PatientDashboardActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientAllergyBinding.inflate(inflater, container, false);
        ((PatientDashboardAllergyPresenter) mPresenter).getAllergy(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewAllergy.setHasFixedSize(true);
        binding.recyclerViewAllergy.setLayoutManager(linearLayoutManager);

        return binding.getRoot();
    }

    @Override
    public void showAllergyList(List<Allergy> allergies) {
        binding.progressBar.setVisibility(View.GONE);
        if (allergies == null) {
            binding.emptyAllergyList.setVisibility(View.VISIBLE);
        } else {
            if (allergies.size() == 0) {
                binding.emptyAllergyList.setVisibility(View.VISIBLE);
            } else {
                binding.emptyAllergyList.setVisibility(View.GONE);
                PatientAllergyRecyclerViewAdapter adapter = new PatientAllergyRecyclerViewAdapter(getContext(), allergies);
                binding.recyclerViewAllergy.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
