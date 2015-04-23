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

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.EncounterTable;
import org.openmrs.mobile.databases.tables.ObservationTable;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;

import java.util.ArrayList;
import java.util.List;

public class EncounterDAO {

    public long saveEncounter(Encounter encounter, Long visitID) {
        encounter.setVisitID(visitID);
        return new EncounterTable().insert(encounter);
    }

    public void saveLastVitalsEncounter(Encounter encounter, Long patientID) {
        if (null != encounter) {
            encounter.setPatientID(patientID);
            long oldLastVitalsEncounterID = getLastVitalsEncounterID(patientID);
            if (0 != oldLastVitalsEncounterID) {
                for (Observation obs: new ObservationDAO().findObservationByEncounterID(oldLastVitalsEncounterID)) {
                    new ObservationTable().delete(obs.getId());
                }
                new EncounterTable().delete(oldLastVitalsEncounterID);
            }
            ObservationDAO observationDAO = new ObservationDAO();
            long encounterID = saveEncounter(encounter, null);
            for (Observation obs : encounter.getObservations()) {
                observationDAO.saveObservation(obs, encounterID);
            }
        }
    }

    public long getLastVitalsEncounterID(Long patientID) {
        long encounterID = 0;
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        String where = String.format("%s is NULL AND %s = ?", EncounterTable.Column.VISIT_KEY_ID, EncounterTable.Column.PATIENT_ID);
        String[] whereArgs = new String[]{String.valueOf(patientID)};
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int id_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    encounterID = cursor.getLong(id_CI);
                }
            } finally {
                cursor.close();
            }
        }
        return encounterID;
    }

    public Encounter getLastVitalsEncounter(Long patientID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Encounter encounter = null;

        String where = String.format("%s = ? AND %s = ? ORDER BY %s DESC LIMIT 1", EncounterTable.Column.PATIENT_ID, EncounterTable.Column.ENCOUNTER_TYPE, EncounterTable.Column.ENCOUNTER_DATETIME);
        String[] whereArgs = new String[]{String.valueOf(patientID), Encounter.EncounterType.VITALS.getType()};
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int id_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    int uuid_CI = cursor.getColumnIndex(EncounterTable.Column.UUID);
                    int display_CI = cursor.getColumnIndex(EncounterTable.Column.DISPLAY);
                    int datetime_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_DATETIME);
                    Long id = cursor.getLong(id_CI);
                    String uuid = cursor.getString(uuid_CI);
                    String display = cursor.getString(display_CI);
                    Long datetime = cursor.getLong(datetime_CI);
                    encounter = new Encounter();
                    encounter.setId(id);
                    encounter.setUuid(uuid);
                    encounter.setDisplay(display);
                    encounter.setEncounterDatetime(datetime);
                    encounter.setEncounterType(Encounter.EncounterType.VITALS);
                    encounter.setObservations(new ObservationDAO().findObservationByEncounterID(id));
                }
            } finally {
                cursor.close();
            }
        }
        return encounter;
    }

    public boolean updateEncounter(long encounterID, Encounter encounter, long visitID) {
        encounter.setVisitID(visitID);
        return new EncounterTable().update(encounterID, encounter) > 0;
    }

    public List<Encounter> findEncountersByVisitID(Long visitID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        List<Encounter> encounters = new ArrayList<Encounter>();

        String where = String.format("%s = ?", EncounterTable.Column.VISIT_KEY_ID);
        String[] whereArgs = new String[]{visitID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int id_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    int uuid_CI = cursor.getColumnIndex(EncounterTable.Column.UUID);
                    int display_CI = cursor.getColumnIndex(EncounterTable.Column.DISPLAY);
                    int datetime_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_DATETIME);
                    int encounterType_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_TYPE);
                    Long id = cursor.getLong(id_CI);
                    String uuid = cursor.getString(uuid_CI);
                    String display = cursor.getString(display_CI);
                    Long datetime = cursor.getLong(datetime_CI);
                    Encounter.EncounterType type = Encounter.EncounterType.getType(cursor.getString(encounterType_CI));
                    Encounter encounter = new Encounter();
                    encounter.setId(id);
                    encounter.setVisitID(visitID);
                    encounter.setUuid(uuid);
                    encounter.setDisplay(display);
                    encounter.setEncounterDatetime(datetime);
                    encounter.setEncounterType(type);
                    encounter.setObservations(new ObservationDAO().findObservationByEncounterID(id));
                    encounters.add(encounter);
                }
            } finally {
                cursor.close();
            }
        }

        return encounters;
    }

    public List<Encounter> getAllEncountersByType(Long patientID, Encounter.EncounterType type) {
        List<Encounter> encounters = new ArrayList<Encounter>();
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        String query = "SELECT e.* FROM observations AS o JOIN encounters AS e ON o.encounter_id = e._id " +
                "JOIN visits AS v on e.visit_id = v._id WHERE v.patient_id = ? AND e.type = ? ORDER BY e.encounterDatetime DESC";
        String type1 = type.getType();
        String[] whereArgs = new String[]{patientID.toString(), type1};
        final Cursor cursor = helper.getReadableDatabase().rawQuery(query, whereArgs);

        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int id_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    int uuid_CI = cursor.getColumnIndex(EncounterTable.Column.UUID);
                    int display_CI = cursor.getColumnIndex(EncounterTable.Column.DISPLAY);
                    int datetime_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_DATETIME);
                    Long id = cursor.getLong(id_CI);
                    String uuid = cursor.getString(uuid_CI);
                    String display = cursor.getString(display_CI);
                    Long datetime = cursor.getLong(datetime_CI);
                    Encounter encounter = new Encounter();
                    encounter.setId(id);
                    encounter.setUuid(uuid);
                    encounter.setDisplay(display);
                    encounter.setEncounterDatetime(datetime);
                    encounter.setEncounterType(type);
                    encounter.setObservations(new ObservationDAO().findObservationByEncounterID(id));
                    encounters.add(encounter);
                }
            } finally {
                cursor.close();
            }
        }
        return encounters;
    }

    public long getEncounterIDByUUID(final String encounterUUID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String where = String.format("%s = ?", EncounterTable.Column.UUID);
        String[] whereArgs = new String[]{encounterUUID};
        long encounterID = 0;
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int encounterID_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    encounterID = cursor.getLong(encounterID_CI);
                }
            } finally {
                cursor.close();
            }
        }
        return encounterID;
    }

    public long getEncounterIDByDatetimeAndVisitId(long encounterDatetime, long encounterVisitId) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String where = String.format("%s is NULL AND %s = ? AND %s = ? ", EncounterTable.Column.UUID, EncounterTable.Column.ENCOUNTER_DATETIME, EncounterTable.Column.VISIT_KEY_ID);
        String[] whereArgs = new String[]{String.valueOf(encounterDatetime), String.valueOf(encounterVisitId)};
        long encounterID = 0;
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int encounterID_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    encounterID = cursor.getLong(encounterID_CI);
                }
            } finally {
                cursor.close();
            }
        }
        return encounterID;
    }
}
