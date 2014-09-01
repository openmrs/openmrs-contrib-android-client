package org.openmrs.client.dao;


import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.PatientSQLiteHelper;
import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private String[] mPatientColumns = {
            PatientSQLiteHelper.COLUMN_ID, PatientSQLiteHelper.COLUMN_DISPLAY,
            PatientSQLiteHelper.COLUMN_UUID, PatientSQLiteHelper.COLUMN_IDENTIFIER,
            PatientSQLiteHelper.COLUMN_GIVEN_NAME, PatientSQLiteHelper.COLUMN_MIDDLE_NAME,
            PatientSQLiteHelper.COLUMN_FAMILY_NAME, PatientSQLiteHelper.COLUMN_GENDER,
            PatientSQLiteHelper.COLUMN_BIRTH_DATE, PatientSQLiteHelper.COLUMN_DEATH_DATE,
            PatientSQLiteHelper.COLUMN_CAUSE_OF_DEATH, PatientSQLiteHelper.COLUMN_AGE};

    public void savePatient(Patient patient) {
        PatientSQLiteHelper helper = OpenMRSDBOpenHelper.getInstance().getPatientSQLiteHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.insert(db, patient);
    }

    public void deletePatient(long id) {
        OpenMRS.getInstance().getOpenMRSLogger().w("Patient deleted with id: " + id);
        PatientSQLiteHelper patientSQLiteHelper = OpenMRSDBOpenHelper.getInstance().getPatientSQLiteHelper();
        patientSQLiteHelper.getReadableDatabase().delete(PatientSQLiteHelper.TABLE_NAME, PatientSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();
        PatientSQLiteHelper patientSQLiteHelper = OpenMRSDBOpenHelper.getInstance().getPatientSQLiteHelper();
        Cursor cursor = patientSQLiteHelper.getReadableDatabase().query(PatientSQLiteHelper.TABLE_NAME,
                mPatientColumns, null, null, null, null, null);

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
        patient.setId(cursor.getLong(cursor.getColumnIndex(mPatientColumns[0])));
        patient.setDisplay(cursor.getString(cursor.getColumnIndex(mPatientColumns[1])));
        patient.setUuid(cursor.getString(cursor.getColumnIndex(mPatientColumns[2])));
        patient.setIdentifier(cursor.getString(cursor.getColumnIndex(mPatientColumns[3])));
        patient.setGivenName(cursor.getString(cursor.getColumnIndex(mPatientColumns[4])));
        patient.setMiddleName(cursor.getString(cursor.getColumnIndex(mPatientColumns[5])));
        patient.setFamilyName(cursor.getString(cursor.getColumnIndex(mPatientColumns[6])));
        patient.setGender(cursor.getString(cursor.getColumnIndex(mPatientColumns[7])));
        patient.setBirthDate(cursor.getString(cursor.getColumnIndex(mPatientColumns[8])));
        patient.setDeathDate(cursor.getString(cursor.getColumnIndex(mPatientColumns[9])));
        patient.setCauseOfDeath(cursor.getString(cursor.getColumnIndex(mPatientColumns[10])));
        patient.setAge(cursor.getString(cursor.getColumnIndex(mPatientColumns[11])));
        return patient;
    }

    public boolean isUserAlreadySaved(String uuid) {
        String where = String.format("%s = ?", PatientSQLiteHelper.COLUMN_UUID);
        String[] whereArgs = new String[]{uuid};

        PatientSQLiteHelper helper = OpenMRSDBOpenHelper.getInstance().getPatientSQLiteHelper();
        final Cursor cursor = helper.getReadableDatabase().query(PatientSQLiteHelper.TABLE_NAME, null, where, whereArgs, null, null, null);
        String patientUUID = "";
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int uuidColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_UUID);
                    patientUUID = cursor.getString(uuidColumnIndex);
                }
            } finally {
                cursor.close();
            }
        }
        return uuid.equalsIgnoreCase(patientUUID);
    }

    public boolean userDoesNotExist(String uuid) {
        return !isUserAlreadySaved(uuid);
    }

    public Patient findPatientByUUID(String uuid) {
        Patient patient = new Patient();
        String where = String.format("%s = ?", PatientSQLiteHelper.COLUMN_UUID);
        String[] whereArgs = new String[]{uuid};

        PatientSQLiteHelper helper = OpenMRSDBOpenHelper.getInstance().getPatientSQLiteHelper();
        final Cursor cursor = helper.getReadableDatabase().query(PatientSQLiteHelper.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int displayColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_DISPLAY);
                    int identifierColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_IDENTIFIER);
                    int givenNameColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_GIVEN_NAME);
                    int familyNameColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_FAMILY_NAME);
                    int genderColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_GENDER);
                    int ageColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_AGE);
                    int birthDateColumnIndex = cursor.getColumnIndex(PatientSQLiteHelper.COLUMN_BIRTH_DATE);
                    patient.setDisplay(cursor.getString(displayColumnIndex));
                    patient.setIdentifier(cursor.getString(identifierColumnIndex));
                    patient.setGivenName(cursor.getString(givenNameColumnIndex));
                    patient.setFamilyName(cursor.getString(familyNameColumnIndex));
                    patient.setGender(cursor.getString(genderColumnIndex));
                    patient.setAge(cursor.getString(ageColumnIndex));
                    patient.setBirthDate(cursor.getString(birthDateColumnIndex));
                }
            } finally {
                cursor.close();
            }
        }
        return patient;
    }
}
