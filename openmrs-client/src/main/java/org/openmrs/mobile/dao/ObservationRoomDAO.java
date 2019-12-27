/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.dao;

        import android.database.Observable;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.Query;
        import androidx.room.Update;

        import org.openmrs.mobile.databases.entities.ObservationEntity;
        import org.openmrs.mobile.models.Observation;

        import java.util.List;

        import rx.Single;

@Dao
public interface ObservationRoomDAO {

    @Insert
    Single<Long> saveObservation(ObservationEntity observationEntity);

    @Update
    Observable<Boolean> updateObservation(ObservationEntity observationEntity);

    @Delete
    void deleteObservation(long observationID);

    @Query("SELECT * FROM observations WHERE encounter_id = :encounterID")
    Single<List<ObservationEntity>> findObservationByEncounterID(Long encounterID);

    @Query("SELECT * FROM observations WHERE uuid = :observationUUID")
    Single<ObservationEntity> getObservationByUUID(final String observationUUID);

}


