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
import androidx.room.Update;

import com.openmrs.android_sdk.library.models.Encountercreate;

import java.util.List;

/**
 * The interface Encounter create room dao.
 */
@Dao
public interface EncounterCreateRoomDAO {

    /**
     * Add encounter created long.
     *
     * @param encountercreate the encountercreate
     * @return the long
     */
    @Insert
    long addEncounterCreated(Encountercreate encountercreate);

    /**
     * Update existing encounter int.
     *
     * @param encountercreate the encountercreate
     * @return the int
     */
    @Update
    int updateExistingEncounter(Encountercreate encountercreate);

    /**
     * Gets all created encounters.
     *
     * @return the all created encounters
     */
    @Query("Select * FROM encountercreate")
    List<Encountercreate> getAllCreatedEncounters();

    /**
     * Gets created encounters by id.
     *
     * @param id the id
     * @return the created encounters by id
     */
    @Query("Select * FROM encountercreate WHERE _id =:id")
    Encountercreate getCreatedEncountersByID(long id);
}
