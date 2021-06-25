/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.models.Encountercreate;

import java.util.List;

@Dao
public interface EncounterCreateRoomDAO {

    @Insert
    long addEncounterCreated(Encountercreate encountercreate);

    @Update
    int updateExistingEncounter(Encountercreate encountercreate);

    @Query("Select * FROM encountercreate")
    List<Encountercreate> getAllCreatedEncounters();

    @Query("Select * FROM encountercreate WHERE _id =:id")
    Encountercreate getCreatedEncountersByID(long id);
}
