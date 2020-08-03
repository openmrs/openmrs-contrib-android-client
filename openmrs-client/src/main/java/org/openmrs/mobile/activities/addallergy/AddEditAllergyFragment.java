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
import org.openmrs.mobile.models.ConceptMembers;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.SystemProperty;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_MILD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_REACTION;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_SEVERE;

public class AddEditAllergyFragment extends ACBaseFragment<AddEditAllergyContract.Presenter> implements AddEditAllergyContract.View {
    private FragmentAllergyInfoBinding patientAllergyBinding;
    private List<Resource> foodAllergens = new ArrayList<>();
    private List<Resource> drugAllergens = new ArrayList<>();
    private List<Resource> environmentAllergens = new ArrayList<>();
    private List<Resource> reactionList = new ArrayList<>();
    private String mildSeverity;
    private String moderateSeverity;
    private String severeSeverity;

    public static AddEditAllergyFragment newInstance() {
        return new AddEditAllergyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        patientAllergyBinding = FragmentAllergyInfoBinding.inflate(inflater, container, false);
        View root = patientAllergyBinding.getRoot();
        mPresenter.fetchSystemProperties(this);
        return root;
    }


    @Override
    public void setConceptMembers(ConceptMembers conceptMembers, String reactions) {
        switch (reactions) {
            case PROPERTY_DRUG:
                drugAllergens = conceptMembers.getMembers();
                break;
            case PROPERTY_FOOD:
                foodAllergens = conceptMembers.getMembers();
                break;
            case PROPERTY_REACTION:
                reactionList = conceptMembers.getMembers();
                break;
            default:
                environmentAllergens = conceptMembers.getMembers();
                break;
        }
    }

    @Override
    public void setSeverity(SystemProperty systemProperty) {
        if (systemProperty.getDisplay().contains(PROPERTY_MILD)) {
            mildSeverity = systemProperty.getConceptUUID();
        } else if (systemProperty.getDisplay().contains(PROPERTY_SEVERE)) {
            severeSeverity = systemProperty.getConceptUUID();
        } else {
            moderateSeverity = systemProperty.getConceptUUID();
        }
    }
}
