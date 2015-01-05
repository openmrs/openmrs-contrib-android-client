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

package org.openmrs.client.dao;

import android.database.CursorJoiner;

import net.sqlcipher.Cursor;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.PatientTable;
import org.openmrs.client.databases.tables.VisitTable;
import org.openmrs.client.databases.tables.Table;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Observation;
import org.openmrs.client.models.Visit;
import org.openmrs.client.models.VisitItemDTO;

import java.util.ArrayList;
import java.util.List;

public class VisitDAO {

    public long saveVisit(Visit visit, long patientID) {
        EncounterDAO encounterDAO = new EncounterDAO();
        ObservationDAO observationDAO = new ObservationDAO();
        visit.setPatientID(patientID);
        long visitID = new VisitTable().insert(visit);
        for (Encounter encounter : visit.getEncounters()) {
            long encounterID = encounterDAO.saveEncounter(encounter, visitID);
            for (Observation obs : encounter.getObservations()) {
                observationDAO.saveObservation(obs, encounterID);
            }
        }
        return visitID;
    }

    public boolean updateVisit(Visit visit, long visitID, long patientID) {
        EncounterDAO encounterDAO = new EncounterDAO();
        ObservationDAO observationDAO = new ObservationDAO();
        visit.setPatientID(patientID);
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
        return new VisitTable().update(visitID, visit) > 0;
    }

    public List<VisitItemDTO> findActiveVisitsByPatientNameLike(final String patientName) {
        String where = String.format("%s LIKE  ?", PatientTable.Column.DISPLAY);
        String[] whereArgs = new String[]{"%" + patientName + "%"};
        return getAllActiveVisits(where, whereArgs);
    }

    public List<VisitItemDTO> getAllActiveVisits() {
        return getAllActiveVisits(null, null);
    }

    public List<VisitItemDTO> getAllActiveVisits(final String where, final String[] whereArgs) {
        List<VisitItemDTO> visitItems = new ArrayList<VisitItemDTO>();

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
                            int visitId_CI = visitCursor.getColumnIndex(VisitTable.Column.ID);
                            int visitPlace_CI = visitCursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                            int visitType_CI = visitCursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                            int visitStart_CI = visitCursor.getColumnIndex(VisitTable.Column.START_DATE);
                            int patientName_CI = patientCursor.getColumnIndex(PatientTable.Column.DISPLAY);
                            int patientIdentifier_CI = patientCursor.getColumnIndex(PatientTable.Column.IDENTIFIER);

                            long visitId = visitCursor.getLong(visitId_CI);
                            String visitPlace = visitCursor.getString(visitPlace_CI);
                            String visitType = visitCursor.getString(visitType_CI);
                            long visitStart = visitCursor.getLong(visitStart_CI);
                            String patientIdentifier = patientCursor.getString(patientIdentifier_CI);
                            String patientName = patientCursor.getString(patientName_CI);

                            VisitItemDTO visitItemDTO = new VisitItemDTO(visitId, patientName,
                                    patientIdentifier, visitPlace, visitType, visitStart);
                            visitItems.add(visitItemDTO);
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

    public List<Visit> getVisitsByPatientID(final Long patientID) {
        List<Visit> visits = new ArrayList<Visit>();
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String where = String.format("%s = ?", VisitTable.Column.PATIENT_KEY_ID);
        String[] whereArgs = new String[]{patientID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int visitUUID_CI = cursor.getColumnIndex(VisitTable.Column.UUID);
                    int visitID_CI = cursor.getColumnIndex(VisitTable.Column.ID);
                    int visitType_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_TYPE);
                    int visitPlace_CI = cursor.getColumnIndex(VisitTable.Column.VISIT_PLACE);
                    int visitStart_CI = cursor.getColumnIndex(VisitTable.Column.START_DATE);
                    int visitStop_CI = cursor.getColumnIndex(VisitTable.Column.STOP_DATE);
                    Visit visit = new Visit();
                    visit.setUuid(cursor.getString(visitUUID_CI));
                    visit.setId(cursor.getLong(visitID_CI));
                    visit.setVisitType(cursor.getString(visitType_CI));
                    visit.setVisitPlace(cursor.getString(visitPlace_CI));
                    visit.setStartDate(cursor.getLong(visitStart_CI));
                    visit.setStopDate(cursor.getLong(visitStop_CI));
                    visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visit.getId()));
                    visits.add(visit);
                }
            } finally {
                cursor.close();
            }
        }
        return visits;
    }

    public Visit getVisitsByID(final Long visitID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Visit visit = null;

        String where = String.format("%s = ?", VisitTable.Column.ID);
        String[] whereArgs = new String[]{visitID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(VisitTable.TABLE_NAME, null, where, whereArgs, null, null, null);
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
                    visit.setVisitType(cursor.getString(visitType_CI));
                    visit.setVisitPlace(cursor.getString(visitPlace_CI));
                    visit.setStartDate(cursor.getLong(visitStart_CI));
                    visit.setStopDate(cursor.getLong(visitStop_CI));
                    visit.setEncounters(new EncounterDAO().findEncountersByVisitID(visitID));
                    visit.setPatientID(cursor.getLong(visitPatientID_CI));
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
