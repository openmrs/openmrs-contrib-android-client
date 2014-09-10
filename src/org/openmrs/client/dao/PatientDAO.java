package org.openmrs.client.dao;


import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.models.Address;
import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private String[] mPatientColumns = {
            DBOpenHelper.COLUMN_ID, DBOpenHelper.COLUMN_DISPLAY,
            DBOpenHelper.COLUMN_UUID, DBOpenHelper.COLUMN_IDENTIFIER,
            DBOpenHelper.COLUMN_GIVEN_NAME, DBOpenHelper.COLUMN_MIDDLE_NAME,
            DBOpenHelper.COLUMN_FAMILY_NAME, DBOpenHelper.COLUMN_GENDER,
            DBOpenHelper.COLUMN_BIRTH_DATE, DBOpenHelper.COLUMN_DEATH_DATE,
            DBOpenHelper.COLUMN_CAUSE_OF_DEATH, DBOpenHelper.COLUMN_AGE};

    public long savePatient(Patient patient) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        return helper.insertPatient(db, patient);
    }

    public void deletePatient(long id) {
        OpenMRS.getInstance().getOpenMRSLogger().w("Patient deleted with id: " + id);
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getReadableDatabase().delete(DBOpenHelper.PATIENTS_TABLE_NAME, DBOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Cursor cursor = openHelper.getReadableDatabase().query(DBOpenHelper.PATIENTS_TABLE_NAME,
                null, null, null, null, null, null);

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
        patient.setBirthDate(cursor.getLong(cursor.getColumnIndex(mPatientColumns[8])));
        patient.setDeathDate(cursor.getLong(cursor.getColumnIndex(mPatientColumns[9])));
        patient.setCauseOfDeath(cursor.getString(cursor.getColumnIndex(mPatientColumns[10])));
        patient.setAge(cursor.getString(cursor.getColumnIndex(mPatientColumns[11])));
        return patient;
    }

    public boolean isUserAlreadySaved(String uuid) {
        String where = String.format("%s = ?", DBOpenHelper.COLUMN_UUID);
        String[] whereArgs = new String[]{uuid};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(DBOpenHelper.PATIENTS_TABLE_NAME, null, where, whereArgs, null, null, null);
        String patientUUID = "";
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int uuidColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_UUID);
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
        String where = String.format("%s = ?", DBOpenHelper.COLUMN_UUID);
        String[] whereArgs = new String[]{uuid};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(DBOpenHelper.PATIENTS_TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int patientIdColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_ID);
                    int displayColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_DISPLAY);
                    int identifierColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_IDENTIFIER);
                    int givenNameColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_GIVEN_NAME);
                    int familyNameColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_FAMILY_NAME);
                    int genderColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_GENDER);
                    int ageColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_AGE);
                    int birthDateColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_BIRTH_DATE);
                    int phoneColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_PHONE);
                    patient.setId(cursor.getLong(patientIdColumnIndex));
                    patient.setDisplay(cursor.getString(displayColumnIndex));
                    patient.setIdentifier(cursor.getString(identifierColumnIndex));
                    patient.setGivenName(cursor.getString(givenNameColumnIndex));
                    patient.setFamilyName(cursor.getString(familyNameColumnIndex));
                    patient.setGender(cursor.getString(genderColumnIndex));
                    patient.setAge(cursor.getString(ageColumnIndex));
                    patient.setBirthDate(cursor.getLong(birthDateColumnIndex));
                    patient.setAddress(cursorToAddress(cursor));
                    patient.setPhoneNumber(cursor.getString(phoneColumnIndex));
                }
            } finally {
                cursor.close();
            }
        }
        return patient;
    }

    private Address cursorToAddress(Cursor cursor) {
        int address1ColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_ADDRESS_1);
        int address2ColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_ADDRESS_2);
        int postalColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_POSTAL_CODE);
        int countryColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRY);
        int stateColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_STATE);
        int cityColumnIndex = cursor.getColumnIndex(DBOpenHelper.COLUMN_CITY);
        return new Address(cursor.getString(address1ColumnIndex), cursor.getString(address2ColumnIndex),
                cursor.getString(postalColumnIndex), cursor.getString(cityColumnIndex),
                cursor.getString(countryColumnIndex), cursor.getString(stateColumnIndex));
    }
}
