package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.databases.entities.PatientEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM patients")
    Flowable<List<PatientEntity>> getAll();

    @Insert
    void save(PatientEntity entity);

    @Query("DELETE FROM patients WHERE uuid = :uuid")
    void delete(String uuid);

    // TODO update is transaction from insert and delete

    @Query("SELECT * FROM patients WHERE uuid = :uuid")
    Flowable<List<PatientEntity>> getPatientByUuid(String uuid);

    @Query("SELECT * FROM patients WHERE synced = 0")
    Flowable<List<PatientEntity>> getUnsyncedPatients();

    @Query("SELECT * FROM patients WHERE _id = :id")
    Flowable<List<PatientEntity>> getPatientById(long id);
}
