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

import android.content.Context;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.AppDatabaseHelper;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

public class VisitDAO {

    OpenMRS openMRS = OpenMRS.getInstance();
    Context context = openMRS.getApplicationContext();
    AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper();
    LocationRoomDAO locationRoomDAO = AppDatabase.getDatabase(context).locationRoomDAO();
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(context).observationRoomDAO();
    VisitRoomDAO visitRoomDAO = AppDatabase.getDatabase(context).visitRoomDAO();

    public Observable<Long> saveOrUpdate(Visit visit, long patientId) {
        return createObservableIO(() -> {
            Long visitId = visit.getId();
            if (visitId == null) {
                visitId = getVisitsIDByUUID(visit.getUuid()).toBlocking().first();
            }
            if (visitId > 0) {
                updateVisit(visit, visitId, patientId);
            } else {
                visitId = saveVisit(visit, patientId);
            }
            return visitId;
        });
    }

    private long saveVisit(Visit visit, long patientID) {
        EncounterDAO encounterDAO = new EncounterDAO();
        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(patientID)));
        VisitEntity visitEntity = appDatabaseHelper.visitToVisitEntity(visit);
        long visitID = visitRoomDAO.addVisit(visitEntity);
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                long encounterID = encounterDAO.saveEncounter(encounter, visitID);
                for (Observation obs : encounter.getObservations()) {
                    ObservationEntity observationEntity = appDatabaseHelper.observationToEntity(obs, encounterID);
                    observationRoomDAO.addObservation(observationEntity);
                }
            }
        }
        return visitID;
    }

    private boolean updateVisit(Visit visit, long visitID, long patientID) {
        EncounterDAO encounterDAO = new EncounterDAO();
        ObservationDAO observationDAO = new ObservationDAO();
        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(patientID)));
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                long encounterID = encounterDAO.getEncounterByUUID(encounter.getUuid());

                if (encounterID > 0) {
                    encounterDAO.updateEncounter(encounterID, encounter, visitID);
                } else {
                    encounterID = encounterDAO.saveEncounter(encounter, visitID);
                }

                List<Observation> oldObs = observationDAO.findObservationByEncounterID(encounterID);
                for (Observation obs : oldObs) {
                    ObservationEntity observationEntity = appDatabaseHelper.observationToEntity(obs, encounterID);
                    observationRoomDAO.deleteObservation(observationEntity);
                }

                for (Observation obs : encounter.getObservations()) {
                    ObservationEntity observationEntity = appDatabaseHelper.observationToEntity(obs, encounterID);
                    observationRoomDAO.addObservation(observationEntity);
                }
            }
        }
        return visitRoomDAO.updateVisit(appDatabaseHelper.visitToVisitEntity(visit)) > 0;
    }

    public Observable<List<Visit>> getActiveVisits() {
        return createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            visitRoomDAO.getActiveVisits()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<VisitEntity>>() {
                        @Override
                        public void onSuccess(List<VisitEntity> visitEntities) {
                            for (VisitEntity entity : visitEntities) {
                                visits.add(appDatabaseHelper.visitEntityToVisit(entity));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
            return visits;
        });
    }

    public Observable<List<Visit>> getVisitsByPatientID(final Long patientID) {
        return createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            visitRoomDAO.getVisitsByPatientID(patientID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<VisitEntity>>() {
                        @Override
                        public void onSuccess(List<VisitEntity> visitEntities) {
                            for (VisitEntity entity : visitEntities) {
                                visits.add(appDatabaseHelper.visitEntityToVisit(entity));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
            return visits;
        });
    }

    public Observable<Visit> getActiveVisitByPatientId(Long patientId) {
        return createObservableIO(() -> {
            final Visit[] activeVisit = {null};
            visitRoomDAO.getActiveVisitByPatientId(patientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<VisitEntity>() {
                        @Override
                        public void onSuccess(VisitEntity visitEntity) {
                            activeVisit[0] = appDatabaseHelper.visitEntityToVisit(visitEntity);
                        }

                        @Override
                        public void onError(Throwable e) {
                            activeVisit[0] = null;
                        }
                    });
            return activeVisit[0];
        });
    }

    public Observable<Visit> getVisitByID(final Long visitID) {
        return createObservableIO(() -> {
            final Visit[] visit = {null};
            visitRoomDAO.getVisitByID(visitID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<VisitEntity>() {
                        @Override
                        public void onSuccess(VisitEntity visitEntity) {
                            visit[0] = appDatabaseHelper.visitEntityToVisit(visitEntity);
                        }

                        @Override
                        public void onError(Throwable e) {
                            visit[0] = null;
                        }
                    });
            return visit[0];
        });
    }

    public Observable<Long> getVisitsIDByUUID(final String visitUUID) {
        return createObservableIO(() -> visitRoomDAO.getVisitsIDByUUID(visitUUID));
    }

    public Observable<Visit> getVisitByUuid(String uuid) {
        return createObservableIO(() -> {
            final Visit[] visit = {null};
            visitRoomDAO.getVisitByUuid(uuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<VisitEntity>() {
                        @Override
                        public void onSuccess(VisitEntity visitEntity) {
                            visit[0] = appDatabaseHelper.visitEntityToVisit(visitEntity);
                        }

                        @Override
                        public void onError(Throwable e) {
                            visit[0] = null;
                        }
                    });

            return visit[0];
        });
    }

    public Observable<Boolean> deleteVisitsByPatientId(Long id) {
        return createObservableIO(() -> {
            visitRoomDAO.deleteVisitsByPatientId(id);
            return true;
        });
    }
}
