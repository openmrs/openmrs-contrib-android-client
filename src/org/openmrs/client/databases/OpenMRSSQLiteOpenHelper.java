package org.openmrs.client.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;

public abstract class OpenMRSSQLiteOpenHelper extends SQLiteOpenHelper {
    private OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();

    public static final String DATABASE_NAME = "openmrs.db";

    private String mSecretKey;

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
}
