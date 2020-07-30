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

package org.openmrs.mobile.activities.formadmission;

import android.content.Context;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.repository.ProviderRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.listeners.retrofit.EncounterResponseCallback;
import org.openmrs.mobile.listeners.retrofit.LocationResponseCallback;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallback;
import org.openmrs.mobile.models.EncounterProviderCreate;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.FormService.getFormResourceByName;

public class FormAdmissionPresenter extends BasePresenter implements FormAdmissionContract.Presenter {
    private FormAdmissionContract.View view;
    private Long patientID;
    private String encounterType;
    private String formName;
    private String formUUID;
    private Patient mPatient;
    private RestApi restApi;
    private Context mContext;
    private ProviderRepository providerRepository;

    public FormAdmissionPresenter(FormAdmissionContract.View view, Long patientID, String encounterType, String formName, Context context) {
        this.view = view;
        this.patientID = patientID;
        this.encounterType = encounterType;
        this.formName = formName;
        this.mPatient = new PatientDAO().findPatientByID(Long.toString(patientID));
        this.formUUID = getFormResourceByName(formName).getUuid();
        restApi = RestServiceBuilder.createService(RestApi.class);
        this.view.setPresenter(this);
        this.mContext = context;
        this.providerRepository = new ProviderRepository();
    }

    public FormAdmissionPresenter(FormAdmissionContract.View formAdmissionView, RestApi restApi, Context context) {
        this.view = formAdmissionView;
        this.restApi = restApi;
        this.view.setPresenter(this);
        this.mContext = context;
        this.providerRepository = new ProviderRepository();
    }

    @Override
    public void subscribe() {
        //the function to start with
    }

    @Override
    public void getProviders(FormAdmissionFragment fragment) {
        providerRepository.getProviders(restApi).observe(fragment, this::updateViews);
    }

    @Override
    public void updateViews(List<Provider> providerList) {
        if (providerList != null && providerList.size() != 0) {
            view.updateProviderAdapter(providerList);
        } else {
            view.showToast(mContext.getResources().getString(R.string.error_occurred));
            view.enableSubmitButton(false);
        }
    }

    public void getLocation(String url) {
        providerRepository.getLocation(restApi, url, new LocationResponseCallback() {
            @Override
            public void onResponse(List<LocationEntity> locationList) {
                view.updateLocationAdapter(locationList);
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                view.showToast(errorMessage);
                view.enableSubmitButton(false);
            }
        });
    }

    @Override
    public void getEncounterRoles() {
        providerRepository.getEncounterRoles(restApi, new EncounterResponseCallback() {
            @Override
            public void onResponse(List<Resource> encounterRoleList) {
                view.updateEncounterRoleList(encounterRoleList);
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                view.enableSubmitButton(false);
                view.showToast(errorMessage);
            }
        });
    }

    @Override
    public void createEncounter(String providerUUID, String locationUUID, String encounterRoleUUID) {
        view.enableSubmitButton(false);

        Encountercreate encountercreate = new Encountercreate();
        encountercreate.setPatient(mPatient.getUuid());
        encountercreate.setEncounterType(encounterType);
        encountercreate.setFormname(formName);
        encountercreate.setPatientId(patientID);
        encountercreate.setFormUuid(formUUID);
        encountercreate.setLocation(locationUUID);

        List<Obscreate> observations = new ArrayList<>();
        encountercreate.setObservations(observations);

        List<EncounterProviderCreate> encounterProviderCreate = new ArrayList<>();
        encounterProviderCreate.add(new EncounterProviderCreate(providerUUID, encounterRoleUUID));
        encountercreate.setEncounterProvider(encounterProviderCreate);

        long id = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext())
                .encounterCreateRoomDAO()
                .addEncounterCreated(encountercreate);
        encountercreate.setId(id);
        if (!mPatient.isSynced()) {
            mPatient.addEncounters(encountercreate.getId());
            new PatientDAO().updatePatient(mPatient.getId(), mPatient);
            view.showToast(mContext.getResources().getString(R.string.form_data_will_be_synced_later_error_message));
            view.enableSubmitButton(true);
        } else {
            new EncounterService().addEncounter(encountercreate, new DefaultResponseCallback() {
                @Override
                public void onResponse() {
                    view.enableSubmitButton(true);
                    ToastUtil.success(mContext.getString(R.string.form_submitted_successfully));
                }

                @Override
                public void onErrorResponse(String errorMessage) {
                    ToastUtil.error(errorMessage);
                    view.enableSubmitButton(true);
                }
            });
            view.quitFormEntry();
        }
    }
}
