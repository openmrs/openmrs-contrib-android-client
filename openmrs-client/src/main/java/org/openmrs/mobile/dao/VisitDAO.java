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

import android.database.CursorJoiner;

import net.sqlcipher.Cursor;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.databases.tables.VisitTable;
import org.openmrs.mobile.databases.tables.Table;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.models.retrofit.Visit;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.VisitType;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class VisitDAO {

    public long saveVisit(Visit visit, long patientID) {
        EncounterDAO encounterDAO = new EncounterDAO();
        ObservationDAO observationDAO = new ObservationDAO();
        visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(patientID)));
        long visitID = new VisitTable().insert(visit);
        if (visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                long encounterID = encounterDAO.saveEncounter(encounter, visitID);
                for (Observation obs : encounter.getObservations()) {
                    observationDAO.saveObservation(obs, encounterID);
                }
            }
        }
        return visitID;
    }

    public boolean updateVisit(Visit visit, long visitID, long patientID) {
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
                    observationDAO.saveObservation(obs, encounterID);
                }
            }
        }
        return new VisitTable().update(visitID, visit) > 0;
    }

    public List<Visit> findActiveVisitsByPatientNameLike(final String patientName) {
        String where = String.format("%s LIKE  ?", PatientTable.Column.DISPLAY);
        String[] whereArgs = new String[]{"%" + patientName + "%"};
        return getAllActiveVisitsDTO(where, whereArgs);
    }

    public List<Visit> getAllActiveVisitsDTO() {
        return getAllActiveVisitsDTO(null, null);
    }

    public List<Visit> getAllActiveVisitsDTO(final String where, final String[] whereArgs) {
        List<Visit> visitItems = new ArrayList<>();

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String sort = VisitTable.Column.PATIENT_KEY_ID + " ASC";
        String visitWhere = String.format("%s IS NULL OR %s = ''", VisitTable.Column.STOP_DATE, VisitTable.Column.STOP_DATE);
        final Cursor visitCursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, visitWhere, null, null, null, sort);

        sort = Table.MasterColumn.ID + " ASC";
        final Cursor patientCursor = helper.getReadableDatabase().query(PatientTable.TABLE_NAME, null, where, whereArgs, null, null, sort);

        if (null != visitCursor && null != patientCursor) {
            try {
                final CursorJoiner joiner = new CursorJoiner(patientCursor,
                        new String[]{PatientTable.Column.ID}, visitCursor,
                        new String[]{VisitTable.Column.PATIENT_KEY_ID});
                for (CursorJoiner.Result result : joiner) {
                    switch (result) {
                        case BOTH:
                            long visitId_CI = visitCursor.getColumnIndex(VisitTable.Column.ID);
                            visitItems.add(getVisitsByID(visitId_CI));
                            break;
                        default:
                            break;
                    }
                }
            } finally {
                visitCursor.close();
                patientCursor.close();
            }
        }
        return visitItems;
    }


    public List<Visit> getAllActiveVisits(){
        List<Visit> allvisits=new ArrayList<>();
        List<Patient> patientList=new PatientDAO().getAllPatients();
        for (Patient patient:patientList)
        {
            if(isPatientNowOnVisit(patient.getId()))
                allvisits.add(getPatientCurrentVisit(patient.getId()));
        }
        return allvisits;
    }

    public List<Visit> getVisitsByPatientID(final Long patientID) {
        List<Visit> visits = new ArrayList<Visit>();
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
    }

    public boolean isPatientNowOnVisit(Long patientID) {
        return (null != getActiveVisitFromList(getVisitsByPatientID(patientID)));
    }

    public boolean isPatientNowOnVisit(List<Visit> patientVisits) {
        return (null != getActiveVisitFromList(patientVisits));
    }

    public Visit getPatientCurrentVisit(Long patientID) {
        return getActiveVisitFromList(getVisitsByPatientID(patientID));
    }

    private Visit getActiveVisitFromList(List<Visit> patientVisits) {
        Visit activeVisit = null;
        for (int i = 0; i < patientVisits.size(); i++) {
            if (DateUtils.convertTime(patientVisits.get(i).getStopDatetime()) == null) {
                activeVisit = patientVisits.get(i);
                break;
            }
        }
        return activeVisit;
    }

    public Visit getVisitsByID(final Long visitID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Visit visit = null;

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
                    visit = new Visit();
                    visit.setUuid(cursor.getString(visitUUID_CI));
                    visit.setId(cursor.getLong(visitID_CI));
                    visit.setVisitType(new VisitType(cursor.getString(visitType_CI)));
                    visit.setLocation(LocationDAO.findLocationByName(cursor.getString(visitPlace_CI)));
                    visit.setStartDatetime(cursor.getString(visitStart_CI));
                    visit.setStopDatetime(cursor.getString(visitStop_CI));
                    visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visitID));
                    visit.setPatient(new PatientDAO().findPatientByID(String.valueOf(cursor.getLong(visitPatientID_CI))));
                }
            } finally {
                cursor.close();
            }
        }
        return visit;
    }

    public long getVisitsIDByUUID(final String visitUUID) {
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
    }
}
