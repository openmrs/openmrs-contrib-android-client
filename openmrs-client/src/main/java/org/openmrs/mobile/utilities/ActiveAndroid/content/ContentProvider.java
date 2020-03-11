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

package org.openmrs.mobile.utilities.ActiveAndroid.content;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.utilities.ActiveAndroid.ActiveAndroid;
import org.openmrs.mobile.utilities.ActiveAndroid.Cache;
import org.openmrs.mobile.utilities.ActiveAndroid.Configuration;
import org.openmrs.mobile.utilities.ActiveAndroid.Model;
import org.openmrs.mobile.utilities.ActiveAndroid.TableInfo;

import java.util.ArrayList;
import java.util.List;


public class ContentProvider extends android.content.ContentProvider {
    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CONSTANTS
    //////////////////////////////////////////////////////////////////////////////////////

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final SparseArray<Class<? extends Model>> TYPE_CODES = new SparseArray<>();
    private static String sAuthority;
    private static SparseArray<String> sMimeTypeCache = new SparseArray<>();

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static Uri createUri(Class<? extends Model> type, Long id) {
        final StringBuilder uri = new StringBuilder();
        uri.append("content://");
        uri.append(sAuthority);
        uri.append("/");
        uri.append(Cache.getTableName(type).toLowerCase());

        if (id != null) {
            uri.append("/");
            uri.append(id.toString());
        }

        return Uri.parse(uri.toString());
    }

    @Override
    public boolean onCreate() {
        ActiveAndroid.initialize(getConfiguration());
        sAuthority = getAuthority();

        final List<TableInfo> tableInfos = new ArrayList<>(Cache.getTableInfos());
        final int size = tableInfos.size();
        for (int i = 0; i < size; i++) {
            final TableInfo tableInfo = tableInfos.get(i);
            final int tableKey = (i * 2) + 1;
            final int itemKey = (i * 2) + 2;

            // content://<authority>/<table>
            URI_MATCHER.addURI(sAuthority, tableInfo.getTableName().toLowerCase(), tableKey);

            TYPE_CODES.put(tableKey, tableInfo.getType());

            // content://<authority>/<table>/<id>
            URI_MATCHER.addURI(sAuthority, tableInfo.getTableName().toLowerCase() + "/#", itemKey);
            TYPE_CODES.put(itemKey, tableInfo.getType());
        }

        return true;
    }

    // SQLite methods

    @Override
    public String getType(@NotNull Uri uri) {
        final int match = URI_MATCHER.match(uri);

        String cachedMimeType = sMimeTypeCache.get(match);
        if (cachedMimeType != null) {
            return cachedMimeType;
        }

        final Class<? extends Model> type = getModelType(uri);
        final boolean single = ((match % 2) == 0);

        StringBuilder mimeType = new StringBuilder();
        mimeType.append("vnd");
        mimeType.append(".");
        mimeType.append(sAuthority);
        mimeType.append(".");
        mimeType.append(single ? "item" : "dir");
        mimeType.append("/");
        mimeType.append("vnd");
        mimeType.append(".");
        mimeType.append(sAuthority);
        mimeType.append(".");
        mimeType.append(Cache.getTableName(type));

        sMimeTypeCache.append(match, mimeType.toString());

        return mimeType.toString();
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        final Class<? extends Model> type = getModelType(uri);
        final Long id = Cache.openDatabase().insert(Cache.getTableName(type), null, values);

        if (id != null && id > 0) {
            Uri retUri = createUri(type, id);
            notifyChange(retUri);

            return retUri;
        }

        return null;
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final Class<? extends Model> type = getModelType(uri);
        final int count = Cache.openDatabase().update(Cache.getTableName(type), values, selection, selectionArgs);

        notifyChange(uri);

        return count;
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        final Class<? extends Model> type = getModelType(uri);
        final int count = Cache.openDatabase().delete(Cache.getTableName(type), selection, selectionArgs);

        notifyChange(uri);

        return count;
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Class<? extends Model> type = getModelType(uri);
        final Cursor cursor = Cache.openDatabase().query(
                Cache.getTableName(type),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    protected String getAuthority() {
        return getContext().getPackageName();
    }

    protected Configuration getConfiguration() {
        return new Configuration.Builder(getContext()).create();
    }

    private Class<? extends Model> getModelType(Uri uri) {
        final int code = URI_MATCHER.match(uri);
        if (code != UriMatcher.NO_MATCH) {
            return TYPE_CODES.get(code);
        }

        return null;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
