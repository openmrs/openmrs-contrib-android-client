package org.openmrs.mobile.utilities;

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

public final class OpenMRS_cache
{
    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CONSTANTS
    //////////////////////////////////////////////////////////////////////////////////////

    public static final int DEFAULT_CACHE_SIZE = 1024;

    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE MEMBERS
    //////////////////////////////////////////////////////////////////////////////////////

    private static Context sContext;

    private static OpenMRS_ModelInfo sModelInfo;
    private static DatabaseHelper sDatabaseHelper;

    private static LruCache<String, Model> sEntities;

    private static boolean sIsInitialized = false;

    //////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////

    private OpenMRS_cache() {
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static synchronized void initialize(Configuration configuration) {
        if (sIsInitialized) {
            Log.v("ActiveAndroid already initialized.");
            return;
        }

        sContext = configuration.getContext();
        sModelInfo = new OpenMRS_ModelInfo(configuration);
        sDatabaseHelper = new DatabaseHelper(configuration);

        // TODO: It would be nice to override sizeOf here and calculate the memory
        // actually used, however at this point it seems like the reflection
        // required would be too costly to be of any benefit. We'll just set a max
        // object size instead.
        sEntities = new LruCache<String, Model>(configuration.getCacheSize());

        openDatabase();

        sIsInitialized = true;

        Log.v("ActiveAndroid initialized successfully.");
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

    // Database access

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    public static synchronized SQLiteDatabase openDatabase() {
        return sDatabaseHelper.getWritableDatabase();
    }

    public static synchronized void closeDatabase() {
        sDatabaseHelper.close();
    }

    // Context access

    public static Context getContext() {
        return sContext;
    }

    // Entity cache

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
        return sEntities.get(getIdentifier(type, id));
    }

    public static synchronized void removeEntity(Model entity) {
        sEntities.remove(getIdentifier(entity));
    }

    // Model cache

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
