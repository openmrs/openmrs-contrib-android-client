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

package org.openmrs.mobile.databases.tables;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Location;

public class LocationTable extends Table<Location> {
    public static final String TABLE_NAME = "locations";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 12;

    @Override
    public String crateTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.UUID + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.DISPLAY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.NAME + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DESCRIPTION + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.ADDRESS_1 + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.ADDRESS_2 + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.CITY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.STATE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.COUNTRY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.POSTAL_CODE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.PARENT_LOCATION_UUID + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.PARENT_LOCATION_DISPLAY + Column.Type.TEXT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.UUID + Column.COMMA
                + Column.DISPLAY + Column.COMMA
                + Column.NAME + Column.COMMA
                + Column.DESCRIPTION + Column.COMMA
                + Column.ADDRESS_1 + Column.COMMA  
                + Column.ADDRESS_2 + Column.COMMA
                + Column.CITY + Column.COMMA
                + Column.STATE + Column.COMMA
                + Column.COUNTRY + Column.COMMA
                + Column.POSTAL_CODE + Column.COMMA
                + Column.PARENT_LOCATION_UUID + Column.COMMA
                + Column.PARENT_LOCATION_DISPLAY + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Location tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertLocation(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Location tableObject) {
        return 0;
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, MasterColumn.ID + MasterColumn.EQUALS + tableObjectID, null);
    }

    public class Column extends MasterColumn {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ADDRESS_1 = "address1";
        public static final String ADDRESS_2 = "address2";
        public static final String POSTAL_CODE = "postalCode";
        public static final String COUNTRY = "country";
        public static final String STATE = "state";
        public static final String CITY = "city";
        public static final String PARENT_LOCATION_UUID = "parentLocationUuid";
        public static final String PARENT_LOCATION_DISPLAY = "parentLocationDisplay";
    }

    @Override
    public String toString() {
        return TABLE_NAME + crateTableDefinition();
    }
}
