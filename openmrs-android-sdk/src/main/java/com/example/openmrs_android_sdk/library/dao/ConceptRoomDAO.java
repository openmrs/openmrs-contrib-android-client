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

package com.example.openmrs_android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.databases.entities.ConceptEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ConceptRoomDAO {

    @Insert
    void addConcept(ConceptEntity conceptEntity);

    /**
     * To update a concept, first get the concept using
     * findConceptsByUUID and then send the id to update
     */

    @Update
    void updateConcept(ConceptEntity conceptEntity);

    @Query("SELECT * FROM concepts WHERE uuid = :uuid")
    Single<List<ConceptEntity>> findConceptsByUUID(String uuid);

    @Query("SELECT * FROM concepts WHERE display = :name")
    Single<List<ConceptEntity>> findConceptsByName(String name);

    @Query("SELECT count(*) FROM concepts")
    long getConceptsCount();
}
