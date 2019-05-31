package org.openmrs.mobile.databases;

import android.content.Context;

import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.databases.converters.AppTypeConverter;
import org.openmrs.mobile.databases.dao.ConceptsDao;
import org.openmrs.mobile.databases.dao.VisitsDao;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.entities.EncounterCreateEntity;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.EncounterTypeEntity;
import org.openmrs.mobile.databases.entities.FormResourceEntity;
import org.openmrs.mobile.databases.entities.LinkEntity;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.databases.entities.ObscreateEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.databases.entities.VisitsEntity;
import org.openmrs.mobile.utilities.ApplicationConstants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {
        ConceptEntity.class,
        EncounterEntity.class,
        LocationEntity.class,
        ObservationEntity.class,
        PatientEntity.class,
        VisitsEntity.class,
        LinkEntity.class,
        EncounterTypeEntity.class,
        FormResourceEntity.class,
        EncounterCreateEntity.class,
        ObscreateEntity.class
},
        version = 1)
@TypeConverters({AppTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    //instantiate Dao's

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

    public abstract ConceptsDao conceptsDao();

    public abstract VisitsDao visitDao();
}
