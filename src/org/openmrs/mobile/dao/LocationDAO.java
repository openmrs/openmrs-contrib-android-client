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

package org.openmrs.mobile.dao;


import net.sqlcipher.Cursor;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.LocationTable;
import org.openmrs.mobile.models.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    public long saveLocation(Location location) {
        return new LocationTable().insert(location);
    }

    public void deleteAllLocations() {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().execSQL(new LocationTable().dropTableDefinition());
        openHelper.getWritableDatabase().execSQL(new LocationTable().crateTableDefinition());
        OpenMRS.getInstance().getOpenMRSLogger().d("All Locations deleted");
    }

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<Location>();
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        Cursor cursor = openHelper.getReadableDatabase().query(LocationTable.TABLE_NAME,
                null, null, null, null, null, null);

        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    Location location = cursorToLocation(cursor);
                    locations.add(location);
                }
            } finally {
                cursor.close();
            }
        }

        return locations;
    }

    public static Location findLocationByName(String name) {
        Location location = new Location();
        String where = String.format("%s = ?", LocationTable.Column.DISPLAY);
        String[] whereArgs = new String[]{name};

        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        final Cursor cursor = helper.getReadableDatabase().query(LocationTable.TABLE_NAME, null, where, whereArgs, null, null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    location = cursorToLocation(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return location;
    }

    private static Location cursorToLocation(Cursor cursor) {
        Location location = new Location();
        location.setId(cursor.getLong(cursor.getColumnIndex(LocationTable.Column.ID)));
        location.setUuid(cursor.getString(cursor.getColumnIndex(LocationTable.Column.UUID)));
        location.setDisplay(cursor.getString(cursor.getColumnIndex(LocationTable.Column.DISPLAY)));
        location.setName(cursor.getString(cursor.getColumnIndex(LocationTable.Column.NAME)));
        location.setDescription(cursor.getString(cursor.getColumnIndex(LocationTable.Column.DESCRIPTION)));
        location.setParentLocationUuid(cursor.getString(cursor.getColumnIndex(LocationTable.Column.PARENT_LOCATION_UUID)));
        location.setParentLocationDisplay(cursor.getString(cursor.getColumnIndex(LocationTable.Column.PARENT_LOCATION_DISPLAY)));
        return location;
    }
}
