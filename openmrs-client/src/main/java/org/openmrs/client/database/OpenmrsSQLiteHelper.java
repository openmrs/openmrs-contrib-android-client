package org.openmrs.client.database;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;

public class OpenmrsSQLiteHelper extends SQLiteOpenHelper {
    protected final OpenMRSLogger mOpenMRSLogger = OpenMRS.getInstance().getOpenMRSLogger();

    private static final String DATABASE_NAME = "openmrs.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PATIENTS = "patients";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DISPLAY = "display";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_GIVEN_NAME = "givenName";
    public static final String COLUMN_MIDDLE_NAME = "middleName";
    public static final String COLUMN_FAMILY_NAME = "familyName";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BIRTH_DATE = "birthDate";
    public static final String COLUMN_DEATH_DATE = "deathDate";
    public static final String COLUMN_CAUSE_OF_DEATH = "causeOfDeath";

    public static final String COLUMN_TEXT_TYPE_NOT_NULL = " text not null,";

    // Database creation sql statement
    private static final String DATABASE_CREATE_PATIENTS_TABLE = "create table "
            + TABLE_PATIENTS + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_DISPLAY + " text,"
            + COLUMN_UUID + COLUMN_TEXT_TYPE_NOT_NULL
            + COLUMN_IDENTIFIER + COLUMN_TEXT_TYPE_NOT_NULL
            + COLUMN_GIVEN_NAME + COLUMN_TEXT_TYPE_NOT_NULL
            + COLUMN_MIDDLE_NAME + " text,"
            + COLUMN_FAMILY_NAME + COLUMN_TEXT_TYPE_NOT_NULL
            + COLUMN_GENDER + COLUMN_TEXT_TYPE_NOT_NULL
            + COLUMN_BIRTH_DATE + " data not null,"
            + COLUMN_DEATH_DATE + " data,"
            + COLUMN_CAUSE_OF_DEATH + " text"
            + ");";

    private static final String DROP_PATIENTS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_PATIENTS;

    public OpenmrsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mOpenMRSLogger.d("Database creating...");
        sqLiteDatabase.execSQL(DATABASE_CREATE_PATIENTS_TABLE);
        mOpenMRSLogger.d("Table " + DATABASE_CREATE_PATIENTS_TABLE + " ver." + DATABASE_VERSION + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        mOpenMRSLogger.w("Upgrading database from version " + i + " to "
                        + i2 + ", which will destroy all old data");
        sqLiteDatabase.execSQL(DROP_PATIENTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void dropDatabase(SQLiteDatabase sqLiteDatabase) {
        mOpenMRSLogger.w("Drop database");

        sqLiteDatabase.execSQL(DROP_PATIENTS_TABLE);
    }
}
