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
package com.openmrs.android_sdk.library.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openmrs.android_sdk.library.databases.entities.ProgramEntity

/**
 * The interface Program Room DAO
 */
@Dao
interface ProgramRoomDAO {

    /**
     * Add a Program to the table
     *
     * @param program the program entity
     * @return the long
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProgram(program: ProgramEntity): Long

    /**
     * Fetch all programs from the table
     *
     * @return the list of Program Entities
     */
    @Query("SELECT * FROM programs")
    fun getAllPrograms(): List<ProgramEntity>

    /**
     * Delete a Program to the table with given uuid
     *
     * @param uuid the uuid of the program to delete
     * @return the id that was deleted
     */
    @Query("DELETE FROM programs WHERE uuid = :uuid")
    fun deleteProgramByUuid(uuid: String): Int

    /**
     * Get a program with given uuid
     *
     * @param uuid the uuid of the program to fetch
     * @return the requested Program Entity
     */
    @Query("SELECT * FROM programs WHERE uuid = :uuid")
    fun getProgramByUuid(uuid: String): ProgramEntity?

    /**
     * Get a program with given name
     *
     * @param name the name of the program to fetch
     * @return the requested Program Entity
     */
    @Query("SELECT * FROM programs WHERE name = :name")
    fun getProgramByName(name: String): List<ProgramEntity>

    /**
     * Saves all program entries of a list to database
     *
     * @param programs the program list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePrograms(programs: List<ProgramEntity>): List<Long>
}