/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.openmrs.android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.openmrs.android_sdk.library.models.EncounterType;

import java.util.List;

/**
 * The interface Encounter type room dao.
 */
@Dao
public interface EncounterTypeRoomDAO {
    /**
     * Add encounter type long.
     *
     * @param encounterType the encounter type
     * @return the long
     */
    @Insert
    long addEncounterType(EncounterType encounterType);

    /**
     * Delete all encounter types.
     */
    @Query("DELETE FROM encounterType")
    void deleteAllEncounterTypes();

    /**
     * Gets encounter type by form name.
     *
     * @param formName the form name
     * @return the encounter type by form name
     */
    @Query("Select * FROM encounterType WHERE display = :formName")
    EncounterType getEncounterTypeByFormName(String formName);

    /**
     * Gets all encounter types.
     *
     * @return the all encounter types
     */
    @Query("SELECT * FROM encounterType")
    List<EncounterType> getAllEncounterTypes();
}
