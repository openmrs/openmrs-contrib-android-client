package org.openmrs.client.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;

public class DBOpenHelper extends OpenMRSSQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String PATIENTS_TABLE_NAME = "patients";
    public static final String VISITS_TABLE_NAME = "visits";
    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String PRIMARY_KEY = " integer primary key autoincrement,";
    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String INSERT_INTO = "INSERT INTO ";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_DISPLAY = "display";

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

    public static final String COLUMN_PATIENT_KEY_ID = "patient_id";
    public static final String COLUMN_VISIT_TYPE = "visit_type";
    public static final String COLUMN_VISIT_PLACE = "visit_place";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_STOP_DATE = "stop_date";

    // Database creation sql statement
    private static final String CREATE_PATIENTS_TABLE = CREATE_TABLE
            + PATIENTS_TABLE_NAME + "("
            + COLUMN_ID + PRIMARY_KEY
            + COLUMN_DISPLAY + TEXT_TYPE_WITH_COMMA
            + COLUMN_UUID + TEXT_TYPE_NOT_NULL
            + COLUMN_IDENTIFIER + TEXT_TYPE_NOT_NULL
            + COLUMN_GIVEN_NAME + TEXT_TYPE_NOT_NULL
            + COLUMN_MIDDLE_NAME + TEXT_TYPE_WITH_COMMA
            + COLUMN_FAMILY_NAME + TEXT_TYPE_NOT_NULL
            + COLUMN_GENDER + TEXT_TYPE_NOT_NULL
            + COLUMN_BIRTH_DATE + DATE_TYPE_NOT_NULL
            + COLUMN_DEATH_DATE + DATE_TYPE_WITH_COMMA
            + COLUMN_CAUSE_OF_DEATH + TEXT_TYPE_WITH_COMMA
            + COLUMN_AGE + TEXT_TYPE_WITH_COMMA
            + COLUMN_ADDRESS_1 + TEXT_TYPE_WITH_COMMA + COLUMN_ADDRESS_2 + TEXT_TYPE_WITH_COMMA
            + COLUMN_POSTAL_CODE + TEXT_TYPE_WITH_COMMA + COLUMN_COUNTRY + TEXT_TYPE_WITH_COMMA
            + COLUMN_STATE + TEXT_TYPE_WITH_COMMA + COLUMN_CITY + TEXT_TYPE_WITH_COMMA
            + COLUMN_PHONE + TEXT_TYPE
            + ");";

    private static final String DROP_PATIENTS_TABLE =
            DROP_TABLE_IF_EXISTS + PATIENTS_TABLE_NAME;

    private static final String INSERT_PATIENT_QUERY = INSERT_INTO + PATIENTS_TABLE_NAME + "("
            + COLUMN_DISPLAY + COMMA + COLUMN_UUID + COMMA
            + COLUMN_IDENTIFIER + COMMA + COLUMN_GIVEN_NAME + COMMA
            + COLUMN_MIDDLE_NAME + COMMA + COLUMN_FAMILY_NAME + COMMA
            + COLUMN_GENDER + COMMA + COLUMN_BIRTH_DATE + COMMA
            + COLUMN_DEATH_DATE + COMMA + COLUMN_CAUSE_OF_DEATH + COMMA
            + COLUMN_AGE + COMMA + COLUMN_ADDRESS_1 + COMMA  + COLUMN_ADDRESS_2 + COMMA
            + COLUMN_POSTAL_CODE + COMMA + COLUMN_COUNTRY + COMMA
            + COLUMN_STATE + COMMA + COLUMN_CITY + COMMA + COLUMN_PHONE + ")"
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String CREATE_VISIT_TABLE = CREATE_TABLE
            + VISITS_TABLE_NAME + "("
            + COLUMN_ID + PRIMARY_KEY
            + COLUMN_UUID + TEXT_TYPE_WITH_COMMA
            + COLUMN_PATIENT_KEY_ID + TEXT_TYPE_NOT_NULL
            + COLUMN_VISIT_TYPE + TEXT_TYPE_WITH_COMMA
            + COLUMN_VISIT_PLACE + TEXT_TYPE_WITH_COMMA
            + COLUMN_START_DATE + DATE_TYPE_NOT_NULL
            + COLUMN_STOP_DATE + DATE_TYPE
            + ");";

    private static final String DROP_VISIT_TABLE =
            DROP_TABLE_IF_EXISTS + VISITS_TABLE_NAME;

    private static final String INSERT_VISIT_QUERY = INSERT_INTO + VISITS_TABLE_NAME + "("
            + COLUMN_UUID + COMMA + COLUMN_PATIENT_KEY_ID + COMMA
            + COLUMN_VISIT_TYPE + COMMA + COLUMN_VISIT_PLACE + COMMA
            + COLUMN_START_DATE + COMMA + COLUMN_STOP_DATE + ")"
            + "VALUES(?, ?, ?, ?, ?, ?);";

    private SQLiteStatement mStatement;

    public DBOpenHelper(Context context) {
        super(context, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mLogger.d("Database creating...");
        sqLiteDatabase.execSQL(CREATE_PATIENTS_TABLE);
        mLogger.d("Table " + CREATE_PATIENTS_TABLE + " ver." + DATABASE_VERSION + " created");
        sqLiteDatabase.execSQL(CREATE_VISIT_TABLE);
        mLogger.d("Table " + CREATE_VISIT_TABLE + " ver." + DATABASE_VERSION + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int currentVersion, int newVersion) {
        mLogger.w("Upgrading database from version " + currentVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL(DROP_PATIENTS_TABLE);
        sqLiteDatabase.execSQL(DROP_VISIT_TABLE);
        onCreate(sqLiteDatabase);
    }

    public long insertPatient(SQLiteDatabase db, Patient patient) {
        long patientId;
        if (null == mStatement) {
            mStatement = db.compileStatement(INSERT_PATIENT_QUERY);
        }
        try {
            db.beginTransaction();
            bindString(mStatement, 1, patient.getDisplay());
            bindString(mStatement, 2, patient.getUuid());
            bindString(mStatement, 3, patient.getIdentifier());
            bindString(mStatement, 4, patient.getGivenName());
            bindString(mStatement, 5, patient.getMiddleName());
            bindString(mStatement, 6, patient.getFamilyName());
            bindString(mStatement, 7, patient.getGender());
            bindLong(mStatement, 8, patient.getBirthDate());
            bindLong(mStatement, 9, patient.getDeathDate());
            bindString(mStatement, 10, patient.getCauseOfDeath());
            bindString(mStatement, 11, patient.getAge());
            bindString(mStatement, 12, patient.getAddress().getAddress1());
            bindString(mStatement, 13, patient.getAddress().getAddress2());
            bindString(mStatement, 14, patient.getAddress().getPostalCode());
            bindString(mStatement, 15, patient.getAddress().getCountry());
            bindString(mStatement, 16, patient.getAddress().getState());
            bindString(mStatement, 17, patient.getAddress().getCityVillage());
            bindString(mStatement, 18, patient.getPhoneNumber());
            patientId = mStatement.executeInsert();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }

        return patientId;
    }

    public void insertVisit(SQLiteDatabase db, Visit visit) {
        if (null == mStatement) {
            mStatement = db.compileStatement(INSERT_VISIT_QUERY);
        }
        try {
            db.beginTransaction();
            bindString(mStatement, 1, visit.getUuid());
            bindLong(mStatement, 2, visit.getPatientID());
            bindString(mStatement, 3, visit.getVisitType());
            bindString(mStatement, 4, visit.getVisitPlace());
            bindLong(mStatement, 5, visit.getStartDate());
            bindLong(mStatement, 6, visit.getStopDate());
            mStatement.execute();
            mStatement.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            mStatement = null;
        }
    }

}
