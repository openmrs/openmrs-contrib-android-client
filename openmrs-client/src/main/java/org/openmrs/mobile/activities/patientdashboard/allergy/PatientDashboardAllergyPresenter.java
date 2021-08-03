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

import androidx.lifecycle.LifecycleOwner;

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.AllergyRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;

import java.util.List;

public class PatientDashboardAllergyPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientAllergyPresenter, DefaultResponseCallback {
    private PatientDashboardContract.ViewPatientAllergy mPatientAllergyView;
    private String patientId;
    private PatientDAO patientDAO;
    private RestApi restApi;
    private AllergyRepository allergyRepository;

    public PatientDashboardAllergyPresenter(String patientId, PatientDashboardContract.ViewPatientAllergy mPatientAllergyView) {
        this.mPatientAllergyView = mPatientAllergyView;
        this.patientId = patientId;
        this.patientDAO = new PatientDAO();
        this.mPatient = patientDAO.findPatientByID(patientId);
        allergyRepository = new AllergyRepository(patientId);
        restApi = RestServiceBuilder.createService(RestApi.class);
        mPatientAllergyView.setPresenter(this);
    }

    public PatientDashboardAllergyPresenter(Patient patient, PatientDashboardContract.ViewPatientAllergy viewPatientAllergy, RestApi restApi, AllergyRepository allergyRepository) {
        this.mPatientAllergyView = viewPatientAllergy;
        this.mPatient = patient;
        this.restApi = restApi;
        this.mPatientAllergyView.setPresenter(this);
        this.allergyRepository = allergyRepository;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void getAllergy(LifecycleOwner lifecycleOwner) {
        allergyRepository.getAllergies(restApi, mPatient.getUuid()).observe(lifecycleOwner, this::updateViews);
    }

    public void getAllergyFromDatabase() {
        List<Allergy> allergyList = allergyRepository.getAllergyFromDatabase(mPatient.getId().toString());
        updateViews(allergyList);
    }

    @Override
    public void deleteAllergy(String allergyUuid) {
        allergyRepository.deleteAllergy(restApi, mPatient.getUuid(), allergyUuid, this);
    }

    public void updateViews(List<Allergy> allergies) {
        mPatientAllergyView.showAllergyList(allergies);
    }

    @Override
    public void onResponse() {
        getAllergyFromDatabase();
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        ToastUtil.error(errorMessage);
    }
}
