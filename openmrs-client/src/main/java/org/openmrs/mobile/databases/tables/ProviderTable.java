package org.openmrs.mobile.databases.tables;

import android.util.Log;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Provider;

public class ProviderTable extends Table<Provider> {

    public static final String TABLE_NAME = "providers";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 7;

    @Override
    public String createTableDefinition() {
        String statement = CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.SYNCED + Column.Type.BOOLEAN
                + Column.DISPLAY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.UUID + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.IDENTIFIER + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.PERSON_UUID + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.RETIRED + Column.Type.BOOLEAN
                + Column.PERSON_DISPLAY + Column.Type.TEXT_TYPE

                + ");";
        Log.e("PROVIDER_CTD: ", statement);
        return statement;
    }

    @Override
    public String insertIntoTableDefinition() {
        String statement = INSERT_INTO + TABLE_NAME + "("
                + Column.DISPLAY + Column.COMMA
                + Column.SYNCED + Column.COMMA
                + Column.UUID + Column.COMMA
                + Column.IDENTIFIER + Column.COMMA
                + Column.PERSON_UUID + Column.COMMA
                + Column.PERSON_DISPLAY + Column.COMMA
                + Column.RETIRED
                + ")"
                + values(INSERT_COLUMNS_COUNT);

        Log.e("PROVIDER_IITD: ", statement);
        return statement;
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Provider tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertProvider(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Provider tableObject) {
        return 0;
    }

    @Override
    public void delete(long tableObjectID) {

    }

    public class Column extends MasterColumn {
        public static final String IDENTIFIER = "identifier";
        public static final String SYNCED = "synced";
        public static final String PERSON_UUID = "personuuid";
        public static final String PERSON_DISPLAY = "persondisplay";
        public static final String RETIRED = "retired";
    }

    @Override
    public String toString() {
        return TABLE_NAME + createTableDefinition();
    }
}
