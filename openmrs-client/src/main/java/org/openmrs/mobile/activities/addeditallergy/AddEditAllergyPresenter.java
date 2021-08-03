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

package org.openmrs.mobile.activities.addeditallergy;

import androidx.fragment.app.Fragment;

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.AllergyPatient;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.repository.AllergyRepository;
import org.openmrs.mobile.application.OpenMRS;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;

import java.util.ArrayList;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_DRUG;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_ENVIRONMENT;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_FOOD;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_REACTION;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MILD;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MODERATE;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_SEVERE;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_OTHER;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_REACTION;

public class AddEditAllergyPresenter extends BasePresenter implements AddEditAllergyContract.Presenter, DefaultResponseCallback {
    Fragment fragment;
    private AddEditAllergyContract.View view;
    private Patient mPatient;
    private Allergy mAllergy;
    private String allergyUuid;
    private AllergyRepository allergyRepository;

    public AddEditAllergyPresenter(AddEditAllergyContract.View view, Long patientID, String allergyUuid) {
        this.view = view;
        this.view.setPresenter(this);
        this.allergyUuid = allergyUuid;
        mPatient = new PatientDAO().findPatientByID(patientID.toString());
        allergyRepository = new AllergyRepository(patientID.toString());
    }

    @Override
    public void subscribe() {
        mAllergy = allergyRepository.getAllergyByUUID(allergyUuid);
        view.fillAllergyToUpdate(mAllergy);
    }

    @Override
    public void fetchSystemProperties(Fragment fragment) {
        this.fragment = fragment;
        allergyRepository.getSystemProperty(CONCEPT_ALLERGEN_DRUG).observe(fragment, this::fetchConceptsForDrugs);
        allergyRepository.getSystemProperty(CONCEPT_ALLERGEN_ENVIRONMENT).observe(fragment, this::fetchConceptsForEnvironment);
        allergyRepository.getSystemProperty(CONCEPT_ALLERGEN_FOOD).observe(fragment, this::fetchConceptsForFood);
        allergyRepository.getSystemProperty(CONCEPT_REACTION).observe(fragment, this::fetchConceptsForReactions);
        allergyRepository.getSystemProperty(CONCEPT_SEVERITY_MILD).observe(fragment, this::setSeverity);
        allergyRepository.getSystemProperty(CONCEPT_SEVERITY_MODERATE).observe(fragment, this::setSeverity);
        allergyRepository.getSystemProperty(CONCEPT_SEVERITY_SEVERE).observe(fragment, this::setSeverity);
    }

    private void fetchConceptsForReactions(SystemProperty systemProperty) {
        allergyRepository.getConceptMembers(systemProperty.getConceptUUID()).observe(fragment, this::initializeReactions);
    }

    private void initializeReactions(ConceptMembers conceptMembers) {
        view.setConceptMembers(conceptMembers, PROPERTY_REACTION);
    }

    private void fetchConceptsForFood(SystemProperty systemProperty) {
        allergyRepository.getConceptMembers(systemProperty.getConceptUUID()).observe(fragment, this::initializeFoodAllergen);
    }

    private void initializeFoodAllergen(ConceptMembers conceptMembers) {
        view.setConceptMembers(conceptMembers, PROPERTY_FOOD);
    }

    private void fetchConceptsForEnvironment(SystemProperty systemProperty) {
        allergyRepository.getConceptMembers(systemProperty.getConceptUUID()).observe(fragment, this::initializeEnvironmentAllergen);
    }

    private void initializeEnvironmentAllergen(ConceptMembers conceptMembers) {
        view.setConceptMembers(conceptMembers, PROPERTY_OTHER);
    }

    private void fetchConceptsForDrugs(SystemProperty systemProperty) {
        allergyRepository.getConceptMembers(systemProperty.getConceptUUID()).observe(fragment, this::initializeDrugAllergen);
    }

    private void initializeDrugAllergen(ConceptMembers conceptMembers) {
        view.setConceptMembers(conceptMembers, PROPERTY_DRUG);
    }

    private void setSeverity(SystemProperty systemProperty) {
        view.setSeverity(systemProperty);
    }

    @Override
    public void createAllergy(AllergyCreate allergyCreate) {
        AllergyPatient allergyPatient = new AllergyPatient();
        allergyPatient.setUuid(mPatient.getUuid());
        allergyPatient.setIdentifier(new ArrayList<>());
        allergyCreate.setPatient(allergyPatient);
        allergyRepository.createAllergy(mPatient.getUuid(), allergyCreate, this);
    }

    @Override
    public void updateAllergy(AllergyCreate allergyCreate) {
        AllergyPatient allergyPatient = new AllergyPatient();
        allergyPatient.setUuid(mPatient.getUuid());
        allergyPatient.setIdentifier(new ArrayList<>());
        allergyCreate.setPatient(allergyPatient);
        allergyRepository.updateAllergy(mPatient.getUuid(), allergyUuid, mAllergy.getId(), allergyCreate, this);
    }

    @Override
    public void onResponse() {
        view.showLoading(false, true);
        ToastUtil.success(OpenMRS.getInstance().getString(R.string.success_creating_allergy));
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        view.showLoading(false, false);
        ToastUtil.error(errorMessage);
    }
}
