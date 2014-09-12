package org.openmrs.client.databases.tables;

import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.models.Observation;

public class ObservationTable extends Table<Observation> {
    public static final String TABLE_NAME = "observations";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.client.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 4;

    @Override
    public String crateTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.ENCOUNTER_KEY_ID  + Column.Type.INT_TYPE_NOT_NULL
                + Column.UUID + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.DISPLAY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DISPLAY_VALUE + Column.Type.TEXT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.ENCOUNTER_KEY_ID + Column.COMMA
                + Column.UUID + Column.COMMA
                + Column.DISPLAY + Column.COMMA
                + Column.DISPLAY_VALUE + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Observation tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertObservation(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Observation tableObject) {
        return 0;
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, MasterColumn.ID + MasterColumn.EQUALS + tableObjectID, null);
    }

    public class Column extends MasterColumn {
        public static final String ENCOUNTER_KEY_ID = "encounter_id";
        public static final String DISPLAY_VALUE = "displayValue";
    }

    @Override
    public String toString() {
        return TABLE_NAME + crateTableDefinition();
    }
}
