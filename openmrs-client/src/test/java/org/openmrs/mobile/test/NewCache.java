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

package org.openmrs.mobile.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.LruCache;

import com.activeandroid.Configuration;
import com.activeandroid.DatabaseHelper;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.serializer.TypeSerializer;
import com.activeandroid.util.Log;

import java.util.Collection;

public final class NewCache {
    public static final int DEFAULT_CACHE_SIZE = 1024;
    private static Context sContext;
    private static NewModelInfo sModelInfo;
    private static DatabaseHelper sDatabaseHelper;
    private static LruCache<String, Model> sEntities;
    private static boolean sIsInitialized = false;

    private NewCache() {
    }

    public static synchronized void initialize(Configuration configuration) {
        if (sIsInitialized) {
            Log.v("ActiveAndroid already initialized.");
        } else {
            sContext = configuration.getContext();
            sModelInfo = new NewModelInfo(configuration);
            sDatabaseHelper = new DatabaseHelper(configuration);
            sEntities = new LruCache(configuration.getCacheSize());
            openDatabase();
            sIsInitialized = true;
            Log.v("ActiveAndroid initialized successfully.");
        }
    }

    public static synchronized void clear() {
        sEntities.evictAll();
        Log.v("Cache cleared.");
    }

    public static synchronized void dispose() {
        closeDatabase();
        sEntities = null;
        sModelInfo = null;
        sDatabaseHelper = null;
        sIsInitialized = false;
        Log.v("ActiveAndroid disposed. Call initialize to use library.");
    }

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    public static synchronized SQLiteDatabase openDatabase() {
        return sDatabaseHelper.getWritableDatabase();
    }

    public static synchronized void closeDatabase() {
        sDatabaseHelper.close();
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getIdentifier(Class<? extends Model> type, Long id) {
        return getTableName(type) + "@" + id;
    }

    public static String getIdentifier(Model entity) {
        return getIdentifier(entity.getClass(), entity.getId());
    }

    public static synchronized void addEntity(Model entity) {
        sEntities.put(getIdentifier(entity), entity);
    }

    public static synchronized Model getEntity(Class<? extends Model> type, long id) {
        return (Model) sEntities.get(getIdentifier(type, id));
    }

    public static synchronized void removeEntity(Model entity) {
        sEntities.remove(getIdentifier(entity));
    }

    public static synchronized Collection<TableInfo> getTableInfos() {
        return sModelInfo.getTableInfos();
    }

    public static synchronized TableInfo getTableInfo(Class<? extends Model> type) {
        return sModelInfo.getTableInfo(type);
    }

    public static synchronized TypeSerializer getParserForType(Class<?> type) {
        return sModelInfo.getTypeSerializer(type);
    }

    public static synchronized String getTableName(Class<? extends Model> type) {
        return sModelInfo.getTableInfo(type).getTableName();
    }
}