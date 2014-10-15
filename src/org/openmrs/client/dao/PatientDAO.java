package org.openmrs.client.dao;


import net.sqlcipher.Cursor;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.PatientTable;
import org.openmrs.client.models.Address;
import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public long savePatient(Patient patient) {
        return new PatientTable().insert(patient);
    }

    public void deletePatient(long id) {
        OpenMRS.getInstance().getOpenMRSLogger().w("Patient deleted with id: " + id);
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getReadableDatabase().delete(PatientTable.TABLE_NAME, PatientTable.Column.ID
                + " = " + id, null);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Cursor cursor = openHelper.getReadableDatabase().query(PatientTable.TABLE_NAME,
                null, null, null, null, null, null);

        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    Patient patient = cursorToPatient(cursor);
                    patients.add(patient);
                }
            } finally {
                cursor.close();
            }
        }
        return patients;
    }

    private Patient cursorToPatient(Cursor cursor) {
        Patient patient = new Patient();
        patient.setId(cursor.getLong(cursor.getColumnIndex(PatientTable.Column.ID)));
        patient.setDisplay(cursor.getString(cursor.getColumnIndex(PatientTable.Column.DISPLAY)));
        patient.setUuid(cursor.getString(cursor.getColumnIndex(PatientTable.Column.UUID)));
        patient.setIdentifier(cursor.getString(cursor.getColumnIndex(PatientTable.Column.IDENTIFIER)));
        patient.setGivenName(cursor.getString(cursor.getColumnIndex(PatientTable.Column.GIVEN_NAME)));
        patient.setMiddleName(cursor.getString(cursor.getColumnIndex(PatientTable.Column.MIDDLE_NAME)));
        patient.setFamilyName(cursor.getString(cursor.getColumnIndex(PatientTable.Column.FAMILY_NAME)));
        patient.setGender(cursor.getString(cursor.getColumnIndex(PatientTable.Column.GENDER)));
        patient.setBirthDate(cursor.getLong(cursor.getColumnIndex(PatientTable.Column.BIRTH_DATE)));
        patient.setDeathDate(cursor.getLong(cursor.getColumnIndex(PatientTable.Column.DEATH_DATE)));
        patient.setCauseOfDeath(cursor.getString(cursor.getColumnIndex(PatientTable.Column.CAUSE_OF_DEATH)));
        patient.setAge(cursor.getString(cursor.getColumnIndex(PatientTable.Column.AGE)));
        return patient;
    }

    public boolean isUserAlreadySaved(String uuid) {
        String where = String.format("%s = ?", PatientTable.Column.UUID);
        String[] whereArgs = new String[]{uuid};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(PatientTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        String patientUUID = "";
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int uuidColumnIndex = cursor.getColumnIndex(PatientTable.Column.UUID);
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
        String where = String.format("%s = ?", PatientTable.Column.UUID);
        String[] whereArgs = new String[]{uuid};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(PatientTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int patientIdColumnIndex = cursor.getColumnIndex(PatientTable.Column.ID);
                    int displayColumnIndex = cursor.getColumnIndex(PatientTable.Column.DISPLAY);
                    int identifierColumnIndex = cursor.getColumnIndex(PatientTable.Column.IDENTIFIER);
                    int givenNameColumnIndex = cursor.getColumnIndex(PatientTable.Column.GIVEN_NAME);
                    int familyNameColumnIndex = cursor.getColumnIndex(PatientTable.Column.FAMILY_NAME);
                    int genderColumnIndex = cursor.getColumnIndex(PatientTable.Column.GENDER);
                    int ageColumnIndex = cursor.getColumnIndex(PatientTable.Column.AGE);
                    int birthDateColumnIndex = cursor.getColumnIndex(PatientTable.Column.BIRTH_DATE);
                    int phoneColumnIndex = cursor.getColumnIndex(PatientTable.Column.PHONE);
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
        int address1ColumnIndex = cursor.getColumnIndex(PatientTable.Column.ADDRESS_1);
        int address2ColumnIndex = cursor.getColumnIndex(PatientTable.Column.ADDRESS_2);
        int postalColumnIndex = cursor.getColumnIndex(PatientTable.Column.POSTAL_CODE);
        int countryColumnIndex = cursor.getColumnIndex(PatientTable.Column.COUNTRY);
        int stateColumnIndex = cursor.getColumnIndex(PatientTable.Column.STATE);
        int cityColumnIndex = cursor.getColumnIndex(PatientTable.Column.CITY);
        return new Address(cursor.getString(address1ColumnIndex), cursor.getString(address2ColumnIndex),
                cursor.getString(postalColumnIndex), cursor.getString(cityColumnIndex),
                cursor.getString(countryColumnIndex), cursor.getString(stateColumnIndex));
    }
}
