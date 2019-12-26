package org.openmrs.mobile.test.DAOClasses;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.LocationEntity;

@RunWith(JUnit4.class)
public class LocationRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;

    @Before
    public void initDb() throws Exception {
        mDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void InsertLocation() {

        LocationEntity entity = new LocationEntity();
        entity.setId(10L);
        entity.setDisplay("location");
        entity.setUuid("uuid");
        entity.setName("name");
        entity.setDescription("description");
        entity.setAddress_1("address 1");
        entity.setAddress_2("address2");
        entity.setCity("city");
        entity.setState("state");
        entity.setCountry("country");
        entity.setPostalCode("postal code");
        entity.setParentLocationuuid("location");

        mDatabase.locationDAO().saveLocation(entity);

        /**
         * code to get and verify the location
         */
    }
}
