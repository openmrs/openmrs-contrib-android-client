package org.openmrs.client.dao;

import net.sqlcipher.Cursor;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.EncounterTable;
import org.openmrs.client.databases.tables.ObservationTable;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Observation;

import java.util.ArrayList;
import java.util.List;

public class EncounterDAO {

    public long saveEncounter(Encounter encounter, long visitID) {
        encounter.setVisitID(visitID);
        return new EncounterTable().insert(encounter);
    }

    public List<Encounter> findEncountersForByVisitID(Long visitID) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        List<Encounter> encounters = new ArrayList<Encounter>();

        String where = String.format("%s = ?", EncounterTable.Column.VISIT_KEY_ID);
        String[] whereArgs = new String[]{visitID.toString()};
        final Cursor cursor = helper.getReadableDatabase().query(EncounterTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int id_CI = cursor.getColumnIndex(EncounterTable.Column.ID);
                    int uuid_CI = cursor.getColumnIndex(EncounterTable.Column.UUID);
                    int display_CI = cursor.getColumnIndex(EncounterTable.Column.DISPLAY);
                    int datetime_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_DATETIME);
                    int encounterType_CI = cursor.getColumnIndex(EncounterTable.Column.ENCOUNTER_TYPE);
                    Long id = cursor.getLong(id_CI);
                    String uuid = cursor.getString(uuid_CI);
                    String display = cursor.getString(display_CI);
                    Long datetime = cursor.getLong(datetime_CI);
                    Encounter.EncounterType type = Encounter.EncounterType.getType(cursor.getString(encounterType_CI));
                    Encounter encounter = new Encounter();
                    encounter.setId(id);
                    encounter.setVisitID(visitID);
                    encounter.setUuid(uuid);
                    encounter.setDisplay(display);
                    encounter.setEncounterDatetime(datetime);
                    encounter.setEncounterType(type);
                    encounter.setObservations(new ObservationDAO().findObservationByEncounterID(id));
                    encounters.add(encounter);
                }
            } finally {
                cursor.close();
            }
        }

        return encounters;
    }
}
