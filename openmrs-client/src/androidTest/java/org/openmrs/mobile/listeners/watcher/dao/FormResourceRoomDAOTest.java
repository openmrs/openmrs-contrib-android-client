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

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FormResourceRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FormResourceEntity formResourceEntity1 = createFormResourceWithResourceList("firstForm1", "json", "123_123");
    private FormResourceEntity formResourceEntity2 = createFormResourceWithResourceList("firstForm2", "json", "124-124");

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
    public void findFormResourceByName_ShouldFindCorrectFormResourceByName() {
        mDatabase.formResourceDAO().addFormResource(formResourceEntity1);

        mDatabase.formResourceDAO().getFormResourceByName("firstForm1")
                .test()
                .assertValue(formResourceEntity ->
                        Objects.equals(formResourceEntity.getName(), formResourceEntity1.getName())
                                && Objects.equals(formResourceEntity.getValueReference(), formResourceEntity1.getValueReference())
                                && Objects.equals(formResourceEntity.getUuid(), formResourceEntity1.getUuid()));
    }

    @Test
    public void findFormResourceList_ShouldFindCorrectFormResourceList() {
        mDatabase.formResourceDAO().addFormResource(formResourceEntity1);
        mDatabase.formResourceDAO().addFormResource(formResourceEntity2);

        mDatabase.formResourceDAO().getFormResourceList()
                .test()
                .assertValue(formResourceEntities -> Objects.equals(formResourceEntities.size(), 2)
                        && Objects.equals(formResourceEntities.get(0).getName(), formResourceEntity1.getName())
                        && Objects.equals(formResourceEntities.get(0).getValueReference(), formResourceEntity1.getValueReference())
                        && Objects.equals(formResourceEntities.get(0).getUuid(), formResourceEntity1.getUuid())
                        && Objects.equals(formResourceEntities.get(1).getName(), formResourceEntity2.getName())
                        && Objects.equals(formResourceEntities.get(1).getValueReference(), formResourceEntity2.getValueReference())
                        && Objects.equals(formResourceEntities.get(1).getUuid(), formResourceEntity2.getUuid()));
    }

    @Test
    public void findFormByUuid_ShouldFindCorrectFormByUuid() {
        mDatabase.formResourceDAO().addFormResource(formResourceEntity1);
        mDatabase.formResourceDAO().addFormResource(formResourceEntity2);

        mDatabase.formResourceDAO().getFormByUuid("123_123")
                .test()
                .assertValue(formResourceEntity -> Objects.equals(formResourceEntity.getName(), formResourceEntity1.getName())
                        && Objects.equals(formResourceEntity.getValueReference(), formResourceEntity1.getValueReference())
                        && Objects.equals(formResourceEntity.getUuid(), formResourceEntity1.getUuid()));
    }

    @Test
    public void deleteALlForms_ShouldDeleteALlFormsCorrectly() {
        mDatabase.formResourceDAO().addFormResource(formResourceEntity1);
        mDatabase.formResourceDAO().addFormResource(formResourceEntity2);
        mDatabase.formResourceDAO().deleteAllForms();

        mDatabase.formResourceDAO().getFormResourceList()
                .test()
                .assertValue(formResourceEntities -> Objects.equals(formResourceEntities.size(), 0));
    }

    @Test
    public void saveFormResource_ShouldSaveFormResource() {
        mDatabase.formResourceDAO().addFormResource(formResourceEntity1);
        mDatabase.formResourceDAO().addFormResource(formResourceEntity2);

        mDatabase.formResourceDAO().getFormResourceList()
                .test()
                .assertValue(formResourceEntities -> Objects.equals(formResourceEntities.size(), 2));

    }

    private FormResourceEntity createFormResourceWithResourceList(String formName, String resName, String uuid) {
        final String exampleJson = getExampleFormResourceJson(formName, uuid);
        final String exampleJson1 = getExampleFormResourceJson(resName, uuid);
        FormResourceEntity formResourceEntity = new Gson().fromJson(exampleJson, FormResourceEntity.class);

        List<FormResourceEntity> formResources = new ArrayList<>();
        formResources.add(new Gson().fromJson(exampleJson1, FormResourceEntity.class));
        formResourceEntity.setResources(formResources);

        formResourceEntity.setValueReference(exampleJson1);
        return formResourceEntity;
    }

    private String getExampleFormResourceJson(String name, String uuid) {
        return "{" +
                "\"display\":\"json\"," +
                "\"uuid\":\"" + uuid + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"valueReference\":\"" +
                "{" +
                "\\\"name\\\":\\\"Some Form\\\"," +
                "\\\"uuid\\\":\\\"77174d67-954f-45c4-a782-d157e70d59f4\\\"" +
                "}\"" +
                "}";
    }
}
