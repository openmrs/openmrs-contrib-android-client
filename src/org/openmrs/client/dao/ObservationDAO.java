package org.openmrs.client.dao;

import net.sqlcipher.Cursor;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.ObservationTable;
import org.openmrs.client.databases.tables.VisitTable;
import org.openmrs.client.models.Observation;
import org.openmrs.client.models.Visit;

import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {

    public void saveObservation(Observation observation, long encounterID) {
        observation.setEncounterID(encounterID);
        new ObservationTable().insert(observation);
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
                    Long obsID = cursor.getLong(obsID_CI);
                    String obsUUID = cursor.getString(obsUUID_CI);
                    String obsDisplay = cursor.getString(obsDisplay_CI);
                    String obsDisplayValue = cursor.getString(obsDisplayValue_CI);
                    Observation obs = new Observation();
                    obs.setId(obsID);
                    obs.setEncounterID(encounterID);
                    obs.setUuid(obsUUID);
                    obs.setDisplay(obsDisplay);
                    obs.setDisplayValue(obsDisplayValue);
                    observationList.add(obs);
                }
            } finally {
                cursor.close();
            }
        }
        return observationList;
    }
}
