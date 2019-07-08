package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.openmrs.mobile.databases.entities.ConceptEntity;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ConceptsDao {

    @Query("SELECT * FROM concepts")
    Flowable<List<ConceptEntity>> getAll();

    @Insert
    void save(ConceptEntity concept);

    @Query("UPDATE concepts SET uuid = :uuid, display = :display WHERE uuid = :olduuid")
    void update(String olduuid, String uuid, String display);

    @Query("SELECT * FROM concepts WHERE uuid = :uuid")
    Flowable<ConceptEntity> findByUUID(String uuid);

    @Query("SELECT count(*) FROM concepts")
    Flowable<Long> getCount();
}
