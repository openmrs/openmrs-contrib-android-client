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
import org.openmrs.mobile.databases.tables.ObservationTable;
import org.openmrs.mobile.models.Observation;

import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {

    public void saveObservation(Observation observation, long encounterID) {
        observation.setEncounterID(encounterID);
        new ObservationTable().insert(observation);
    }

    public boolean updateObservation(long observationID, Observation observation, long encounterID) {
        observation.setEncounterID(encounterID);
        return new ObservationTable().update(observationID, observation) > 0;
    }

    public void deleteObservation(long observationID) {
       new ObservationTable().delete(observationID);
    }

    public List<Observation> findObservationByEncounterID(Long encounterID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        List<Observation> observationList = new ArrayList<Observation>();

        String where = String.format("%s = ?", ObservationTable.Column.ENCOUNTER_KEY_ID);
        String[] whereArgs = new String[]{encounterID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(ObservationTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int obsID_CI = cursor.getColumnIndex(ObservationTable.Column.ID);
                    int obsUUID_CI = cursor.getColumnIndex(ObservationTable.Column.UUID);
                    int obsDisplay_CI = cursor.getColumnIndex(ObservationTable.Column.DISPLAY);
                    int obsDisplayValue_CI = cursor.getColumnIndex(ObservationTable.Column.DISPLAY_VALUE);
                    int obsDiagnosisOrder_CI = cursor.getColumnIndex(ObservationTable.Column.DIAGNOSIS_ORDER);
                    int obsDiagnosisList_CI = cursor.getColumnIndex(ObservationTable.Column.DIAGNOSIS_LIST);
                    int obsDiagnosisCertainty_CI = cursor.getColumnIndex(ObservationTable.Column.DIAGNOSIS_CERTAINTY);
                    int obsDiagnosisNote_CI = cursor.getColumnIndex(ObservationTable.Column.DIAGNOSIS_NOTE);
                    Long obsID = cursor.getLong(obsID_CI);
                    String obsUUID = cursor.getString(obsUUID_CI);
                    String obsDisplay = cursor.getString(obsDisplay_CI);
                    String obsDisplayValue = cursor.getString(obsDisplayValue_CI);
                    String obsDiagnosisOrder = cursor.getString(obsDiagnosisOrder_CI);
                    String obsDiagnosisList = cursor.getString(obsDiagnosisList_CI);
                    String obsDiagnosisCertainty = cursor.getString(obsDiagnosisCertainty_CI);
                    String obsDiagnosisNote = cursor.getString(obsDiagnosisNote_CI);
                    Observation obs = new Observation();
                    obs.setId(obsID);
                    obs.setEncounterID(encounterID);
                    obs.setUuid(obsUUID);
                    obs.setDisplay(obsDisplay);
                    obs.setDisplayValue(obsDisplayValue);
                    if (obsDiagnosisOrder != null) {
                        obs.setDiagnosisOrder(Observation.DiagnosisOrder.getOrder(obsDiagnosisOrder));
                    }
                    obs.setDiagnosisList(obsDiagnosisList);
                    if (obsDiagnosisCertainty != null) {
                        obs.setDiagnosisCertainty(Observation.DiagnosisCertainty.getCertainty(obsDiagnosisCertainty));
                    }
                    obs.setDiagnosisNote(obsDiagnosisNote);
                    observationList.add(obs);
                }
            } finally {
                cursor.close();
            }
        }
        return observationList;
    }


    public Observation getObservationByUUID(final String observationUUID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();

        String where = String.format("%s = ?", ObservationTable.Column.UUID);
        String[] whereArgs = new String[]{observationUUID};
        Observation obs = new Observation();
        final Cursor cursor = helper.getReadableDatabase().query(ObservationTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    int observationID_CI = cursor.getColumnIndex(ObservationTable.Column.ID);
                    int encounterID_CI = cursor.getColumnIndex(ObservationTable.Column.ENCOUNTER_KEY_ID);
                    Long obsID = cursor.getLong(observationID_CI);
                    Long encounterID = cursor.getLong(encounterID_CI);
                    obs.setId(obsID);
                    obs.setEncounterID(encounterID);
                }
            } finally {
                cursor.close();
            }
        }
        return obs;
    }
}
