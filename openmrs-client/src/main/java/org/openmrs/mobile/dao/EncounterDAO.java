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

package org.openmrs.mobile.dao;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.AppDatabaseHelper;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ActiveAndroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

public class EncounterDAO {
    AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper();
    EncounterRoomDAO encounterRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).encounterRoomDAO();
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).observationRoomDAO();

    public long saveEncounter(Encounter encounter, Long visitID) {
        EncounterEntity encounterEntity = appDatabaseHelper.encounterToEntity(encounter, visitID);
        return encounterRoomDAO.addEncounter(encounterEntity);
    }

    public EncounterType getEncounterTypeByFormName(String formname) {
        return new Select()
                .from(EncounterType.class)
                .where("display = ?", formname)
                .executeSingle();
    }

    public void saveLastVitalsEncounter(Encounter encounter, String patientUUID) {
        if (null != encounter) {
            encounter.setPatientUUID(patientUUID);
            long oldLastVitalsEncounterID = encounterRoomDAO.getLastVitalsEncounterID(patientUUID).blockingGet();
            if (0 != oldLastVitalsEncounterID) {
                for (Observation obs : new ObservationDAO().findObservationByEncounterID(oldLastVitalsEncounterID)) {
                    ObservationEntity observationEntity = appDatabaseHelper.observationToEntity(obs, 1L);
                    observationRoomDAO.deleteObservation(observationEntity);
                }
                encounterRoomDAO.deleteEncounterByID(oldLastVitalsEncounterID);
            }
            long encounterID = saveEncounter(encounter, null);
            for (Observation obs : encounter.getObservations()) {
                ObservationEntity observationEntity = appDatabaseHelper.observationToEntity(obs, encounterID);
                observationRoomDAO.addObservation(observationEntity);
            }
        }
    }

    public Observable<Encounter> getLastVitalsEncounter(String patientUUID) {
        return createObservableIO(() -> {
            final Encounter[] encounter = {null};
            encounterRoomDAO.getLastVitalsEncounter(patientUUID, EncounterType.VITALS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<EncounterEntity>() {
                        @Override
                        public void onSuccess(EncounterEntity encounterEntity) {
                            encounter[0] = appDatabaseHelper.encounterEntityToEncounter(encounterEntity);
                        }

                        @Override
                        public void onError(Throwable e) {
                            encounter[0] = null;
                        }
                    });
            return encounter[0];
        });
    }

    public int updateEncounter(long encounterID, Encounter encounter, long visitID) {
        EncounterEntity encounterEntity = appDatabaseHelper.encounterToEntity(encounter, visitID);
        return encounterRoomDAO.updateEncounter(encounterEntity);
    }

    public List<Encounter> findEncountersByVisitID(Long visitID) {
        List<Encounter> encounters = new ArrayList<>();
        List<EncounterEntity> encounterEntities = encounterRoomDAO.findEncountersByVisitID(visitID.toString()).blockingGet();

        for (EncounterEntity entity : encounterEntities) {
            encounters.add(appDatabaseHelper.encounterEntityToEncounter(entity));
        }
        return encounters;
    }

    public Observable<List<Encounter>> getAllEncountersByType(Long patientID, EncounterType type) {
        return createObservableIO(() -> {
            List<Encounter> encounters = new ArrayList<>();
            encounterRoomDAO.getAllEncountersByType(patientID, type.getDisplay())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<EncounterEntity>>() {
                        @Override
                        public void onSuccess(List<EncounterEntity> encounterEntities) {
                            for (EncounterEntity entity : encounterEntities) {
                                encounters.add(appDatabaseHelper.encounterEntityToEncounter(entity));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
            return encounters;
        });
    }

    public long getEncounterByUUID(final String encounterUUID) {
        return encounterRoomDAO.getEncounterByUUID(encounterUUID).blockingGet();
    }
}
