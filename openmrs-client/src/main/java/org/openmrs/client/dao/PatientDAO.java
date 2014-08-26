package org.openmrs.client.dao;

import net.sqlcipher.Cursor;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.PatientSQLiteHelper;
import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private final PatientSQLiteHelper mPatientSQLiteHelper;

    private String[] mPatientColumns = {
            PatientSQLiteHelper.COLUMN_ID, PatientSQLiteHelper.COLUMN_DISPLAY,
            PatientSQLiteHelper.COLUMN_UUID, PatientSQLiteHelper.COLUMN_IDENTIFIER,
            PatientSQLiteHelper.COLUMN_GIVEN_NAME, PatientSQLiteHelper.COLUMN_MIDDLE_NAME,
            PatientSQLiteHelper.COLUMN_FAMILY_NAME, PatientSQLiteHelper.COLUMN_GENDER,
            PatientSQLiteHelper.COLUMN_BIRTH_DATE, PatientSQLiteHelper.COLUMN_DEATH_DATE,
            PatientSQLiteHelper.COLUMN_CAUSE_OF_DEATH};

    public PatientDAO() {
        mPatientSQLiteHelper = new OpenMRSDBOpenHelper().getPatientSQLiteHelper();
    }

    public void deletePatient(long id) {
        OpenMRS.getInstance().getOpenMRSLogger().w("Patient deleted with id: " + id);
        mPatientSQLiteHelper.getWritableDatabase().delete(PatientSQLiteHelper.TABLE_NAME, PatientSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<Patient>();

        Cursor cursor = mPatientSQLiteHelper.getWritableDatabase().query(PatientSQLiteHelper.TABLE_NAME,
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
        return patient;
    }

}
