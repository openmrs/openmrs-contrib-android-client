/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.databases;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.utilities.StringUtils;

public abstract class OpenMRSSQLiteOpenHelper extends SQLiteOpenHelper {
    protected OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();

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

    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindString(int, String)
     *
     * @param columnIndex
     * @param columnValue
     * @param statement
     */
    public void bindString(int columnIndex, String columnValue, SQLiteStatement statement) {
        if (StringUtils.notNull(columnValue)) {
            statement.bindString(columnIndex, columnValue);
        }
    }

    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindLong(int, long)
     *
     * @param columnIndex
     * @param columnValue
     * @param statement
     */
    public void bindLong(int columnIndex, Long columnValue, SQLiteStatement statement) {
        if (null != columnValue) {
            statement.bindLong(columnIndex, columnValue);
        }
    }
    /**
     * Null safe wrapper method for
     * @see net.sqlcipher.database.SQLiteStatement#bindDouble(int, double)
     *
     * @param columnIndex
     * @param columnValue
     * @param statement
     */
    public void bindDouble(int columnIndex, Double columnValue, SQLiteStatement statement) {
        if (null != columnValue) {
            statement.bindDouble(columnIndex, columnValue);
        }
    }
}
