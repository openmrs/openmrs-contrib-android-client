package org.openmrs.client.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.databases.tables.EncounterTable;
import org.openmrs.client.databases.tables.LocationTable;
import org.openmrs.client.databases.tables.ObservationTable;
import org.openmrs.client.databases.tables.PatientTable;
import org.openmrs.client.databases.tables.VisitTable;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Location;
import org.openmrs.client.models.Observation;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;

public class DBOpenHelper extends OpenMRSSQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

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
        if (null == mStatement) {
            mStatement = db.compileStatement(mPatientTable.insertIntoTableDefinition());
        }
        try {
            db.beginTransaction();
            bindString(1, patient.getDisplay());
            bindString(2, patient.getUuid());
            bindString(3, patient.getIdentifier());
            bindString(4, patient.getGivenName());
            bindString(5, patient.getMiddleName());
            bindString(6, patient.getFamilyName());
            bindString(7, patient.getGender());
            bindLong(8, patient.getBirthDate());
            bindLong(9, patient.getDeathDate());
            bindString(10, patient.getCauseOfDeath());
            bindString(11, patient.getAge());
            bindString(12, patient.getAddress().getAddress1());
            bindString(13, patient.getAddress().getAddress2());
            bindString(14, patient.getAddress().getPostalCode());
            bindString(15, patient.getAddress().getCountry());
            bindString(16, patient.getAddress().getState());
            bindString(17, patient.getAddress().getCityVillage());
            bindString(18, patient.getPhoneNumber());
            patientId = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }

        return patientId;
    }

    public long insertVisit(SQLiteDatabase db, Visit visit) {
        long visitId;
        if (null == mStatement) {
            mStatement = db.compileStatement(mVisitTable.insertIntoTableDefinition());
        }
        try {
            db.beginTransaction();
            bindString(1, visit.getUuid());
            bindLong(2, visit.getPatientID());
            bindString(3, visit.getVisitType());
            bindString(4, visit.getVisitPlace());
            bindLong(5, visit.getStartDate());
            bindLong(6, visit.getStopDate());
            visitId = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }
        return visitId;
    }

    public long insertEncounter(SQLiteDatabase db, Encounter encounter) {
        long encounterId;
        if (null == mStatement) {
            mStatement = db.compileStatement(mEncounterTable.insertIntoTableDefinition());
        }
        try {
            db.beginTransaction();
            bindLong(1, encounter.getVisitID());
            bindString(2, encounter.getUuid());
            bindString(3, encounter.getDisplay());
            bindLong(4, encounter.getEncounterDatetime());
            bindString(5, encounter.getEncounterType().getType());
            encounterId = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }
        return encounterId;
    }

    public long insertObservation(SQLiteDatabase db, Observation obs) {
        long obsID;
        if (null == mStatement) {
            mStatement = db.compileStatement(mObservationTable.insertIntoTableDefinition());
        }
        try {
            db.beginTransaction();
            bindLong(1, obs.getEncounterID());
            bindString(2, obs.getUuid());
            bindString(3, obs.getDisplay());
            bindString(4, obs.getDisplayValue());
            obsID = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }
        return obsID;
    }

    public Long insertLocation(SQLiteDatabase db, Location loc) {
        long locID;
        if (null == mStatement) {
            mStatement = db.compileStatement(mLocationTable.insertIntoTableDefinition());
        }
        try {
            db.beginTransaction();
            bindString(1, loc.getUuid());
            bindString(2, loc.getDisplay());
            bindString(3, loc.getName());
            bindString(4, loc.getDescription());
            bindString(5, loc.getAddress().getAddress1());
            bindString(6, loc.getAddress().getAddress2());
            bindString(7, loc.getAddress().getCityVillage());
            bindString(8, loc.getAddress().getState());
            bindString(9, loc.getAddress().getCountry());
            bindString(10, loc.getAddress().getPostalCode());
            locID = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }
        return locID;
    }
}
