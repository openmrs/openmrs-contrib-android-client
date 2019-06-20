package org.openmrs.mobile.test;
import com.google.common.collect.ImmutableList;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.DBOpenHelper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenMRS.class)
public class DBOpenHelperTest {
    private static final ImmutableList<String> createTableQueries = ImmutableList.of(
            "CREATE TABLE patients(_id integer primary key autoincrement,synced boolean,display text,uuid text,identifier text,givenName text not null,middleName text,familyName text not null,gender text not null,birthDate data not null,deathDate date,causeOfDeath text,age text,photo blob,address1 text,address2 text,postalCode text,country text,state text,city text,encounters text);",
            "CREATE TABLE concepts(_id integer primary key autoincrement,uuid text not null,display text);",
            "CREATE TABLE visits(_id integer primary key autoincrement,patient_id integer not null,uuid data not null,visit_type text,visit_place text,start_date data not null,stop_date date);",
            "CREATE TABLE encounters(_id integer primary key autoincrement,visit_id integer,uuid text not null,display text,encounterDatetime data not null,type date,patient_uuid text,form_uuid text);",
            "CREATE TABLE observations(_id integer primary key autoincrement,encounter_id integer not null,uuid text not null,display text,displayValue text,diagnosisOrder text,diagnosisList text,diagnosisCertainty text,diagnosisNote text,conceptUuid text);",
            "CREATE TABLE locations(_id integer primary key autoincrement,uuid text not null,display text,name text,description text,address1 text,address2 text,city text,state text,country text,postalCode text,parentLocationUuid text);"
    );

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OpenMRS openMRS;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        Mockito.lenient().when(openMRS.getResources().getString(R.string.dbname)).thenReturn("openmrs.db");
        Mockito.lenient().when(openMRS.getResources().getInteger(R.integer.dbversion)).thenReturn(9);
        PowerMockito.mockStatic(OpenMRS.class);
        Mockito.lenient().when(OpenMRS.getInstance()).thenReturn(openMRS);
    }

    @Test
    public void onCreateShouldCreateTables() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(null);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        dbOpenHelper.onCreate(sqLiteDatabase);
        verify(sqLiteDatabase, atLeastOnce()).execSQL(argumentCaptor.capture());

        List<String> executedQueries = argumentCaptor.getAllValues();
        createTableQueries.forEach(query -> assertThat(executedQueries, hasItem(query)));
    }

    @Test
    public void shouldReturnDB_encryptedDB_matchingKey() {
        String key = "database key";

        DBOpenHelper dbOpenHelper = mockEncryptedDBWithKey(key);
        mockKey(key);

        assertEquals(sqLiteDatabase, dbOpenHelper.getWritableDatabase());
    }

    @Test(expected = SQLiteException.class)
    public void shouldThrowSQLiteException_encryptedDB_wrongKey() {
        String key1 = "database key";
        String key2 = "wrong input";

        DBOpenHelper dbOpenHelper = mockEncryptedDBWithKey(key1);
        mockKey(key2);

        dbOpenHelper.getWritableDatabase();
    }

    @Test
    public void shouldReturnDB_unencryptedDB_nonEmptyKey() {
        DBOpenHelper dbOpenHelper = mockUnencryptedDB();
        mockKey("database key");

        assertEquals(sqLiteDatabase, dbOpenHelper.getWritableDatabase());
    }

    @Test
    public void shouldReturnDB_unencryptedDB_emptyKey() {
        DBOpenHelper dbOpenHelper = mockUnencryptedDB();
        mockKey("");

        assertEquals(sqLiteDatabase, dbOpenHelper.getWritableDatabase());
    }

    private DBOpenHelper mockEncryptedDBWithKey(String key) {
        DBOpenHelper dbOpenHelper = spy(new DBOpenHelper(null));

        doThrow(new SQLiteException()).when(dbOpenHelper).getWritableDatabase(Mockito.nullable(String.class));
        doReturn(sqLiteDatabase).when(dbOpenHelper).getWritableDatabase(key);

        return dbOpenHelper;
    }

    private DBOpenHelper mockUnencryptedDB() {
        DBOpenHelper dbOpenHelper = spy(new DBOpenHelper(null));

        doThrow(new SQLiteException()).when(dbOpenHelper).getWritableDatabase(Mockito.anyString());
        doReturn(sqLiteDatabase).when(dbOpenHelper).getWritableDatabase("");

        return dbOpenHelper;
    }

    private void mockKey(String key) {
        Mockito.lenient().when(openMRS.getSecretKey()).thenReturn(key);
    }
}
