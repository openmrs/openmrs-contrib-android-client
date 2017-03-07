package org.openmrs.mobile.databases.tables;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Concept;

public class ConceptTable extends Table<Concept> {

    public static final String TABLE_NAME = "concepts";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.mobile.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 2;

    public class Column extends MasterColumn {

    }

    @Override
    public String createTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + ConceptTable.Column.ID + PRIMARY_KEY
                + ConceptTable.Column.UUID + ConceptTable.Column.Type.TEXT_TYPE_NOT_NULL
                + ConceptTable.Column.DISPLAY + ConceptTable.Column.Type.TEXT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + ConceptTable.Column.UUID + ConceptTable.Column.COMMA
                + ConceptTable.Column.DISPLAY + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Concept tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertConcept(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Concept tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.updateConcept(helper.getWritableDatabase(), tableObjectID, tableObject);
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, Table.MasterColumn.ID + Table.MasterColumn.EQUALS + tableObjectID, null);
    }

    @Override
    public String toString() {
        return TABLE_NAME + createTableDefinition();
    }

}
