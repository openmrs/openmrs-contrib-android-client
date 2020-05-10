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

package org.openmrs.mobile.listeners.watcher.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.utilities.ActiveAndroid.util.Log;

import java.util.List;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConceptRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ConceptEntity expectedConceptEntity1 = newConceptEntity(10L, "123", "Concept 1", "name_123");
    private ConceptEntity expectedConceptEntity2 = newConceptEntity(20L, "124", "Concept 2", "name_124");

    private AppDatabase mDatabase;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void findConceptsByUUID_shouldGetCorrectConceptByUUID() {
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity1);
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity2);

        mDatabase.conceptRoomDAO().findConceptsByUUID(expectedConceptEntity1.getUuid())
                .test()
                .assertValue(conceptEntities -> {
                    ConceptEntity actualEntity = conceptEntities.get(0);
                    return Objects.equals(actualEntity.getUuid(), expectedConceptEntity1.getUuid())
                            && Objects.equals(actualEntity.getName(), expectedConceptEntity1.getName())
                            && Objects.equals(actualEntity.getDisplay(), expectedConceptEntity1.getDisplay());
                });
    }

    @Test
    public void findConceptsByName_shouldGetCorrectConceptByName() {
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity1);
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity2);

        mDatabase.conceptRoomDAO().findConceptsByName(expectedConceptEntity1.getName())
                .test()
                .assertValue(conceptEntities -> {
                    ConceptEntity actualEntity = conceptEntities.get(0);
                    return Objects.equals(actualEntity.getUuid(), expectedConceptEntity1.getUuid())
                            && Objects.equals(actualEntity.getName(), expectedConceptEntity1.getName())
                            && Objects.equals(actualEntity.getDisplay(), expectedConceptEntity1.getDisplay());
                });
    }

    @Test
    public void getConceptsCount_shouldGetCorrectConceptCount() {
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity1);
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity2);

        mDatabase.conceptRoomDAO().getConceptsCount()
                .test()
                .assertValue(aLong -> Objects.equals(aLong, 2L));
    }

    @Test
    public void updateConcept_ShouldCorrectlyUpdateConcept() {
        mDatabase.conceptRoomDAO().saveConcept(expectedConceptEntity1);
        expectedConceptEntity1.setName("name_123_123");

        mDatabase.conceptRoomDAO().updateConcept(expectedConceptEntity1);

        mDatabase.conceptRoomDAO().findConceptsByUUID(expectedConceptEntity1.getUuid())
                .test()
                .assertValue(conceptEntities -> {
                    ConceptEntity actualEntity = conceptEntities.get(0);
                    return Objects.equals(actualEntity.getName(), expectedConceptEntity1.getName());
                });
    }

    private ConceptEntity newConceptEntity(long id, String uuid, String display, String name) {
        ConceptEntity entity = new ConceptEntity();

        entity.setId(id);
        entity.setUuid(uuid);
        entity.setDisplay(display);
        entity.setName(name);

        return entity;
    }
}
