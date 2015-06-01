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
import org.openmrs.mobile.models.Observation;

public class ObservationTable extends Table<Observation> {
    public static final String TABLE_NAME = "observations";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.mobile.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 8;

    @Override
    public String crateTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.ENCOUNTER_KEY_ID  + Column.Type.INT_TYPE_NOT_NULL
                + Column.UUID + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DISPLAY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DISPLAY_VALUE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DIAGNOSIS_ORDER + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DIAGNOSIS_LIST + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DIAGNOSIS_CERTAINTY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.DIAGNOSIS_NOTE + Column.Type.TEXT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.ENCOUNTER_KEY_ID + Column.COMMA
                + Column.UUID + Column.COMMA
                + Column.DISPLAY + Column.COMMA
                + Column.DISPLAY_VALUE + Column.COMMA
                + Column.DIAGNOSIS_ORDER + Column.COMMA
                + Column.DIAGNOSIS_LIST + Column.COMMA
                + Column.DIAGNOSIS_CERTAINTY + Column.COMMA
                + Column.DIAGNOSIS_NOTE + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Observation tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertObservation(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Observation tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.updateObservation(helper.getWritableDatabase(), tableObjectID, tableObject);
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        String where = String.format("%s = ?", ObservationTable.Column.ID);
        String[] whereArgs = new String[]{String.valueOf(tableObjectID)};

        openHelper.getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
    }

    public class Column extends MasterColumn {
        public static final String ENCOUNTER_KEY_ID = "encounter_id";
        public static final String DISPLAY_VALUE = "displayValue";
        public static final String DIAGNOSIS_ORDER = "diagnosisOrder";
        public static final String DIAGNOSIS_LIST = "diagnosisList";
        public static final String DIAGNOSIS_CERTAINTY = "diagnosisCertainty";
        public static final String DIAGNOSIS_NOTE = "diagnosisNote";

    }

    @Override
    public String toString() {
        return TABLE_NAME + crateTableDefinition();
    }
}
