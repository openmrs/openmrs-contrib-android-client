package org.openmrs.mobile.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.openmrs.mobile.databases.entities.EncounterEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface EncounterDao {

    @Query("SELECT * FROM encounters")
    Flowable<List<EncounterEntity>> getAll();

    @Insert
    void save(EncounterEntity entity); // by id

    @Query("SELECT * FROM encounters WHERE display = :formname")
    Flowable<List<EncounterEntity>> getEncounterTypeByFormName(String formname);

    @Query("SELECT _id FROM encounters WHERE visit_id IS NULL AND patient_uuid = :patientUuid")
    Flowable<Long> getLastVitalsEncounterId(String patientUuid);

    @Query("SELECT * FROM encounters WHERE patient_uuid = :patientUuid AND type = :type ORDER BY encounterDatetime DESC LIMIT 1")
    Flowable<List<EncounterEntity>> getLastVitalsEncounter(String patientUuid, String type);

    @Query("DELETE FROM encounters WHERE uuid = :uuid")
    void delete(String uuid);

    // UPDATE operation is combination of delete and insert

    @Query("SELECT * FROM encounters WHERE visit_id = :visitId")
    Flowable<List<EncounterEntity>> getEncounterByVisitId(Long visitId);

    // TODO getAllEncountersByType

    @Query("SELECT * FROM encounters WHERE uuid = :encounterUuid")
    Flowable<List<EncounterEntity>> getEncounterByUUID(String encounterUuid);
}
