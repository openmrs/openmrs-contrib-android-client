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

package com.example.openmrs_android_sdk.library.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.openmrs_android_sdk.library.dao.AllergyRoomDAO;
import com.example.openmrs_android_sdk.library.dao.ConceptRoomDAO;
import com.example.openmrs_android_sdk.library.dao.EncounterCreateRoomDAO;
import com.example.openmrs_android_sdk.library.dao.EncounterRoomDAO;
import com.example.openmrs_android_sdk.library.dao.EncounterTypeRoomDAO;
import com.example.openmrs_android_sdk.library.dao.FormResourceDAO;
import com.example.openmrs_android_sdk.library.dao.LocationRoomDAO;
import com.example.openmrs_android_sdk.library.dao.ObservationRoomDAO;
import com.example.openmrs_android_sdk.library.dao.PatientRoomDAO;
import com.example.openmrs_android_sdk.library.dao.ProviderRoomDAO;
import com.example.openmrs_android_sdk.library.dao.VisitRoomDAO;
import com.example.openmrs_android_sdk.library.databases.entities.AllergyEntity;
import com.example.openmrs_android_sdk.library.databases.entities.ConceptEntity;
import com.example.openmrs_android_sdk.library.databases.entities.EncounterEntity;
import com.example.openmrs_android_sdk.library.databases.entities.FormResourceEntity;
import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;
import com.example.openmrs_android_sdk.library.databases.entities.ObservationEntity;
import com.example.openmrs_android_sdk.library.databases.entities.PatientEntity;
import com.example.openmrs_android_sdk.library.databases.entities.VisitEntity;
import com.example.openmrs_android_sdk.library.models.EncounterType;
import com.example.openmrs_android_sdk.library.models.Encountercreate;
import com.example.openmrs_android_sdk.library.models.Provider;
import com.example.openmrs_android_sdk.utilities.ApplicationConstants;

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

    public abstract LocationRoomDAO locationRoomDAO();

    public abstract VisitRoomDAO visitRoomDAO();

    public abstract PatientRoomDAO patientRoomDAO();

    public abstract ObservationRoomDAO observationRoomDAO();

    public abstract EncounterRoomDAO encounterRoomDAO();

    public abstract ConceptRoomDAO conceptRoomDAO();

    public abstract ProviderRoomDAO providerRoomDAO();

    public abstract FormResourceDAO formResourceDAO();

    public abstract EncounterTypeRoomDAO encounterTypeRoomDAO();

    public abstract EncounterCreateRoomDAO encounterCreateRoomDAO();

    public abstract AllergyRoomDAO allergyRoomDAO();
}
