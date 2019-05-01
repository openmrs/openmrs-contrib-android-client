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

import net.sqlcipher.Cursor;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.VisitTable;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitType;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

public class VisitDAO {

    public Observable<Long> saveOrUpdate(Visit visit, long patientId) {
        return createObservableIO(() -> {
            Long visitId = visit.getId();
            if(visitId == null)
                visitId = getVisitsIDByUUID(visit.getUuid()).toBlocking().first();
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
        ObservationDAO observationDAO = new ObservationDAO();
        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(patientID)));
        long visitID = new VisitTable().insert(visit);
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                long encounterID = encounterDAO.saveEncounter(encounter, visitID);
                for (Observation obs : encounter.getObservations()) {
                    observationDAO.saveObservation(obs, encounterID)
                            .observeOn(Schedulers.io())
                            .subscribe();
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
                    observationDAO.deleteObservation(obs.getId());
                }

                for (Observation obs : encounter.getObservations()) {
                    observationDAO.saveObservation(obs, encounterID)
                            .observeOn(Schedulers.io())
                            .subscribe();
                }
            }
        }
        return new VisitTable().update(visitID, visit) > 0;
    }

    public Observable<List<Visit>> getActiveVisits() {
        return createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

            String visitWhere = String.format("%s IS NULL OR %s = ''", VisitTable.Column.STOP_DATE, VisitTable.Column.STOP_DATE);
            String orderBy = VisitTable.Column.START_DATE + " DESC";

            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, visitWhere, null, null, null, orderBy);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        int visitPatientID_CI = cursor.getColumnIndex(VisitTable.Column.PATIENT_KEY_ID);
                        Visit visit = new Visit();
                        visit.setUuid(cursor.getString(visitUUID_CI));
                        visit.setId(cursor.getLong(visitID_CI));
                        visit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                        visit.setLocation(new Location(cursor.getString(visitPlace_CI)));
                        visit.setStartDatetime(cursor.getString(visitStart_CI));
                        visit.setStopDatetime(cursor.getString(visitStop_CI));
                        visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visit.getId()));
                        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                        visits.add(visit);
                    }
                } finally {
                    cursor.close();
                }
            }
            return visits;
        });
    }

    public Observable<List<Visit>> getVisitsByPatientID(final Long patientID) {
        return createObservableIO(() -> {
            List<Visit> visits = new ArrayList<>();
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

            String where = String.format("%s = ?", VisitTable.Column.PATIENT_KEY_ID);
            String[] whereArgs = new String[]{patientID.toString()};
            String orderBy = VisitTable.Column.START_DATE + " DESC";

            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, orderBy);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        int visitPatientID_CI = cursor.getColumnIndex(VisitTable.Column.PATIENT_KEY_ID);
                        Visit visit = new Visit();
                        visit.setUuid(cursor.getString(visitUUID_CI));
                        visit.setId(cursor.getLong(visitID_CI));
                        visit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                        visit.setLocation(new Location(cursor.getString(visitPlace_CI)));
                        visit.setStartDatetime(cursor.getString(visitStart_CI));
                        visit.setStopDatetime(cursor.getString(visitStop_CI));
                        visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visit.getId()));
                        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                        visits.add(visit);
                    }
                } finally {
                    cursor.close();
                }
            }
            return visits;
        });
    }

    public Observable<Visit> getActiveVisitByPatientId(Long patientId){
        return createObservableIO(() -> {
            LocationDAO locationDAO = new LocationDAO();
            Visit activeVisit = null;
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

            String where = String.format("%s = ? AND (%s is null OR %s = '')",
                    VisitTable.Column.PATIENT_KEY_ID, VisitTable.Column.STOP_DATE,
                    VisitTable.Column.STOP_DATE);
            String[] whereArgs = new String[]{patientId.toString()};
            String orderBy = VisitTable.Column.START_DATE + " DESC";
            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, orderBy);
            if (null != cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        int visitPatientID_CI = cursor.getColumnIndex(VisitTable.Column.PATIENT_KEY_ID);
                        Visit visit = new Visit();
                        visit.setUuid(cursor.getString(visitUUID_CI));
                        visit.setId(cursor.getLong(visitID_CI));
                        visit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                        visit.setLocation(locationDAO.findLocationByName(cursor.getString(visitPlace_CI)));
                        visit.setStartDatetime(cursor.getString(visitStart_CI));
                        visit.setStopDatetime(cursor.getString(visitStop_CI));
                        visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visit.getId()));
                        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                        activeVisit = visit;
                    }
                } finally {
                    cursor.close();
                }
            }
            return activeVisit;
        });
    }

    public Observable<Visit> getVisitByID(final Long visitID) {
        return createObservableIO(() -> {
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
            Visit visit = null;
            LocationDAO locationDAO = new LocationDAO();

            String where = String.format("%s = ?", VisitTable.Column.ID);
            String[] whereArgs = new String[]{visitID.toString()};
            String orderBy = VisitTable.Column.START_DATE + " DESC";

            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, orderBy);
            if (null != cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        int visitPatientID_CI = cursor.getColumnIndex(VisitTable.Column.PATIENT_KEY_ID);
                        Visit patientVisit = new Visit();
                        patientVisit.setUuid(cursor.getString(visitUUID_CI));
                        patientVisit.setId(cursor.getLong(visitID_CI));
                        patientVisit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                        patientVisit.setLocation(locationDAO.findLocationByName(cursor.getString(visitPlace_CI)));
                        patientVisit.setStartDatetime(cursor.getString(visitStart_CI));
                        patientVisit.setStopDatetime(cursor.getString(visitStop_CI));
                        patientVisit.setEncounters(new EncounterDAO().findEncountersByVisitID(visitID));
                        patientVisit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                        visit = patientVisit;
                    }
                } finally {
                    cursor.close();
                }
            }
            return visit;
        });
    }

    public Observable<Long> getVisitsIDByUUID(final String visitUUID) {
        return createObservableIO(() -> {
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

            String where = String.format("%s = ?", VisitTable.Column.UUID);
            String[] whereArgs = new String[]{visitUUID};
            long visitID = 0;
            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, null);
            if (null != cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        visitID = cursor.getLong(visitID_CI);
                    }
                } finally {
                    cursor.close();
                }
            }
            return visitID;
        });
    }

    public Observable<Visit> getVisitByUuid(String uuid) {
        return createObservableIO(() -> {
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
            Visit visit = null;
            LocationDAO locationDAO = new LocationDAO();

            String where = String.format("%s = ?", VisitTable.Column.UUID);
            String[] whereArgs = new String[]{uuid};
            String orderBy = VisitTable.Column.START_DATE + " DESC";

            final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, orderBy);
            if (null != cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                        int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                        int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                        int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                        int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                        int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                        int visitPatientID_CI = cursor.getColumnIndex(VisitTable.Column.PATIENT_KEY_ID);
                        Visit patientVisit = new Visit();
                        patientVisit.setUuid(cursor.getString(visitUUID_CI));
                        patientVisit.setId(cursor.getLong(visitID_CI));
                        patientVisit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                        patientVisit.setLocation(locationDAO.findLocationByName(cursor.getString(visitPlace_CI)));
                        patientVisit.setStartDatetime(cursor.getString(visitStart_CI));
                        patientVisit.setStopDatetime(cursor.getString(visitStop_CI));
                        patientVisit.setEncounters(new EncounterDAO().findEncountersByVisitID(patientVisit.getId()));
                        patientVisit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                        visit = patientVisit;
                    }
                } finally {
                    cursor.close();
                }
            }
            return visit;
        });
    }

    public Observable<Boolean> deleteVisitsByPatientId(Long id) {
        return createObservableIO(() -> {
            OpenMRS.getInstance().getOpenMRSLogger().w("Visits deleted with patient_id: " + id);
            DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
            openHelper.getReadableDatabase().delete(VisitTable.TABLE_NAME, VisitTable.Column.PATIENT_KEY_ID
                    + " = " + id, null);
            return true;
        });
    }
}
