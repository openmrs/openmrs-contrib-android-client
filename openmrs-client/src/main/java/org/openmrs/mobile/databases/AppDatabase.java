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

package org.openmrs.mobile.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.ConceptRoomDAO;
import org.openmrs.mobile.dao.EncounterRoomDAO;
import org.openmrs.mobile.dao.LocationRoomDAO;
import org.openmrs.mobile.dao.ObservationRoomDAO;
import org.openmrs.mobile.dao.PatientRoomDAO;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.dao.VisitRoomDAO;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;

@Database(entities = {ConceptEntity.class,
        EncounterEntity.class,
        LocationEntity.class, ObservationEntity.class,
        PatientEntity.class,
        VisitEntity.class,
        Provider.class},
        version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract LocationRoomDAO locationRoomDAO();
    public abstract VisitRoomDAO visitRoomDAO();
    public abstract PatientRoomDAO patientRoomDAO();
    public abstract ObservationRoomDAO observationRoomDAO();
    public abstract EncounterRoomDAO encounterRoomDAO();
    public abstract ConceptRoomDAO conceptRoomDAO();
    public abstract ProviderRoomDAO providerRoomDAO();

    private static volatile AppDatabase INSTANCE;

    //TODO remove this public and refactor the packages of classes to incorporate allDAOs under this repository
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    String secretKey = OpenMRS.getInstance().getSecretKey();
                    char[] secretPassPhrase = secretKey.toCharArray();
                    final byte[] passphrase = SQLiteDatabase.getBytes(secretPassPhrase);
                    final SupportFactory factory = new SupportFactory(passphrase);

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, ApplicationConstants.DB_NAME)
                            .allowMainThreadQueries().fallbackToDestructiveMigration()
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
