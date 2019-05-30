package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.databases.entities.ObservationEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ObservationDao {

    @Query("SELECT * FROM observations")
    Flowable<List<ObservationEntity>> getAll();

    @Insert
    void save(ObservationEntity observation);

    @Query("DELETE FROM observations WHERE uuid = :uuid")
    void delete(String uuid);

    // TODO UPDATE is combination of delete and insert

    @Query("SELECT * FROM observations WHERE encounter_id = :id")
    Flowable<List<ObservationEntity>> findObservationByEncounterId(String id);

    @Query("SELECT * FROM observations WHERE uuid = :id")
    Flowable<List<ObservationEntity>> findObservationByUUID(String id);


}
