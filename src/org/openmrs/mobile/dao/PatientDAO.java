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

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.models.Address;
import org.openmrs.mobile.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public long savePatient(Patient patient) {
        return new PatientTable().insert(patient);
    }

    public boolean updatePatient(long patientID, Patient patient) {
        return new PatientTable().update(patientID, patient) > 0;
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
        patient.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PatientTable.Column.PHONE)));
        patient.setAddress(cursorToAddress(cursor));
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
                    patient = cursorToPatient(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return patient;
    }

    public Patient findPatientByID(Long id) {
        Patient patient = new Patient();
        String where = String.format("%s = ?", PatientTable.Column.ID);
        String[] whereArgs = new String[]{String.valueOf(id)};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(PatientTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    patient = cursorToPatient(cursor);
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
