/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.query.Select;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.multiple.MultipleResults;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.IdGenPatientIdentifiers;
import org.openmrs.mobile.models.retrofit.IdentifierType;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.PatientIdentifier;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PatientService extends IntentService {
    public static final String FULL_REPRESENTATION = "full";
    OpenMRS openMrs = OpenMRS.getInstance();
    PatientDAO patientDao = new PatientDAO();

    public PatientService() {
        super("Register Patients");
    }

    public SimplePromise<Patient> registerPatient(final Patient patient) {
        patient.setSynced(false);
        patientDao.savePatient(patient);
        return syncPatient(patient);
    }

    public SimplePromise<Patient> syncPatient(final Patient patient) {
        final SimpleDeferredObject<Patient> deferred = new SimpleDeferredObject<>();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        Boolean syncstate = prefs.getBoolean("sync", true);

        if (syncstate) {
            AndroidDeferredManager dm = new AndroidDeferredManager();
            dm.when(getLocationUuid(), getIdGenPatientIdentifier(), getPatientIdentifierTypeUuid())
                    .done(new DoneCallback<MultipleResults>() {
                        @Override
                        public void onDone(final MultipleResults results) {
                            final List<PatientIdentifier> identifiers = new ArrayList<>();

                            final PatientIdentifier identifier = new PatientIdentifier();
                            identifier.setLocation((Location) results.get(0).getResult());
                            identifier.setIdentifier((String) results.get(1).getResult());
                            identifier.setIdentifierType((IdentifierType) results.get(2).getResult());
                            identifiers.add(identifier);

                            patient.setIdentifiers(identifiers);
                            patient.setUuid(null);

                            final RestApi apiService =
                                    RestServiceBuilder.createService(RestApi.class);
                            Call<Patient> call = apiService.createPatient(patient);
                            call.enqueue(new Callback<Patient>() {
                                @Override
                                public void onResponse(Call<Patient> call, Response<Patient> response) {
                                    if (response.isSuccessful()) {
                                        Patient newPatient = response.body();

                                        ToastUtil.success("Patient " +patient.getPerson().getName().getNameString()
                                                +" created with UUID " + newPatient.getUuid());

                                        patient.setSynced(true);
                                        patient.setUuid(newPatient.getUuid());

                                        new PatientDAO().updatePatient(patient.getId(), patient);
                                        if(!patient.getEncounters().equals(""))
                                            addEncounters(patient);

                                        deferred.resolve(patient);
                                    } else {
                                        ToastUtil.error("Patient[" + patient.getId() + "] cannot be synced due to server error"+ response.message());
                                        deferred.reject(new RuntimeException("Patient cannot be synced due to server error: " + response.errorBody().toString()));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Patient> call, Throwable t) {
                                    ToastUtil.notify("Patient[" + patient.getId() + "] cannot be synced due to request error: " + t.toString());

                                    deferred.reject(t);
                                }
                            });
                        }
                    });
        } else {
            ToastUtil.error("Sync is off. Patient Registration data is saved locally " +
                    "and will sync when online mode is restored. ");
        }

        return deferred.promise();
    }

    private void addEncounters(Patient patient) {
        String enc=patient.getEncounters();
        List<Long> list = new ArrayList<>();
        for (String s : enc.split(","))
            list.add(Long.parseLong(s));


        for(long id:list)
        {
            Encountercreate encountercreate = new Select()
                        .from(Encountercreate.class)
                        .where("id = ?",id)
                        .executeSingle();
            encountercreate.setPatient(patient.getUuid());
            encountercreate.save();
            new EncounterService().addEncounter(encountercreate);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(NetworkUtils.isOnline()) {
            List<Patient> patientList = new PatientDAO().getAllPatients();
            final ListIterator<Patient> it = patientList.listIterator();
            while (it.hasNext()) {
                final Patient patient=it.next();
                if(!patient.isSynced()) {
                    syncPatient(patient);
                }
            }
        } else {
            ToastUtil.error("No internet connection. Patient Registration data is saved locally " +
                    "and will sync when internet connection is restored. ");
        }
    }


    SimplePromise<Location> getLocationUuid() {
        final SimpleDeferredObject<Location> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
                RestServiceBuilder.createService(RestApi.class);
        Call<Results<Location>> call = apiService.getLocations();
        call.enqueue(new Callback<Results<Location>>() {
            @Override
            public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                Results<Location> locationList = response.body();
                for (Location result : locationList.getResults()) {
                    if ((result.getDisplay().trim()).equalsIgnoreCase((openMrs.getLocation().trim()))) {
                        deferred.resolve(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<Location>> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });

        return deferred.promise();
    }

    SimplePromise<String> getIdGenPatientIdentifier() {
        final SimpleDeferredObject<String> deferred = new SimpleDeferredObject<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(openMrs.getServerUrl() + '/')
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi apiService =
                retrofit.create(RestApi.class);
        Call<IdGenPatientIdentifiers> call = apiService.getPatientIdentifiers(openMrs.getUsername(), openMrs.getPassword());
        call.enqueue(new Callback<IdGenPatientIdentifiers>() {
            @Override
            public void onResponse(Call<IdGenPatientIdentifiers> call, Response<IdGenPatientIdentifiers> response) {
                IdGenPatientIdentifiers idList = response.body();
                deferred.resolve(idList.getIdentifiers().get(0));
            }

            @Override
            public void onFailure(Call<IdGenPatientIdentifiers> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });

        return deferred.promise();
    }


    SimplePromise<IdentifierType> getPatientIdentifierTypeUuid() {
        final SimpleDeferredObject<IdentifierType> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
                RestServiceBuilder.createService(RestApi.class);
        Call<Results<IdentifierType>> call = apiService.getIdentifierTypes();
        call.enqueue(new Callback<Results<IdentifierType>>() {
            @Override
            public void onResponse(Call<Results<IdentifierType>> call, Response<Results<IdentifierType>> response) {
                Results<IdentifierType> idresList = response.body();
                for (IdentifierType result : idresList.getResults()) {
                    if(result.getDisplay().equals("OpenMRS ID")) {
                        deferred.resolve(result);
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<IdentifierType>> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });

        return deferred.promise();
    }


}