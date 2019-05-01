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
import android.graphics.Bitmap;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.tables.ConceptTable;
import org.openmrs.mobile.databases.tables.EncounterTable;
import org.openmrs.mobile.databases.tables.LocationTable;
import org.openmrs.mobile.databases.tables.ObservationTable;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.databases.tables.Table;
import org.openmrs.mobile.databases.tables.VisitTable;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

public class DBOpenHelper extends OpenMRSSQLiteOpenHelper {
    private static int DATABASE_VERSION = OpenMRS.getInstance().
            getResources().getInteger(R.integer.dbversion);
    private static final String WHERE_ID_CLAUSE = String.format("%s = ?", Table.MasterColumn.ID);

    private PatientTable mPatientTable;
    private ConceptTable mConceptTable;
    private VisitTable mVisitTable;
    private EncounterTable mEncounterTable;
    private ObservationTable mObservationTable;
    private LocationTable mLocationTable;

    public DBOpenHelper(Context context) {
        super(context, null, DATABASE_VERSION);
        this.mPatientTable = new PatientTable();
        this.mConceptTable = new ConceptTable();
        this.mVisitTable = new VisitTable();
        this.mEncounterTable = new EncounterTable();
        this.mObservationTable = new ObservationTable();
        this.mLocationTable = new LocationTable();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mLogger.d("Database creating...");
        sqLiteDatabase.execSQL(mPatientTable.createTableDefinition());
        logOnCreate(mPatientTable.toString());
        sqLiteDatabase.execSQL(mConceptTable.createTableDefinition());
        logOnCreate(mConceptTable.toString());
        sqLiteDatabase.execSQL(mVisitTable.createTableDefinition());
        logOnCreate(mVisitTable.toString());
        sqLiteDatabase.execSQL(mEncounterTable.createTableDefinition());
        logOnCreate(mEncounterTable.toString());
        sqLiteDatabase.execSQL(mObservationTable.createTableDefinition());
        logOnCreate(mObservationTable.toString());
        sqLiteDatabase.execSQL(mLocationTable.createTableDefinition());
        logOnCreate(mLocationTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int currentVersion, int newVersion) {
        switch (currentVersion) {
            case 8:
                sqLiteDatabase.execSQL(new ConceptTable().createTableDefinition());
            case 9:
                //upgrade from version 8 to 10
                //db.execSQL("ALTER TABLE " + %tableName% + " ADD COLUMN " + %columnName + " %columnType%;");

                //and so on.. do not add breaks so that switch will
                //start at oldVersion, and run straight through to the latest
        }
    }

    private void logOnCreate(String tableToString) {
        mLogger.d("Table " + tableToString + " ver." + DATABASE_VERSION + " created");
    }

    public long insertPatient(SQLiteDatabase db, Patient patient) {
        long patientId;

        SQLiteStatement patientStatement = db.compileStatement(mPatientTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindString(1, patient.getPerson().getName().getNameString(), patientStatement);
            bindString(2, Boolean.toString(patient.isSynced()),patientStatement);

            if(patient.getUuid()!=null)
                bindString(3, patient.getUuid(), patientStatement);
            else
                bindString(3, null, patientStatement);

            if(patient.getIdentifier()!=null)
                bindString(4, patient.getIdentifier().getIdentifier(), patientStatement);
            else
                bindString(4, null, patientStatement);

            bindString(5, patient.getPerson().getName().getGivenName(), patientStatement);
            bindString(6, patient.getPerson().getName().getMiddleName(), patientStatement);
            bindString(7, patient.getPerson().getName().getFamilyName(), patientStatement);
            bindString(8, patient.getPerson().getGender(), patientStatement);
            bindString(9, patient.getPerson().getBirthdate(), patientStatement);
            bindLong(10, null, patientStatement);
            bindString(11, null, patientStatement);
            bindString(12, null, patientStatement);
            if (null != patient.getPerson().getPhoto()) {
                bindBlob(13, bitmapToByteArray(patient.getPerson().getPhoto()), patientStatement);
            }
            if (null != patient.getPerson().getAddress()) {
                bindString(14, patient.getPerson().getAddress().getAddress1(), patientStatement);
                bindString(15, patient.getPerson().getAddress().getAddress2(), patientStatement);
                bindString(16, patient.getPerson().getAddress().getPostalCode(), patientStatement);
                bindString(17, patient.getPerson().getAddress().getCountry(), patientStatement);
                bindString(18, patient.getPerson().getAddress().getStateProvince(), patientStatement);
                bindString(19, patient.getPerson().getAddress().getCityVillage(), patientStatement);
            }
            bindString(20, patient.getEncounters(), patientStatement);
            bindString(21, null, patientStatement);
            patientId = patientStatement.executeInsert();
            patientStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            patientStatement.close();
        }

        patient.setId(patientId);

        return patientId;
    }

    public int updatePatient(SQLiteDatabase db, long patientID, Patient patient) {
        ContentValues newValues = new ContentValues();
        newValues.put(PatientTable.Column.UUID, patient.getUuid());
        newValues.put(PatientTable.Column.SYNCED, patient.isSynced());
        newValues.put(PatientTable.Column.DISPLAY, patient.getDisplay());

        newValues.put(PatientTable.Column.IDENTIFIER, patient.getIdentifier().getIdentifier());
        newValues.put(PatientTable.Column.GIVEN_NAME, patient.getPerson().getName().getGivenName());
        newValues.put(PatientTable.Column.MIDDLE_NAME, patient.getPerson().getName().getMiddleName());

        newValues.put(PatientTable.Column.FAMILY_NAME, patient.getPerson().getName().getFamilyName());
        newValues.put(PatientTable.Column.GENDER, patient.getPerson().getGender());
        newValues.put(PatientTable.Column.BIRTH_DATE, patient.getPerson().getBirthdate());

        newValues.put(PatientTable.Column.DEATH_DATE, (Long) null);
        newValues.put(PatientTable.Column.CAUSE_OF_DEATH, (String) null);
        newValues.put(PatientTable.Column.AGE, (String) null);
        if (null != patient.getPerson().getPhoto()) {
            mLogger.i("inserting into db");
            newValues.put(PatientTable.Column.PHOTO, bitmapToByteArray(patient.getPerson().getPhoto()));
        }

        if (null != patient.getPerson().getAddress()) {
            newValues.put(PatientTable.Column.ADDRESS_1, patient.getPerson().getAddress().getAddress1());
            newValues.put(PatientTable.Column.ADDRESS_2, patient.getPerson().getAddress().getAddress2());
            newValues.put(PatientTable.Column.POSTAL_CODE, patient.getPerson().getAddress().getPostalCode());
            newValues.put(PatientTable.Column.COUNTRY, patient.getPerson().getAddress().getCountry());
            newValues.put(PatientTable.Column.STATE, patient.getPerson().getAddress().getStateProvince());
            newValues.put(PatientTable.Column.CITY, patient.getPerson().getAddress().getCityVillage());

        }
        newValues.put(PatientTable.Column.ENCOUNTERS, patient.getEncounters());

        String[] whereArgs = new String[]{String.valueOf(patientID)};

        return db.update(PatientTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public long insertConcept (SQLiteDatabase db, Concept concept) {
        long conceptId;
        SQLiteStatement statement = db.compileStatement(mConceptTable.insertIntoTableDefinition());
        try {
            db.beginTransaction();
            bindString(1, concept.getUuid(), statement);
            bindString(2, concept.getDisplay(), statement);
            conceptId = statement.executeInsert();
            statement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            statement.close();
        }
        return conceptId;
    }

    public int updateConcept (SQLiteDatabase db, long conceptId, Concept concept) {
        ContentValues newValues = new ContentValues();
        newValues.put(ConceptTable.Column.UUID, concept.getUuid());
        newValues.put(ConceptTable.Column.DISPLAY, concept.getDisplay());

        String[] whereArgs = new String[]{String.valueOf(conceptId)};

        return db.update(ConceptTable.TABLE_NAME, newValues, WHERE_ID_CLAUSE, whereArgs);
    }

    public long insertVisit(SQLiteDatabase db, Visit visit) {
        long visitId;

        SQLiteStatement visitStatement = db.compileStatement(mVisitTable.insertIntoTableDefinition());

        try {
            db.beginTransaction();
            bindString(1, visit.getUuid(), visitStatement);
            bindLong(2, visit.getPatient().getId(), visitStatement);
            bindString(3, visit.getVisitType().getDisplay(), visitStatement);
            if (visit.getLocation() != null) {
                bindString(4, visit.getLocation().getDisplay(), visitStatement);
            }
            bindString(5, visit.getStartDatetime(), visitStatement);
            bindString(6, visit.getStopDatetime(), visitStatement);
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
        newValues.put(VisitTable.Column.PATIENT_KEY_ID, visit.getPatient().getId());
        newValues.put(VisitTable.Column.VISIT_TYPE, visit.getVisitType().getDisplay());
        if (visit.getLocation() != null) {
            newValues.put(VisitTable.Column.VISIT_PLACE, visit.getLocation().getDisplay());
        }
        newValues.put(VisitTable.Column.START_DATE, visit.getStartDatetime());
        newValues.put(VisitTable.Column.STOP_DATE, visit.getStopDatetime());

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
            bindString(5, encounter.getEncounterType().getDisplay(), encounterStatement);
            bindString(6, encounter.getPatientUUID(), encounterStatement);
            bindString(7, encounter.getFormUuid(), encounterStatement);
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
        newValues.put(EncounterTable.Column.ENCOUNTER_TYPE, encounter.getEncounterType().getDisplay());

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
                bindString(5, obs.getDiagnosisOrder(), observationStatement);
            }
            bindString(6, obs.getDiagnosisList(), observationStatement);
            if (obs.getDiagnosisCertainty() != null) {
                bindString(7, obs.getDiagnosisCertainty(), observationStatement);
            }
            bindString(8, obs.getDiagnosisNote(), observationStatement);
            if(obs.getConcept() != null){
                bindString(9, obs.getConcept().getUuid(), observationStatement);
            }
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
            newValues.put(ObservationTable.Column.DIAGNOSIS_ORDER, observation.getDiagnosisOrder());
        }
        newValues.put(ObservationTable.Column.DIAGNOSIS_LIST, observation.getDiagnosisList());
        if (observation.getDiagnosisCertainty() != null) {
            newValues.put(ObservationTable.Column.DIAGNOSIS_CERTAINTY, observation.getDiagnosisCertainty());
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
            bindString(5, loc.getAddress1(), locationStatement);
            bindString(6, loc.getAddress2(), locationStatement);
            bindString(7, loc.getCityVillage(), locationStatement);
            bindString(8, loc.getStateProvince(), locationStatement);
            bindString(9, loc.getCountry(), locationStatement);
            bindString(10, loc.getPostalCode(), locationStatement);
            bindString(11, loc.getParentLocationUuid(), locationStatement);
            locID = locationStatement.executeInsert();
            locationStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            locationStatement.close();
        }
        return locID;
    }

    public static <T> Observable<T> createObservableIO(final Callable<T> func) {
        return Observable.fromCallable(func)
                .subscribeOn(Schedulers.io());
    }
    private byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
