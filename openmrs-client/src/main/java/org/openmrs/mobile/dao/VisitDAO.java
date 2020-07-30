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

import rx.Observable;

public class VisitDAO {

    OpenMRS openMRS = OpenMRS.getInstance();
    Context context = openMRS.getApplicationContext();
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(context).observationRoomDAO();
    VisitRoomDAO visitRoomDAO = AppDatabase.getDatabase(context).visitRoomDAO();

    public Observable<Long> saveOrUpdate(Visit visit, long patientId) {
        return AppDatabaseHelper.createObservableIO(() -> {
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
        VisitEntity visitEntity = AppDatabaseHelper.convert(visit);
        long visitID = visitRoomDAO.addVisit(visitEntity);
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                long encounterID = encounterDAO.saveEncounter(encounter, visitID);
                for (Observation obs : encounter.getObservations()) {
                    ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
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
                    ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
                    observationRoomDAO.deleteObservation(observationEntity);
                }

                for (Observation obs : encounter.getObservations()) {
                    ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
                    observationRoomDAO.addObservation(observationEntity);
                }
            }
        }
        return visitRoomDAO.updateVisit(AppDatabaseHelper.convert(visit)) > 0;
    }

    public Observable<List<Visit>> getActiveVisits() {
        return AppDatabaseHelper.createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            List<VisitEntity> visitEntities;
            try {
                visitEntities = visitRoomDAO.getActiveVisits().blockingGet();
                for (VisitEntity entity : visitEntities) {
                    visits.add(AppDatabaseHelper.convert(entity));
                }
                return visits;
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    public Observable<List<Visit>> getVisitsByPatientID(final Long patientID) {
        return AppDatabaseHelper.createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            List<VisitEntity> visitEntities;
            try {
                visitEntities = visitRoomDAO.getVisitsByPatientID(patientID).blockingGet();
                for (VisitEntity entity : visitEntities) {
                    visits.add(AppDatabaseHelper.convert(entity));
                }
                return visits;
            } catch (Exception e) {
                return visits;
            }
        });
    }

    public Observable<Visit> getActiveVisitByPatientId(Long patientId) {
        return AppDatabaseHelper.createObservableIO(() -> {
            try {
                VisitEntity visitEntity = visitRoomDAO.getActiveVisitByPatientId(patientId).blockingGet();
                return AppDatabaseHelper.convert(visitEntity);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public Observable<Visit> getVisitByID(final Long visitID) {
        return AppDatabaseHelper.createObservableIO(() -> {
            try {
                VisitEntity visitEntity = visitRoomDAO.getVisitByID(visitID).blockingGet();
                return AppDatabaseHelper.convert(visitEntity);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public Observable<Long> getVisitsIDByUUID(final String visitUUID) {
        return AppDatabaseHelper.createObservableIO(() -> visitRoomDAO.getVisitsIDByUUID(visitUUID));
    }

    public Observable<Visit> getVisitByUuid(String uuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            try {
                VisitEntity visitEntity = visitRoomDAO.getVisitByUuid(uuid).blockingGet();
                return AppDatabaseHelper.convert(visitEntity);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public Observable<Boolean> deleteVisitsByPatientId(Long id) {
        return AppDatabaseHelper.createObservableIO(() -> {
            visitRoomDAO.deleteVisitsByPatientId(id);
            return true;
        });
    }
}
