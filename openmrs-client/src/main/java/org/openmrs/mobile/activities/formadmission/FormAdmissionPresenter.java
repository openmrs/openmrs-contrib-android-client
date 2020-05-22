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

import static org.openmrs.mobile.utilities.FormService.getFormResourceByName;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.ProviderRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterProviderCreate;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormAdmissionPresenter extends BasePresenter implements FormAdmissionContract.Presenter {

    private FormAdmissionContract.View view;
    private Long patientID;
    private String encounterType;
    private String formName;
    private String formUUID;
    private Patient mPatient;
    private RestApi restApi;

    public FormAdmissionPresenter(FormAdmissionContract.View view, Long patientID, String encounterType, String formName) {
        this.view = view;
        this.patientID = patientID;
        this.encounterType = encounterType;
        this.formName = formName;
        this.mPatient = new PatientDAO().findPatientByID(Long.toString(patientID));
        this.formUUID = getFormResourceByName(formName).getUuid();
        restApi = RestServiceBuilder.createService(RestApi.class);
        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        //the function to start with
    }

    @Override
    public void getProviders(FormAdmissionFragment fragment) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.getProviders(restApi).observe(fragment, this::updateViews);
    }

    @Override
    public void updateViews(List<Provider> providerList) {
        if (providerList != null && providerList.size() != 0) {
            view.updateProviderAdapter(providerList);
        } else {
            view.showToast("Error");
            view.enableSubmitButton(false);
        }
    }

    @Override
    public void getLocation(String url) {
        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            Call<Results<Location>> call =
                    restApi.getLocations(locationEndPoint, "Admission Location", "full");
            call.enqueue(new Callback<Results<Location>>() {
                @Override
                public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                    if (response.isSuccessful()) {
                        view.updateLocationAdapter(response.body().getResults());
                    } else {
                        view.showToast("An error Occurred, Try Again Later !!!");
                        view.enableSubmitButton(false);
                    }
                }

                @Override
                public void onFailure(Call<Results<Location>> call, Throwable t) {
                    view.showToast(t.getMessage());
                    view.enableSubmitButton(false);
                }
            });
        } else {
            view.showToast("You are currently offline, Try again when connected !!!");
        }
    }

    @Override
    public void getEncounterRoles() {
        restApi.getEncounterRoles().enqueue(new Callback<Results<Resource>>() {
            @Override
            public void onResponse(Call<Results<Resource>> call, Response<Results<Resource>> response) {
                if(response.isSuccessful()) {
                    view.updateEncounterRoleList(response.body().getResults());
                } else {
                    view.enableSubmitButton(false);
                    view.showToast("An error Occurred, Try Again Later !!!");
                }
            }

            @Override
            public void onFailure(Call<Results<Resource>> call, Throwable t) {
                view.showToast(t.getMessage());
                view.enableSubmitButton(false);
            }
        });
    }

    @Override
    public void createEncounter(String providerUUID, String locationUUID, String encounterRoleUUID) {
        view.enableSubmitButton(false);

        Encountercreate encountercreate=new Encountercreate();
        encountercreate.setPatient(mPatient.getUuid());
        encountercreate.setEncounterType(encounterType);
        encountercreate.setFormname(formName);
        encountercreate.setPatientId(patientID);
        encountercreate.setFormUuid(formUUID);
        encountercreate.setLocation(locationUUID);

        List<Obscreate> observations=new ArrayList<>();
        encountercreate.setObservations(observations);

        List<EncounterProviderCreate> encounterProviderCreate = new ArrayList<>();
        encounterProviderCreate.add(new EncounterProviderCreate(providerUUID, encounterRoleUUID));
        encountercreate.setEncounterProvider(encounterProviderCreate);

        encountercreate.setObslist();
        encountercreate.save();

        if(!mPatient.isSynced()) {
            mPatient.addEncounters(encountercreate.getId());
            new PatientDAO().updatePatient(mPatient.getId(),mPatient);
            ToastUtil.error("Patient not yet registered. Form data is saved locally " +
                    "and will sync when internet connection is restored. ");
            view.enableSubmitButton(true);
        }
        else {
            new EncounterService().addEncounter(encountercreate, new DefaultResponseCallbackListener() {
                @Override
                public void onResponse() {
                    view.enableSubmitButton(true);
                }
                @Override
                public void onErrorResponse(String errorMessage) {
                    view.showToast(errorMessage);
                    view.enableSubmitButton(true);
                }
            });
            view.quitFormEntry();
        }
    }
}
