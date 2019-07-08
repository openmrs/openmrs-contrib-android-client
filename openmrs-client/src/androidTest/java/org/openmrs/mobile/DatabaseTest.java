package org.openmrs.mobile;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openmrs.mobile.databases.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    protected AppDatabase database;

    @Before
    public void setUp() throws Exception {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }
}
