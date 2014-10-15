package org.openmrs.client.dao;


import net.sqlcipher.Cursor;

import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.databases.DBOpenHelper;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.databases.tables.LocationTable;
import org.openmrs.client.models.Location;

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

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Location location = cursorToLocation(cursor);
            locations.add(location);
            cursor.moveToNext();
        }

        cursor.close();
        return locations;
    }

    private Location cursorToLocation(Cursor cursor) {
        Location location = new Location();
        location.setId(cursor.getLong(cursor.getColumnIndex(LocationTable.Column.ID)));
        location.setUuid(cursor.getString(cursor.getColumnIndex(LocationTable.Column.UUID)));
        location.setDisplay(cursor.getString(cursor.getColumnIndex(LocationTable.Column.DISPLAY)));
        location.setName(cursor.getString(cursor.getColumnIndex(LocationTable.Column.NAME)));
        location.setDescription(cursor.getString(cursor.getColumnIndex(LocationTable.Column.DESCRIPTION)));
        return location;
    }
}
