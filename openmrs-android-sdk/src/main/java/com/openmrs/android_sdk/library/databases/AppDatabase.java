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

package com.openmrs.android_sdk.library.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.dao.ConceptRoomDAO;
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO;
import com.openmrs.android_sdk.library.dao.EncounterRoomDAO;
import com.openmrs.android_sdk.library.dao.EncounterTypeRoomDAO;
import com.openmrs.android_sdk.library.dao.FormResourceDAO;
import com.openmrs.android_sdk.library.dao.LocationRoomDAO;
import com.openmrs.android_sdk.library.dao.ObservationRoomDAO;
import com.openmrs.android_sdk.library.dao.PatientRoomDAO;
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO;
import com.openmrs.android_sdk.library.dao.VisitRoomDAO;
import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;
import com.openmrs.android_sdk.library.databases.entities.ConceptEntity;
import com.openmrs.android_sdk.library.databases.entities.EncounterEntity;
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;
import com.openmrs.android_sdk.library.databases.entities.PatientEntity;
import com.openmrs.android_sdk.library.databases.entities.VisitEntity;
import com.openmrs.android_sdk.library.models.EncounterType;
import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

/**
 * The type App database.
 */
@Database(entities = {ConceptEntity.class,
        EncounterEntity.class,
        LocationEntity.class,
        ObservationEntity.class,
        PatientEntity.class,
        VisitEntity.class,
        Provider.class,
        FormResourceEntity.class,
        EncounterType.class,
        Encountercreate.class,
        AllergyEntity.class},
        version = 1)

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /**
     * Gets database.
     *
     * @param context the context
     * @return the database
     */
//TODO remove this public and refactor the packages of classes to incorporate allDAOs under this repository
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, ApplicationConstants.DB_NAME)
                            .allowMainThreadQueries().fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Location room dao location room dao.
     *
     * @return the location room dao
     */
    public abstract LocationRoomDAO locationRoomDAO();

    /**
     * Visit room dao visit room dao.
     *
     * @return the visit room dao
     */
    public abstract VisitRoomDAO visitRoomDAO();

    /**
     * Patient room dao patient room dao.
     *
     * @return the patient room dao
     */
    public abstract PatientRoomDAO patientRoomDAO();

    /**
     * Observation room dao observation room dao.
     *
     * @return the observation room dao
     */
    public abstract ObservationRoomDAO observationRoomDAO();

    /**
     * Encounter room dao encounter room dao.
     *
     * @return the encounter room dao
     */
    public abstract EncounterRoomDAO encounterRoomDAO();

    /**
     * Concept room dao concept room dao.
     *
     * @return the concept room dao
     */
    public abstract ConceptRoomDAO conceptRoomDAO();

    /**
     * Provider room dao provider room dao.
     *
     * @return the provider room dao
     */
    public abstract ProviderRoomDAO providerRoomDAO();

    /**
     * Form resource dao form resource dao.
     *
     * @return the form resource dao
     */
    public abstract FormResourceDAO formResourceDAO();

    /**
     * Encounter type room dao encounter type room dao.
     *
     * @return the encounter type room dao
     */
    public abstract EncounterTypeRoomDAO encounterTypeRoomDAO();

    /**
     * Encounter create room dao encounter create room dao.
     *
     * @return the encounter create room dao
     */
    public abstract EncounterCreateRoomDAO encounterCreateRoomDAO();

    /**
     * Allergy room dao allergy room dao.
     *
     * @return the allergy room dao
     */
    public abstract AllergyRoomDAO allergyRoomDAO();
}
