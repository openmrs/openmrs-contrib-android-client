package org.openmrs.client.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;
import org.openmrs.client.utilities.StringUtils;

public abstract class OpenMRSSQLiteOpenHelper extends SQLiteOpenHelper {
    protected OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();

    public static final String DATABASE_NAME = "openmrs.db";

    private String mSecretKey;

    protected SQLiteStatement mStatement;

    public OpenMRSSQLiteOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook) {
        super(context, DATABASE_NAME, factory, version, hook);
    }

    public OpenMRSSQLiteOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version, new OpenMRSDefaultDBHook());
    }

    public OpenMRSSQLiteOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, int version, String secretKey) {
        this(context, factory, version);
        mSecretKey = secretKey;
    }

    public String getSecretKey() {
        return mSecretKey != null ? mSecretKey : OpenMRS.getInstance().getSecretKey();
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase(getSecretKey());
        } catch (SQLiteException e) {
            db = openDatabaseWithoutSecretKey(true);
        }
        return db;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db;
        try {
            db = getReadableDatabase(getSecretKey());
        } catch (SQLiteException e) {
            db = openDatabaseWithoutSecretKey(false);
        }
        return db;
    }

    private SQLiteDatabase openDatabaseWithoutSecretKey(boolean writable) {
        SQLiteDatabase db;
        mLogger.w("Can't open database with secret key. Trying to open without key (may be not encrypted).");
        if (writable) {
            db = getWritableDatabase("");
        } else {
            db = getReadableDatabase("");
        }
        mLogger.w("Database opened but is not encrypted!");
        return db;
    }

    public static class OpenMRSDefaultDBHook implements SQLiteDatabaseHook {

        @Override
        public void preKey(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("PRAGMA cipher_default_kdf_iter = '4000'");
        }

        @Override
        public void postKey(SQLiteDatabase sqLiteDatabase) {

        }

    }

    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindString(int, String)
     *
     * @param columnIndex
     * @param columnValue
     */
    public void bindString(int columnIndex, String columnValue) {
        if (StringUtils.notNull(columnValue)) {
            mStatement.bindString(columnIndex, columnValue);
        } else {
            mStatement.bindNull(columnIndex);
        }
    }

    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindLong(int, long)
     *
     * @param columnIndex
     * @param columnValue
     */
    public void bindLong(int columnIndex, Long columnValue) {
        if (null != columnValue) {
            mStatement.bindLong(columnIndex, columnValue);
        } else {
            mStatement.bindNull(columnIndex);
        }
    }

    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindDouble(int, double)
     *
     * @param columnIndex
     * @param columnValue
     */
    public void bindDouble(int columnIndex, Double columnValue) {
        if (null != columnValue) {
            mStatement.bindDouble(columnIndex, columnValue);
        } else {
            mStatement.bindNull(columnIndex);
        }
    }
}
