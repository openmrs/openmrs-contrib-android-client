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
import org.openmrs.mobile.dao.EncounterRoomDAO;
import org.openmrs.mobile.dao.LocationRoomDAO;
import org.openmrs.mobile.dao.VisitRoomDAO;
import org.openmrs.mobile.dao.PatientRoomDAO;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.openmrs.mobile.utilities.ApplicationConstants;

@Database(entities = {ConceptEntity.class,
        EncounterEntity.class,
        LocationEntity.class, ObservationEntity.class,
        PatientEntity.class,
        VisitEntity.class},
        version = 1)

public abstract class AppDatabase extends RoomDatabase {

    //instantiate Dao's
    public abstract LocationRoomDAO locationRoomDAO();
    public abstract VisitRoomDAO visitRoomDAO();
    public abstract PatientRoomDAO patientRoomDAO();
    public abstract EncounterRoomDAO encounterRoomDAO();

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, ApplicationConstants.DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
