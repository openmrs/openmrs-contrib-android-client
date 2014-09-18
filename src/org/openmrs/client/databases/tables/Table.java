package org.openmrs.client.databases.tables;

public abstract class Table<T> {
    public static final String PRIMARY_KEY = " integer primary key autoincrement,";

    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String INSERT_INTO = "INSERT INTO ";
    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    /**
     * @return Appended String which is representation of create table definition
     */
    public abstract String crateTableDefinition();

    /**
     * @return Appended String which is representation of insert into table definition
     */
    public abstract String insertIntoTableDefinition();

    /**
     * @return Appended String which is representation of drop table definition
     */
    public abstract String dropTableDefinition();

    /**
     * Creates new row for passed param
     *
     * @param tableObject object to be saved in table
     * @return ID of new object
     */
    public abstract Long insert(T tableObject);

    /**
     * Updates existing row with values of param tableObject
     *
     * @param tableObjectID id of object to be updated
     * @param tableObject object to be updated in table
     * @return number of updated rows
     */
    public abstract int update(long tableObjectID, T tableObject);

    /**
     * Deletes object from table where id=tableObjectID
     *
     * @param tableObjectID id of object to be deleted
     */
    public abstract void delete(long tableObjectID);

    public static String values(int numberOfColumns) {
        StringBuilder builder = new StringBuilder("VALUES(");
        for (int i = 1; i <= numberOfColumns; i++) {
            if (i < numberOfColumns) {
                builder.append("?,");
            } else if (i == numberOfColumns) {
                builder.append("?");
            }
        }
        builder.append(");");
        return builder.toString();
    }

    /**
     * Class contains common parts of columns types and definitions
     */
    public abstract class MasterColumn {
        public static final String ID = "_id";
        public static final String UUID = "uuid";
        public static final String DISPLAY = "display";
        public static final String COMMA = ",";
        public static final String EQUALS = " = ";

        /**
         * Contains columns type definitions
         */
        public abstract class Type {
            public static final String TEXT_TYPE = " text";
            public static final String TEXT_TYPE_WITH_COMMA = TEXT_TYPE + COMMA;
            public static final String TEXT_TYPE_NOT_NULL = " text not null,";
            public static final String DATE_TYPE = " date";
            public static final String DATE_TYPE_WITH_COMMA = DATE_TYPE + COMMA;
            public static final String DATE_TYPE_NOT_NULL = " data not null,";
            public static final String INT_TYPE = " integer";
            public static final String INT_TYPE_WITH_COMMA = INT_TYPE + COMMA;
            public static final String INT_TYPE_NOT_NULL = " integer not null,";
        }
    }
}
