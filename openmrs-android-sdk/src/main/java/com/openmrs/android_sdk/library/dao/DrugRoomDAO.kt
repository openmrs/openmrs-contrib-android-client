/* The contents of this file are subject to the OpenMRS Public License
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
package com.openmrs.android_sdk.library.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openmrs.android_sdk.library.databases.entities.DrugEntity

/**
 * The interface Drug Room DAO.
 */
@Dao
interface DrugRoomDAO {

    /**
     * Get all drug entries from table.
     *
     * @return the list of Drug Entity
     */
    @Query("SELECT * FROM drugs")
    fun getAllDrugs(): List<DrugEntity>

    /**
     * Get a Drug by uuid
     *
     * @param uuid the drug uuid
     * @return the Drug Entity
     */
    @Query("SELECT * FROM drugs WHERE uuid = :uuid")
    fun getDrugByUuid(uuid: String): DrugEntity?

    /**
     * Create a Drug
     *
     * @param drug the Drug Entity object
     * @return the Long id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createDrug(drug: DrugEntity): Long

    /**
     * Update a Drug
     *
     * @param drug the updated drug
     */
    @Update
    fun updateDrug(drug: DrugEntity)

    /**
     * Delete a drug
     *
     * @param uuid the drug uuid
     */
    @Query("DELETE FROM drugs WHERE uuid = :uuid")
    fun deleteDrugByUuid(uuid: String)

    /**
     * Saves all drug enteries of a list to database
     *
     * @param drugs the drug list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateDrugs(drugs: List<DrugEntity>): List<Long>
}