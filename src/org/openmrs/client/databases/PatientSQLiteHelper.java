package org.openmrs.client.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.models.Patient;

public class PatientSQLiteHelper extends OpenMRSSQLiteOpenHelper {
    protected final OpenMRSLogger mOpenMRSLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private static final String COMMA = ",";

    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "patients";

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
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_ADDRESS_1 = "address1";
    public static final String COLUMN_ADDRESS_2 = "address2";
    public static final String COLUMN_POSTAL_CODE = "postalCode";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHONE = "phone";

    public static final String TEXT_TYPE_NOT_NULL = " text not null,";

    public static final String TEXT_TYPE_WITH_COMMA = " text,";
    // Database creation sql statement
    private static final String DATABASE_CREATE_PATIENTS_TABLE = "create table "
            + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_DISPLAY + TEXT_TYPE_WITH_COMMA
            + COLUMN_UUID + TEXT_TYPE_NOT_NULL
            + COLUMN_IDENTIFIER + TEXT_TYPE_NOT_NULL
            + COLUMN_GIVEN_NAME + TEXT_TYPE_NOT_NULL
            + COLUMN_MIDDLE_NAME + TEXT_TYPE_WITH_COMMA
            + COLUMN_FAMILY_NAME + TEXT_TYPE_NOT_NULL
            + COLUMN_GENDER + TEXT_TYPE_NOT_NULL
            + COLUMN_BIRTH_DATE + " data not null,"
            + COLUMN_DEATH_DATE + " data,"
            + COLUMN_CAUSE_OF_DEATH + TEXT_TYPE_WITH_COMMA
            + COLUMN_AGE + TEXT_TYPE_WITH_COMMA
            + COLUMN_ADDRESS_1 + TEXT_TYPE_WITH_COMMA + COLUMN_ADDRESS_2 + TEXT_TYPE_WITH_COMMA
            + COLUMN_POSTAL_CODE + TEXT_TYPE_WITH_COMMA + COLUMN_COUNTRY + TEXT_TYPE_WITH_COMMA
            + COLUMN_STATE + TEXT_TYPE_WITH_COMMA + COLUMN_CITY + TEXT_TYPE_WITH_COMMA
            + COLUMN_PHONE + " text"
            + ");";

    private static final String DROP_PATIENTS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String INSERT_PATIENT_QUERY = "INSERT INTO " + TABLE_NAME + "("
            + COLUMN_DISPLAY + COMMA + COLUMN_UUID + COMMA
            + COLUMN_IDENTIFIER + COMMA + COLUMN_GIVEN_NAME + COMMA
            + COLUMN_MIDDLE_NAME + COMMA + COLUMN_FAMILY_NAME + COMMA
            + COLUMN_GENDER + COMMA + COLUMN_BIRTH_DATE + COMMA
            + COLUMN_DEATH_DATE + COMMA + COLUMN_CAUSE_OF_DEATH + COMMA
            + COLUMN_AGE + COMMA + COLUMN_ADDRESS_1 + COMMA  + COLUMN_ADDRESS_2 + COMMA
            + COLUMN_POSTAL_CODE + COMMA + COLUMN_COUNTRY + COMMA
            + COLUMN_STATE + COMMA + COLUMN_CITY + COMMA + COLUMN_PHONE + ")"
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private SQLiteStatement mPatientSQLStatement;

    public PatientSQLiteHelper(Context context) {
        super(context, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mOpenMRSLogger.d("Database creating...");
        sqLiteDatabase.execSQL(DATABASE_CREATE_PATIENTS_TABLE);
        mOpenMRSLogger.d("Table " + DATABASE_CREATE_PATIENTS_TABLE + " ver." + DATABASE_VERSION + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int currentVersion, int newVersion) {
        mOpenMRSLogger.w("Upgrading database from version " + currentVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL(DROP_PATIENTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insert(SQLiteDatabase db, Patient patient) {
        if (null == mPatientSQLStatement) {
            mPatientSQLStatement = db.compileStatement(INSERT_PATIENT_QUERY);
        }
        try {
            db.beginTransaction();
            mPatientSQLStatement.bindString(1, patient.getDisplay());
            mPatientSQLStatement.bindString(2, patient.getUuid());
            mPatientSQLStatement.bindString(3, patient.getIdentifier());
            mPatientSQLStatement.bindString(4, patient.getGivenName());
            mPatientSQLStatement.bindString(5, patient.getMiddleName());
            mPatientSQLStatement.bindString(6, patient.getFamilyName());
            mPatientSQLStatement.bindString(7, patient.getGender());
            mPatientSQLStatement.bindString(8, patient.getBirthDate());
            mPatientSQLStatement.bindString(9, patient.getDeathDate());
            mPatientSQLStatement.bindString(10, patient.getCauseOfDeath());
            mPatientSQLStatement.bindString(11, patient.getAge());
            mPatientSQLStatement.bindString(12, patient.getAddress().getAddress1());
            mPatientSQLStatement.bindString(13, patient.getAddress().getAddress2());
            mPatientSQLStatement.bindString(14, patient.getAddress().getPostalCode());
            mPatientSQLStatement.bindString(15, patient.getAddress().getCountry());
            mPatientSQLStatement.bindString(16, patient.getAddress().getState());
            mPatientSQLStatement.bindString(17, patient.getAddress().getCityVillage());
            mPatientSQLStatement.bindString(18, patient.getPhoneNumber());
            mPatientSQLStatement.execute();
            mPatientSQLStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
