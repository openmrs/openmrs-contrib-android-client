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
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AllergyRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AllergyEntity allergyEntity1 = createAllergyEntity(10L, "uuid 1", "1", "comment 1", "severity display 1", "severity uuid 1", "allergen display 1", "allergen uuid 1");
    private AllergyEntity allergyEntity2 = createAllergyEntity(20L, "uuid 2", "2", "comment 2", "severity display 2", "severity uuid 2", "allergen display 2", "allergen uuid 2");
    private AppDatabase mDatabase;
    private AllergyRoomDAO allergyRoomDAO;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        allergyRoomDAO = mDatabase.allergyRoomDAO();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void deleteAllAllergies_shouldDeleteAllAllergiesCorrectly() {
        allergyRoomDAO.saveAllergy(allergyEntity1);
        allergyRoomDAO.saveAllergy(allergyEntity2);
        Assert.assertEquals(allergyRoomDAO.getAllAllergiesByPatientID("1").size(), 1L);
        allergyRoomDAO.deleteAllPatientAllergy("1");
        Assert.assertEquals(allergyRoomDAO.getAllAllergiesByPatientID("1").size(), 0L);
    }

    @Test
    public void updateAllergy_shouldUpdateAllergyCorrectly() {
        allergyRoomDAO.saveAllergy(allergyEntity1);
        allergyEntity1.setComment("changed");
        allergyRoomDAO.updateAllergy(allergyEntity1);
        Assert.assertEquals(renderAllergyString(allergyRoomDAO.getAllAllergiesByPatientID("1").get(0)), renderAllergyString(allergyEntity1));
    }

    @Test
    public void getAllAllergies_shouldGetAllAllergiesCorrectly() {
        allergyRoomDAO.saveAllergy(allergyEntity1);
        allergyRoomDAO.saveAllergy(allergyEntity2);
        Assert.assertEquals(allergyRoomDAO.getAllAllergiesByPatientID("2").size(), 1L);
        Assert.assertEquals(renderAllergyString(allergyRoomDAO.getAllAllergiesByPatientID("1").get(0)), renderAllergyString(allergyEntity1));
        Assert.assertEquals(renderAllergyString(allergyRoomDAO.getAllAllergiesByPatientID("2").get(0)), renderAllergyString(allergyEntity2));
    }

    @Test
    public void deleteAllergyByUUID_shouldCorrectlyDeleteAllergyByUUID() {
        allergyRoomDAO.saveAllergy(allergyEntity1);
        allergyRoomDAO.saveAllergy(allergyEntity2);
        allergyRoomDAO.deleteAllergyByUUID(allergyEntity1.getUuid());
        Assert.assertEquals(allergyRoomDAO.getAllAllergiesByPatientID("1").size(), 0L);
        Assert.assertEquals(allergyRoomDAO.getAllAllergiesByPatientID("2").size(), 1L);
    }

    private AllergyEntity createAllergyEntity(long id, String uuid, String patientID, String comment, String severityDisplay, String severityUUID, String allergenDisplay, String allergenUUID) {
        AllergyEntity allergyEntity = new AllergyEntity();
        allergyEntity.setId(id);
        allergyEntity.setUuid(uuid);
        allergyEntity.setPatientId(patientID);
        allergyEntity.setComment(comment);
        allergyEntity.setSeverityDisplay(severityDisplay);
        allergyEntity.setSeverityUUID(severityUUID);
        allergyEntity.setAllergenDisplay(allergenDisplay);
        allergyEntity.setAllergenUUID(allergenUUID);
        return allergyEntity;
    }

    String renderAllergyString(AllergyEntity allergyEntity) {
        return allergyEntity.getId() + allergyEntity.getPatientId()
                + allergyEntity.getComment() + allergyEntity.getSeverityDisplay()
                + allergyEntity.getSeverityUUID() + allergyEntity.getAllergenDisplay()
                + allergyEntity.getAllergenUUID();
    }
}
