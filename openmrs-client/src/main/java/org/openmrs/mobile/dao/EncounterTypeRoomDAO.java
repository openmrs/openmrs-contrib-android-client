/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.models.EncounterType;

import java.util.List;

@Dao
public interface EncounterTypeRoomDAO {
    @Insert
    long addEncounterType(EncounterType encounterType);

    @Query("DELETE FROM encounterType")
    void deleteAllEncounterTypes();

    @Query("Select * FROM encounterType WHERE display = :formName")
    EncounterType getEncounterTypeByFormName(String formName);

    @Query("SELECT * FROM encounterType")
    List<EncounterType> getAllEncounterTypes();
}
