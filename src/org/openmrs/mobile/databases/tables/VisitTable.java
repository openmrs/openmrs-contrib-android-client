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
import org.openmrs.mobile.models.Visit;

public class VisitTable extends Table<Visit> {
    public static final String TABLE_NAME = "visits";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.mobile.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 6;

    @Override
    public String crateTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.PATIENT_KEY_ID + Column.Type.INT_TYPE_NOT_NULL
                + Column.UUID + Column.Type.DATE_TYPE_WITH_COMMA
                + Column.VISIT_TYPE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.VISIT_PLACE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.START_DATE + Column.Type.DATE_TYPE_NOT_NULL
                + Column.STOP_DATE + Column.Type.DATE_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.UUID + Column.COMMA
                + Column.PATIENT_KEY_ID + Column.COMMA
                + Column.VISIT_TYPE + Column.COMMA
                + Column.VISIT_PLACE + Column.COMMA
                + Column.START_DATE + Column.COMMA
                + Column.STOP_DATE + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Visit tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertVisit(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Visit tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.updateVisit(helper.getWritableDatabase(), tableObjectID, tableObject);
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, MasterColumn.ID + MasterColumn.EQUALS + tableObjectID, null);
    }

    public class Column extends MasterColumn {
        public static final String PATIENT_KEY_ID = "patient_id";
        public static final String VISIT_TYPE = "visit_type";
        public static final String VISIT_PLACE = "visit_place";
        public static final String START_DATE = "start_date";
        public static final String STOP_DATE = "stop_date";
    }

    @Override
    public String toString() {
        return TABLE_NAME + crateTableDefinition();
    }
}
