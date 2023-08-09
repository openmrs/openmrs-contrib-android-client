package com.openmrs.android_sdk.library.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openmrs.android_sdk.library.databases.entities.DrugEntity

/**
 * The interface Drug Room DAO.
 */
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
}