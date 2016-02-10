package org.odk.collect.android.openmrs.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.ItemsetDbAdapter;
import org.odk.collect.android.database.ODKSQLiteOpenHelper;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.MediaUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OpenMRSFormsProvider extends ContentProvider {
        private static final String t = "OpenMRSFormsProvider";

        private static final String DATABASE_NAME = "forms.db";
        private static final int DATABASE_VERSION = 4;
        private static final String FORMS_TABLE_NAME = "forms";

        private static HashMap<String, String> sFormsProjectionMap;

        private static final int FORMS = 1;
        private static final int FORM_ID = 2;

        private static final UriMatcher sUriMatcher;

        /**
         * This class helps open, create, and upgrade the database file.
         */
        private static class DatabaseHelper extends ODKSQLiteOpenHelper {
            // These exist in database versions 2 and 3, but not in 4...
            private static final String TEMP_FORMS_TABLE_NAME = "forms_v4";
            private static final String MODEL_VERSION = "modelVersion";

            DatabaseHelper(String databaseName) {
                super(Collect.METADATA_PATH, databaseName, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                onCreateNamed(db, FORMS_TABLE_NAME);
            }

            private void onCreateNamed(SQLiteDatabase db, String tableName) {
                db.execSQL("CREATE TABLE " + tableName + " (" + OpenMRSFormsProviderAPI.FormsColumns._ID
                        + " integer primary key, " + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME
                        + " text not null, " + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                        + " text not null, " + OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION
                        + " text, "
                        + OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID
                        + " text not null, "
                        + OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION
                        + " text, "
                        + OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH
                        + " text not null, "
                        + OpenMRSFormsProviderAPI.FormsColumns.DATE
                        + " integer not null, " // milliseconds
                        + OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + " text not null, "
                        + OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + " text not null, "
                        + OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE + " text, "
                        + OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI + " text, "
                        + OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + " text, "
                        + OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " text not null );");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                int initialVersion = oldVersion;
                if (oldVersion < 2) {
                    Log.w(t, "Upgrading database from version " + oldVersion
                            + " to " + newVersion
                            + ", which will destroy all old data");
                    db.execSQL("DROP TABLE IF EXISTS " + FORMS_TABLE_NAME);
                    onCreate(db);
                    return;
                } else {
                    // adding BASE64_RSA_PUBLIC_KEY and changing type and name of
                    // integer MODEL_VERSION to text VERSION
                    db.execSQL("DROP TABLE IF EXISTS " + TEMP_FORMS_TABLE_NAME);
                    onCreateNamed(db, TEMP_FORMS_TABLE_NAME);
                    db.execSQL("INSERT INTO "
                            + TEMP_FORMS_TABLE_NAME
                            + " ("
                            + OpenMRSFormsProviderAPI.FormsColumns._ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DATE
                            + ", " // milliseconds
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION
                            + ", "
                            + ((oldVersion != 3) ? ""
                            : (OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "))
                            + OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH
                            + ") SELECT "
                            + OpenMRSFormsProviderAPI.FormsColumns._ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DATE
                            + ", " // milliseconds
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI
                            + ", "
                            + "CASE WHEN "
                            + MODEL_VERSION
                            + " IS NOT NULL THEN "
                            + "CAST("
                            + MODEL_VERSION
                            + " AS TEXT) ELSE NULL END, "
                            + ((oldVersion != 3) ? ""
                            : (OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "))
                            + OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " FROM "
                            + FORMS_TABLE_NAME);

                    // risky failures here...
                    db.execSQL("DROP TABLE IF EXISTS " + FORMS_TABLE_NAME);
                    onCreateNamed(db, FORMS_TABLE_NAME);
                    db.execSQL("INSERT INTO "
                            + FORMS_TABLE_NAME
                            + " ("
                            + OpenMRSFormsProviderAPI.FormsColumns._ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DATE
                            + ", " // milliseconds
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + ") SELECT "
                            + OpenMRSFormsProviderAPI.FormsColumns._ID + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH
                            + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.DATE
                            + ", " // milliseconds
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY + ", "
                            + OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH + " FROM "
                            + TEMP_FORMS_TABLE_NAME);
                    db.execSQL("DROP TABLE IF EXISTS " + TEMP_FORMS_TABLE_NAME);

                    Log.w(t, "Successfully upgraded database from version "
                            + initialVersion + " to " + newVersion
                            + ", without destroying all the old data");
                }
            }
        }

        private DatabaseHelper mDbHelper;

        @Override
        public boolean onCreate() {

            // must be at the beginning of any activity that can be called from an
            // external intent
            try {
                Collect.overrideODKDirs(getContext());
                Collect.createODKDirs();
            } catch (RuntimeException e) {
                return false;
            }

            mDbHelper = new DatabaseHelper(DATABASE_NAME);
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection,
                            String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(FORMS_TABLE_NAME);

            switch (sUriMatcher.match(uri)) {
                case FORMS:
                    qb.setProjectionMap(sFormsProjectionMap);
                    break;

                case FORM_ID:
                    qb.setProjectionMap(sFormsProjectionMap);
                    qb.appendWhere(OpenMRSFormsProviderAPI.FormsColumns._ID + "="
                            + uri.getPathSegments().get(1));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            // Get the database and run the query
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                    null, sortOrder);

            // Tell the cursor what uri to watch, so it knows when its source data
            // changes
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }

        @Override
        public String getType(Uri uri) {
            switch (sUriMatcher.match(uri)) {
                case FORMS:
                    return OpenMRSFormsProviderAPI.FormsColumns.CONTENT_TYPE;

                case FORM_ID:
                    return OpenMRSFormsProviderAPI.FormsColumns.CONTENT_ITEM_TYPE;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        @Override
        public synchronized Uri insert(Uri uri, ContentValues initialValues) {
            // Validate the requested uri
            if (sUriMatcher.match(uri) != FORMS) {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }

            ContentValues values;
            if (initialValues != null) {
                values = new ContentValues(initialValues);
            } else {
                values = new ContentValues();
            }

            if (!values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH)) {
                throw new IllegalArgumentException(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH
                        + " must be specified.");
            }

            // Normalize the file path.
            // (don't trust the requester).
            String filePath = values.getAsString(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH);
            File form = new File(filePath);
            filePath = form.getAbsolutePath(); // normalized
            values.put(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH, filePath);

            Long now = Long.valueOf(System.currentTimeMillis());

            // Make sure that the necessary fields are all set
            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.DATE) == false) {
                values.put(OpenMRSFormsProviderAPI.FormsColumns.DATE, now);
            }

            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT) == false) {
                Date today = new Date();
                String ts = new SimpleDateFormat(getContext().getString(
                        R.string.added_on_date_at_time), Locale.getDefault())
                        .format(today);
                values.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT, ts);
            }

            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME) == false) {
                values.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME, form.getName());
            }

            // don't let users put in a manual md5 hash
            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH)) {
                values.remove(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH);
            }
            String md5 = FileUtils.getMd5Hash(form);
            values.put(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH, md5);

            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH) == false) {
                String cachePath = Collect.CACHE_PATH + File.separator + md5
                        + ".formdef";
                values.put(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH, cachePath);
            }
            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH) == false) {
//                String pathNoExtension = filePath.substring(0,
//                        filePath.lastIndexOf("."));
//                String mediaPath = pathNoExtension + "-media";
                values.put(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH, "");
            }

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // first try to see if a record with this filename already exists...
            String[] projection = { OpenMRSFormsProviderAPI.FormsColumns._ID, OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH };
            String[] selectionArgs = { filePath };
            String selection = OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH + "=?";
            Cursor c = null;
            try {
                c = db.query(FORMS_TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                if (c.getCount() > 0) {
                    // already exists
                    throw new SQLException("FAILED Insert into " + uri
                            + " -- row already exists for form definition file: "
                            + filePath);
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }

            long rowId = db.insert(FORMS_TABLE_NAME, null, values);
            if (rowId > 0) {
                Uri formUri = ContentUris.withAppendedId(OpenMRSFormsProviderAPI.FormsColumns.CONTENT_URI,
                        rowId);
                getContext().getContentResolver().notifyChange(formUri, null);
                Collect.getInstance()
                        .getActivityLogger()
                        .logActionParam(this, "insert", formUri.toString(),
                                values.getAsString(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH));
                return formUri;
            }

            throw new SQLException("Failed to insert row into " + uri);
        }

        private void deleteFileOrDir(String fileName) {
            File file = new File(fileName);
            if (file.exists()) {
                if (file.isDirectory()) {
                    // delete any media entries for files in this directory...
                    int images = MediaUtils
                            .deleteImagesInFolderFromMediaProvider(file);
                    int audio = MediaUtils
                            .deleteAudioInFolderFromMediaProvider(file);
                    int video = MediaUtils
                            .deleteVideoInFolderFromMediaProvider(file);

                    Log.i(t, "removed from content providers: " + images
                            + " image files, " + audio + " audio files," + " and "
                            + video + " video files.");

                    // delete all the containing files
                    File[] files = file.listFiles();
                    for (File f : files) {
                        // should make this recursive if we get worried about
                        // the media directory containing directories
                        Log.i(t,
                                "attempting to delete file: " + f.getAbsolutePath());
                        f.delete();
                    }
                }
                file.delete();
                Log.i(t, "attempting to delete file: " + file.getAbsolutePath());
            }
        }

        /**
         * This method removes the entry from the content provider, and also removes
         * any associated files. files: form.xml, [formmd5].formdef, formname-media
         * {directory}
         */
        @Override
        public int delete(Uri uri, String where, String[] whereArgs) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int count;

            switch (sUriMatcher.match(uri)) {
                case FORMS:
                    Cursor del = null;
                    try {
                        del = this.query(uri, null, where, whereArgs, null);
                        if (del.getCount() > 0) {
                            del.moveToFirst();
                            do {
                                deleteFileOrDir(del
                                        .getString(del
                                                .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)));
                                String formFilePath = del.getString(del
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH));
                                Collect.getInstance().getActivityLogger()
                                        .logAction(this, "delete", formFilePath);
                                deleteFileOrDir(formFilePath);
                                deleteFileOrDir(del.getString(del
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH)));
                            } while (del.moveToNext());
                        }
                    } finally {
                        if (del != null) {
                            del.close();
                        }
                    }
                    count = db.delete(FORMS_TABLE_NAME, where, whereArgs);
                    break;

                case FORM_ID:
                    String formId = uri.getPathSegments().get(1);

                    Cursor c = null;
                    try {
                        c = this.query(uri, null, where, whereArgs, null);
                        // This should only ever return 1 record.
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            do {
                                deleteFileOrDir(c.getString(c
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)));
                                String formFilePath = c.getString(c
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH));
                                Collect.getInstance().getActivityLogger()
                                        .logAction(this, "delete", formFilePath);
                                deleteFileOrDir(formFilePath);
                                deleteFileOrDir(c.getString(c
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH)));

                                try {
                                    // get rid of the old tables
                                    ItemsetDbAdapter ida = new ItemsetDbAdapter();
                                    ida.open();
                                    ida.delete(c.getString(c
                                            .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH))
                                            + "/itemsets.csv");
                                    ida.close();
                                } catch (Exception e) {
                                    // if something else is accessing the provider this may not exist
                                    // so catch it and move on.
                                }

                            } while (c.moveToNext());
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }

                    count = db.delete(
                            FORMS_TABLE_NAME,
                            OpenMRSFormsProviderAPI.FormsColumns._ID
                                    + "="
                                    + formId
                                    + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        @Override
        public int update(Uri uri, ContentValues values, String where,
                          String[] whereArgs) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int count = 0;
            switch (sUriMatcher.match(uri)) {
                case FORMS:
                    // don't let users manually update md5
                    if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH)) {
                        values.remove(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH);
                    }
                    // if values contains path, then all filepaths and md5s will get
                    // updated
                    // this probably isn't a great thing to do.
                    if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH)) {
                        String formFile = values
                                .getAsString(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH);
                        values.put(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH,
                                FileUtils.getMd5Hash(new File(formFile)));
                    }

                    Cursor c = null;
                    try {
                        c = this.query(uri, null, where, whereArgs, null);

                        if (c.getCount() > 0) {
                            c.moveToPosition(-1);
                            while (c.moveToNext()) {
                                // before updating the paths, delete all the files
                                if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH)) {
                                    String newFile = values
                                            .getAsString(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH);
                                    String delFile = c
                                            .getString(c
                                                    .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH));
                                    if (newFile.equalsIgnoreCase(delFile)) {
                                        // same file, so don't delete anything
                                    } else {
                                        // different files, delete the old one
                                        deleteFileOrDir(delFile);
                                    }

                                    // either way, delete the old cache because we'll
                                    // calculate a new one.
                                    deleteFileOrDir(c
                                            .getString(c
                                                    .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)));
                                }
                            }
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }

                    // Make sure that the necessary fields are all set
                    if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.DATE) == true) {
                        Date today = new Date();
                        String ts = new SimpleDateFormat(getContext().getString(
                                R.string.added_on_date_at_time), Locale.getDefault())
                                .format(today);
                        values.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT, ts);
                    }

                    count = db.update(FORMS_TABLE_NAME, values, where, whereArgs);
                    break;

                case FORM_ID:
                    String formId = uri.getPathSegments().get(1);
                    // Whenever file paths are updated, delete the old files.

                    Cursor update = null;
                    try {
                        update = this.query(uri, null, where, whereArgs, null);

                        // This should only ever return 1 record.
                        if (update.getCount() > 0) {
                            update.moveToFirst();

                            // don't let users manually update md5
                            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH)) {
                                values.remove(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH);
                            }

                            // the order here is important (jrcache needs to be before
                            // form file)
                            // because we update the jrcache file if there's a new form
                            // file
                            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)) {
                                deleteFileOrDir(update
                                        .getString(update
                                                .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)));
                            }

                            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH)) {
                                String formFile = values
                                        .getAsString(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH);
                                String oldFile = update.getString(update
                                        .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH));

                                if (formFile != null
                                        && formFile.equalsIgnoreCase(oldFile)) {
                                    // Files are the same, so we may have just copied
                                    // over something we had
                                    // already
                                } else {
                                    // New file name. This probably won't ever happen,
                                    // though.
                                    deleteFileOrDir(oldFile);
                                }

                                // we're updating our file, so update the md5
                                // and get rid of the cache (doesn't harm anything)
                                deleteFileOrDir(update
                                        .getString(update
                                                .getColumnIndex(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH)));
                                String newMd5 = FileUtils
                                        .getMd5Hash(new File(formFile));
                                values.put(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH, newMd5);
                                values.put(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH,
                                        Collect.CACHE_PATH + File.separator + newMd5
                                                + ".formdef");
                            }

                            // Make sure that the necessary fields are all set
                            if (values.containsKey(OpenMRSFormsProviderAPI.FormsColumns.DATE) == true) {
                                Date today = new Date();
                                String ts = new SimpleDateFormat(getContext()
                                        .getString(R.string.added_on_date_at_time),
                                        Locale.getDefault()).format(today);
                                values.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT, ts);
                            }

                            count = db.update(
                                    FORMS_TABLE_NAME,
                                    values,
                                    OpenMRSFormsProviderAPI.FormsColumns._ID
                                            + "="
                                            + formId
                                            + (!TextUtils.isEmpty(where) ? " AND ("
                                            + where + ')' : ""), whereArgs);
                        } else {
                            Log.e(t, "Attempting to update row that does not exist");
                        }
                    } finally {
                        if (update != null) {
                            update.close();
                        }
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        static {
            sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            sUriMatcher.addURI(OpenMRSFormsProviderAPI.AUTHORITY, "forms", FORMS);
            sUriMatcher.addURI(OpenMRSFormsProviderAPI.AUTHORITY, "forms/#", FORM_ID);

            sFormsProjectionMap = new HashMap<String, String>();
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns._ID, OpenMRSFormsProviderAPI.FormsColumns._ID);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME,
                    OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_NAME);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT,
                    OpenMRSFormsProviderAPI.FormsColumns.DISPLAY_SUBTEXT);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION,
                    OpenMRSFormsProviderAPI.FormsColumns.DESCRIPTION);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID,
                    OpenMRSFormsProviderAPI.FormsColumns.JR_FORM_ID);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION,
                    OpenMRSFormsProviderAPI.FormsColumns.JR_VERSION);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI,
                    OpenMRSFormsProviderAPI.FormsColumns.SUBMISSION_URI);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY,
                    OpenMRSFormsProviderAPI.FormsColumns.BASE64_RSA_PUBLIC_KEY);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH, OpenMRSFormsProviderAPI.FormsColumns.MD5_HASH);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.DATE, OpenMRSFormsProviderAPI.FormsColumns.DATE);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH,
                    OpenMRSFormsProviderAPI.FormsColumns.FORM_MEDIA_PATH);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH,
                    OpenMRSFormsProviderAPI.FormsColumns.FORM_FILE_PATH);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH,
                    OpenMRSFormsProviderAPI.FormsColumns.JRCACHE_FILE_PATH);
            sFormsProjectionMap.put(OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE, OpenMRSFormsProviderAPI.FormsColumns.LANGUAGE);
        }

}
