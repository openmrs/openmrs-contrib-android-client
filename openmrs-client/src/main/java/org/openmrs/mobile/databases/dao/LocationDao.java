package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.databases.entities.LocationEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM locations")
    Flowable<List<LocationEntity>> getAll();

    @Insert
    void save(LocationEntity location);

    @Query("DELETE FROM locations")
    void deleteAll();

    @Query("SELECT * FROM locations WHERE display = :locationName")
    Flowable<List<LocationEntity>> findLocationByName(String locationName);
}
