package org.openmrs.client.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public final class PatientDataSource {

    private SQLiteDatabase mDatabase;
    private OpenmrsSQLiteHelper mDbHelper;
    private String[] mAllColumns = {OpenmrsSQLiteHelper.COLUMN_ID,
            OpenmrsSQLiteHelper.COLUMN_UUID, OpenmrsSQLiteHelper.COLUMN_IDENTIFIER,
            OpenmrsSQLiteHelper.COLUMN_GIVEN_NAME, OpenmrsSQLiteHelper.COLUMN_MIDDLE_NAME,
            OpenmrsSQLiteHelper.COLUMN_FAMILY_NAME, OpenmrsSQLiteHelper.COLUMN_GENDER,
            OpenmrsSQLiteHelper.COLUMN_BIRTH_DATE, OpenmrsSQLiteHelper.COLUMN_DEATH_DATE,
            OpenmrsSQLiteHelper.COLUMN_CAUSE_OF_DEATH};
    private static PatientDataSource instance;

    public static synchronized PatientDataSource getDataSource(Context context)
    {
        if (instance == null) {
            instance = new PatientDataSource(context);
        }

        return instance;
    }


    private PatientDataSource(Context context) {
        mDbHelper = new OpenmrsSQLiteHelper(context);
    }

    public void open(String password) throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase(password);
    }

    public void close() {
        mDbHelper.close();
    }

    public Patient addPatient(String uuid, String identifier, String givenName,
                              String familyName, String gender, String birthDate) {
        ContentValues values = new ContentValues();
        values.put(OpenmrsSQLiteHelper.COLUMN_UUID, uuid);
        values.put(OpenmrsSQLiteHelper.COLUMN_IDENTIFIER, identifier);
        values.put(OpenmrsSQLiteHelper.COLUMN_GIVEN_NAME, givenName);
        values.put(OpenmrsSQLiteHelper.COLUMN_FAMILY_NAME, familyName);
        values.put(OpenmrsSQLiteHelper.COLUMN_GENDER, gender);
        values.put(OpenmrsSQLiteHelper.COLUMN_BIRTH_DATE, birthDate);
        long insertId = mDatabase.insert(OpenmrsSQLiteHelper.TABLE_PATIENTS, null,
                values);
        Cursor cursor = mDatabase.query(OpenmrsSQLiteHelper.TABLE_PATIENTS,
                mAllColumns, OpenmrsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Patient patient = cursorToPatient(cursor);
        cursor.close();
        return patient;
    }

    public void deletePatient(Patient patient) {
        long id = patient.getId();
        Log.w(this.getClass().getCanonicalName(), "Comment deleted with id: " + id);
        mDatabase.delete(OpenmrsSQLiteHelper.TABLE_PATIENTS, OpenmrsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();

        Cursor cursor = mDatabase.query(OpenmrsSQLiteHelper.TABLE_PATIENTS,
                mAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient patient = cursorToPatient(cursor);
            patients.add(patient);
            cursor.moveToNext();
        }

        cursor.close();
        return patients;
    }

    private Patient cursorToPatient(Cursor cursor) {
        Patient patient = new Patient();
        patient.setId(cursor.getLong(0));
        patient.setUuid(cursor.getString(1));
        patient.setIdentifier(cursor.getString(2));
        patient.setGivenName(cursor.getString(3));
        patient.setMiddleName(cursor.getString(4));
        patient.setFamilyName(cursor.getString(5));
        patient.setGender(cursor.getString(6));
        patient.setBirthDate(cursor.getString(7));
        patient.setDeathDate(cursor.getString(8));
        patient.setCauseOfDeath(cursor.getString(9));
        return patient;
    }
}
