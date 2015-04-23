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

package org.openmrs.mobile.databases;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.mobile.databases.tables.EncounterTable;
import org.openmrs.mobile.databases.tables.LocationTable;
import org.openmrs.mobile.databases.tables.ObservationTable;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.databases.tables.VisitTable;
import org.openmrs.mobile.databases.tables.Table;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

public class DBOpenHelper extends OpenMRSSQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String WHERE_ID_CLAUSE = String.format("%s = ?", Table.MasterColumn.ID);

    private PatientTable mPatientTable;
    private VisitTable mVisitTable;
    private EncounterTable mEncounterTable;
    private ObservationTable mObservationTable;
    private LocationTable mLocationTable;

    public DBOpenHelper(Context context) {
        super(context, null, DATABASE_VERSION);
        this.mPatientTable = new PatientTable();
        this.mVisitTable = new VisitTable();
        this.mEncounterTable = new EncounterTable();
        this.mObservationTable = new ObservationTable();
        this.mLocationTable = new LocationTable();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mLogger.d("Database creating...");
        sqLiteDatabase.execSQL(mPatientTable.crateTableDefinition());
        logOnCreate(mPatientTable.toString());
        sqLiteDatabase.execSQL(mVisitTable.crateTableDefinition());
        logOnCreate(mVisitTable.toString());
        sqLiteDatabase.execSQL(mEncounterTable.crateTableDefinition());
        logOnCreate(mEncounterTable.toString());
        sqLiteDatabase.execSQL(mObservationTable.crateTableDefinition());
        logOnCreate(mObservationTable.toString());
        sqLiteDatabase.execSQL(mLocationTable.crateTableDefinition());
        logOnCreate(mLocationTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int currentVersion, int newVersion) {
        mLogger.w("Upgrading database from version " + currentVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL(mPatientTable.dropTableDefinition());
        sqLiteDatabase.execSQL(mVisitTable.dropTableDefinition());
        sqLiteDatabase.execSQL(mEncounterTable.dropTableDefinition());
        sqLiteDatabase.execSQL(mObservationTable.dropTableDefinition());
        sqLiteDatabase.execSQL(mLocationTable.dropTableDefinition());
        onCreate(sqLiteDatabase);
    }

    private void logOnCreate(String tableToString) {
        mLogger.d("Table " + tableToString + " ver." + DATABASE_VERSION + " created");
    }

    public long insertPatient(SQLiteDatabase db, Patient patient) {
        long patientId;

        SQLiteStatement patientStatement = db.compileStatement(mPatientTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindString(1, patient.getDisplay(), patientStatement);
            bindString(2, patient.getUuid(), patientStatement);
            bindString(3, patient.getIdentifier(), patientStatement);
            bindString(4, patient.getGivenName(), patientStatement);
            bindString(5, patient.getMiddleName(), patientStatement);
            bindString(6, patient.getFamilyName(), patientStatement);
            bindString(7, patient.getGender(), patientStatement);
            bindLong(8, patient.getBirthDate(), patientStatement);
            bindLong(9, patient.getDeathDate(), patientStatement);
            bindString(10, patient.getCauseOfDeath(), patientStatement);
            bindString(11, patient.getAge(), patientStatement);
            if (null != patient.getAddress()) {
                bindString(12, patient.getAddress().getAddress1(), patientStatement);
                bindString(13, patient.getAddress().getAddress2(), patientStatement);
                bindString(14, patient.getAddress().getPostalCode(), patientStatement);
                bindString(15, patient.getAddress().getCountry(), patientStatement);
                bindString(16, patient.getAddress().getState(), patientStatement);
                bindString(17, patient.getAddress().getCityVillage(), patientStatement);
            }
            bindString(18, patient.getPhoneNumber(), patientStatement);
            patientId = patientStatement.executeInsert();
            patientStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            patientStatement.close();
        }

        return patientId;
    }

    public int updatePatient(SQLiteDatabase db, long patientID, Patient patient) {
        ContentValues newValues = new ContentValues();
        newValues.put(PatientTable.Column.UUID, patient.getUuid());
        newValues.put(PatientTable.Column.DISPLAY, patient.getDisplay());
        newValues.put(PatientTable.Column.IDENTIFIER, patient.getIdentifier());
        newValues.put(PatientTable.Column.GIVEN_NAME, patient.getGivenName());
        newValues.put(PatientTable.Column.MIDDLE_NAME, patient.getMiddleName());
        newValues.put(PatientTable.Column.FAMILY_NAME, patient.getFamilyName());
        newValues.put(PatientTable.Column.GENDER, patient.getGender());
        newValues.put(PatientTable.Column.BIRTH_DATE, patient.getBirthDate());
        newValues.put(PatientTable.Column.DEATH_DATE, patient.getDeathDate());
        newValues.put(PatientTable.Column.CAUSE_OF_DEATH, patient.getCauseOfDeath());
        newValues.put(PatientTable.Column.AGE, patient.getAge());
        if (null != patient.getAddress()) {
            newValues.put(PatientTable.Column.ADDRESS_1, patient.getAddress().getAddress1());
            newValues.put(PatientTable.Column.ADDRESS_2, patient.getAddress().getAddress2());
            newValues.put(PatientTable.Column.POSTAL_CODE, patient.getAddress().getPostalCode());
            newValues.put(PatientTable.Column.COUNTRY, patient.getAddress().getCountry());
            newValues.put(PatientTable.Column.STATE, patient.getAddress().getState());
            newValues.put(PatientTable.Column.CITY, patient.getAddress().getCityVillage());
        }
        newValues.put(PatientTable.Column.PHONE, patient.getPhoneNumber());

        String[] whereArgs = new String[]{String.valueOf(patientID)};

        return db.update(PatientTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public long insertVisit(SQLiteDatabase db, Visit visit) {
        long visitId;

        SQLiteStatement visitStatement = db.compileStatement(mVisitTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindString(1, visit.getUuid(), visitStatement);
            bindLong(2, visit.getPatientID(), visitStatement);
            bindString(3, visit.getVisitType(), visitStatement);
            bindString(4, visit.getVisitPlace(), visitStatement);
            bindLong(5, visit.getStartDate(), visitStatement);
            bindLong(6, visit.getStopDate(), visitStatement);
            visitId = visitStatement.executeInsert();
            visitStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            visitStatement.close();
        }
        return visitId;
    }

    public int updateVisit(SQLiteDatabase db, long visitID, Visit visit) {
        ContentValues newValues = new ContentValues();
        newValues.put(VisitTable.Column.UUID, visit.getUuid());
        newValues.put(VisitTable.Column.PATIENT_KEY_ID, visit.getPatientID());
        newValues.put(VisitTable.Column.VISIT_TYPE, visit.getVisitType());
        newValues.put(VisitTable.Column.VISIT_PLACE, visit.getVisitPlace());
        newValues.put(VisitTable.Column.START_DATE, visit.getStartDate());
        newValues.put(VisitTable.Column.STOP_DATE, visit.getStopDate());

        String[] whereArgs = new String[]{String.valueOf(visitID)};

        return db.update(VisitTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public long insertEncounter(SQLiteDatabase db, Encounter encounter) {
        long encounterId;

        SQLiteStatement encounterStatement = db.compileStatement(mEncounterTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindLong(1, encounter.getVisitID(), encounterStatement);
            bindString(2, encounter.getUuid(), encounterStatement);
            bindString(3, encounter.getDisplay(), encounterStatement);
            bindLong(4, encounter.getEncounterDatetime(), encounterStatement);
            bindString(5, encounter.getEncounterType().getType(), encounterStatement);
            bindLong(6, encounter.getPatientID(), encounterStatement);
            encounterId = encounterStatement.executeInsert();
            encounterStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            encounterStatement.close();
        }
        return encounterId;
    }

    public int updateEncounter(SQLiteDatabase db, long encounterID, Encounter encounter) {
        ContentValues newValues = new ContentValues();
        newValues.put(EncounterTable.Column.UUID, encounter.getUuid());
        newValues.put(EncounterTable.Column.VISIT_KEY_ID, encounter.getVisitID());
        newValues.put(EncounterTable.Column.DISPLAY, encounter.getDisplay());
        newValues.put(EncounterTable.Column.ENCOUNTER_DATETIME, encounter.getEncounterDatetime());
        newValues.put(EncounterTable.Column.ENCOUNTER_TYPE, encounter.getEncounterType().getType());

        String[] whereArgs = new String[]{String.valueOf(encounterID)};

        return db.update(EncounterTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public long insertObservation(SQLiteDatabase db, Observation obs) {
        long obsID;
        SQLiteStatement observationStatement = db.compileStatement(mObservationTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindLong(1, obs.getEncounterID(), observationStatement);
            bindString(2, obs.getUuid(), observationStatement);
            bindString(3, obs.getDisplay(), observationStatement);
            bindString(4, obs.getDisplayValue(), observationStatement);
            if (obs.getDiagnosisOrder() != null) {
                bindString(5, obs.getDiagnosisOrder().getOrder(), observationStatement);
            }
            bindString(6, obs.getDiagnosisList(), observationStatement);
            if (obs.getDiagnosisCertainty() != null) {
                bindString(7, obs.getDiagnosisCertainty().getCertainty(), observationStatement);
            }
            bindString(8, obs.getDiagnosisNote(), observationStatement);
            obsID = observationStatement.executeInsert();
            observationStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            observationStatement.close();
        }
        return obsID;
    }

    public int updateObservation(SQLiteDatabase db, long observationID, Observation observation) {
        ContentValues newValues = new ContentValues();
        newValues.put(ObservationTable.Column.UUID, observation.getUuid());
        newValues.put(ObservationTable.Column.ENCOUNTER_KEY_ID, observation.getEncounterID());
        newValues.put(ObservationTable.Column.DISPLAY, observation.getDisplay());
        newValues.put(ObservationTable.Column.DISPLAY_VALUE, observation.getDisplayValue());
        if (observation.getDiagnosisOrder() != null) {
            newValues.put(ObservationTable.Column.DIAGNOSIS_ORDER, observation.getDiagnosisOrder().getOrder());
        }
        newValues.put(ObservationTable.Column.DIAGNOSIS_LIST, observation.getDiagnosisList());
        if (observation.getDiagnosisCertainty() != null) {
            newValues.put(ObservationTable.Column.DIAGNOSIS_CERTAINTY, observation.getDiagnosisCertainty().getCertainty());
        }
        newValues.put(ObservationTable.Column.DIAGNOSIS_NOTE, observation.getDiagnosisNote());

        String[] whereArgs = new String[]{String.valueOf(observationID)};

        return db.update(ObservationTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public Long insertLocation(SQLiteDatabase db, Location loc) {
        long locID;

        SQLiteStatement locationStatement = db.compileStatement(mLocationTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindString(1, loc.getUuid(), locationStatement);
            bindString(2, loc.getDisplay(), locationStatement);
            bindString(3, loc.getName(), locationStatement);
            bindString(4, loc.getDescription(), locationStatement);
            bindString(5, loc.getAddress().getAddress1(), locationStatement);
            bindString(6, loc.getAddress().getAddress2(), locationStatement);
            bindString(7, loc.getAddress().getCityVillage(), locationStatement);
            bindString(8, loc.getAddress().getState(), locationStatement);
            bindString(9, loc.getAddress().getCountry(), locationStatement);
            bindString(10, loc.getAddress().getPostalCode(), locationStatement);
            bindString(11, loc.getParentLocationUuid(), locationStatement);
            bindString(12, loc.getParentLocationDisplay(), locationStatement);
            locID = locationStatement.executeInsert();
            locationStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            locationStatement.close();
        }
        return locID;
    }
}
