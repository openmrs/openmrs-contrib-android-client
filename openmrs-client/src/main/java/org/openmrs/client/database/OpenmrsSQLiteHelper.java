package org.openmrs.client.database;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class OpenmrsSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patients.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PATIENTS = "patients";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_GIVEN_NAME = "givenName";
    public static final String COLUMN_MIDDLE_NAME = "middleName";
    public static final String COLUMN_FAMILY_NAME = "familyName";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BIRTH_DATE = "birthDate";
    public static final String COLUMN_DEATH_DATE = "deathDate";
    public static final String COLUMN_CAUSE_OF_DEATH = "causeOfDeath";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PATIENTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_UUID + " text not null,"
            + COLUMN_IDENTIFIER + " text not null,"
            + COLUMN_GIVEN_NAME + " text not null,"
            + COLUMN_MIDDLE_NAME + " text,"
            + COLUMN_FAMILY_NAME + " text no null,"
            + COLUMN_GENDER + " text not null,"
            + COLUMN_BIRTH_DATE + " text not null,"
            + COLUMN_DEATH_DATE + " text,"
            + COLUMN_CAUSE_OF_DEATH + " text"
            + ");";

    public OpenmrsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.w(OpenmrsSQLiteHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i2 + ", which will destroy all old data"
        );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        onCreate(sqLiteDatabase);
    }
}
