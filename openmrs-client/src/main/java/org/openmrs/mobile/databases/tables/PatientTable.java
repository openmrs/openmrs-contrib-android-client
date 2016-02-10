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
import org.openmrs.mobile.models.Patient;

public class PatientTable extends Table<Patient> {
    public static final String TABLE_NAME = "patients";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.mobile.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 18;

    @Override
    public String crateTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.DISPLAY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.UUID + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.IDENTIFIER + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.GIVEN_NAME + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.MIDDLE_NAME + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.FAMILY_NAME + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.GENDER + Column.Type.TEXT_TYPE_NOT_NULL
                + Column.BIRTH_DATE + Column.Type.DATE_TYPE_NOT_NULL
                + Column.DEATH_DATE + Column.Type.DATE_TYPE_WITH_COMMA
                + Column.CAUSE_OF_DEATH + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.AGE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.ADDRESS_1 + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.ADDRESS_2 + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.POSTAL_CODE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.COUNTRY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.STATE + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.CITY + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.PHONE + Column.Type.TEXT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.DISPLAY + Column.COMMA 
                + Column.UUID + Column.COMMA
                + Column.IDENTIFIER + Column.COMMA 
                + Column.GIVEN_NAME + Column.COMMA
                + Column.MIDDLE_NAME + Column.COMMA 
                + Column.FAMILY_NAME + Column.COMMA
                + Column.GENDER + Column.COMMA 
                + Column.BIRTH_DATE + Column.COMMA
                + Column.DEATH_DATE + Column.COMMA 
                + Column.CAUSE_OF_DEATH + Column.COMMA
                + Column.AGE + Column.COMMA 
                + Column.ADDRESS_1 + Column.COMMA  
                + Column.ADDRESS_2 + Column.COMMA
                + Column.POSTAL_CODE + Column.COMMA 
                + Column.COUNTRY + Column.COMMA
                + Column.STATE + Column.COMMA 
                + Column.CITY + Column.COMMA 
                + Column.PHONE + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Patient tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertPatient(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Patient tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.updatePatient(helper.getWritableDatabase(), tableObjectID, tableObject);
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, MasterColumn.ID + MasterColumn.EQUALS + tableObjectID, null);
    }

    public class Column extends MasterColumn {
        public static final String IDENTIFIER = "identifier";
        public static final String GIVEN_NAME = "givenName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String FAMILY_NAME = "familyName";
        public static final String GENDER = "gender";
        public static final String BIRTH_DATE = "birthDate";
        public static final String DEATH_DATE = "deathDate";
        public static final String CAUSE_OF_DEATH = "causeOfDeath";
        public static final String AGE = "age";
        public static final String ADDRESS_1 = "address1";
        public static final String ADDRESS_2 = "address2";
        public static final String POSTAL_CODE = "postalCode";
        public static final String COUNTRY = "country";
        public static final String STATE = "state";
        public static final String CITY = "city";
        public static final String PHONE = "phone";
    }

    @Override
    public String toString() {
        return TABLE_NAME + crateTableDefinition();
    }
}
