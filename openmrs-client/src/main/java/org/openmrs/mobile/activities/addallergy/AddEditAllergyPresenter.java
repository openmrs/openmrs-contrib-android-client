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

import androidx.fragment.app.Fragment;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.repository.AllergyRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.ConceptMembers;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.SystemProperty;

import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_DRUG;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_ENVIRONMENT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_FOOD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_REACTION;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MILD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MODERATE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_SEVERE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_ENVIRONMENT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD;
import static org.openmrs.mobile.utilities.ApplicationConstants.AllergyModule.PROPERTY_REACTION;

public class AddEditAllergyPresenter extends BasePresenter implements AddEditAllergyContract.Presenter {
    private AddEditAllergyContract.View view;
    private Long patientID;
    private Patient mPatient;
    private PatientDAO patientDAO;
    private AllergyRepository allergyRepository;
    Fragment fragment;

    public AddEditAllergyPresenter(AddEditAllergyContract.View view, Long patientID) {
        this.view = view;
        this.view.setPresenter(this);
        this.patientID = patientID;
        patientDAO = new PatientDAO();
        mPatient = patientDAO.findPatientByID(patientID.toString());
        allergyRepository = new AllergyRepository(patientID.toString());
    }

    @Override
    public void subscribe() {

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
        view.setConceptMembers(conceptMembers, PROPERTY_ENVIRONMENT);
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
}
