package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.databases.entities.VisitsEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface VisitsDao {

    @Query("SELECT * FROM visits")
    Flowable<List<VisitsEntity>> getAll();

    @Insert
    void save(VisitsEntity entity);

    @Query("DELETE FROM visits WHERE uuid = :uuid")
    void delete(String uuid);

    @Query("DELETE FROM visits WHERE patient_id = :id")
    void deleteByPatientId(String id);

    @Query("SELECT * FROM visits WHERE stop_date IS NULL OR stop_date = '' ORDER BY start_date DESC")
    Flowable<List<VisitsEntity>> getActiveVisits();

    @Query("SELECT * FROM visits WHERE patient_id = :patientId ORDER BY start_date DESC")
    Flowable<List<VisitsEntity>> getVisitsByPatientId(String patientId);

    @Query("SELECT * FROM visits WHERE patient_id = :patientId AND (stop_date IS NULL OR stop_date = '') ORDER BY start_date DESC")
    Flowable<List<VisitsEntity>> getActiveVisitsByPatientId(String patientId);

    // getVisitById and getVisitIdByUUID are very strange queries, double check them due your work
    //@Query("SELECT * FROM visits WHERE _id = :id ORDER BY start_date DESC")
    //Flowable<List<VisitsEntity>> getVisitsById(Long id);

    @Query("SELECT * FROM visits WHERE uuid = :uuid ORDER BY start_date DESC")
    Flowable<List<VisitsEntity>> getVisitsByUuid(String uuid);

    // TODO consider Views for more granulate queries https://developer.android.com/training/data-storage/room/creating-views
}
