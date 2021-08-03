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
import androidx.test.platform.app.InstrumentationRegistry;

import com.openmrs.android_sdk.library.models.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.openmrs.android_sdk.library.databases.AppDatabase;

import java.util.Objects;

public class ProviderRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private Provider expectedProviderEntity1 = newProvider(10L, "123", "provider 1", "name_123", false);
    private Provider expectedProviderEntity2 = newProvider(20L, "124", "provider 2", "name_124", false);
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
    public void getProviders_shouldGetAllProviders() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity2);

        mDatabase.providerRoomDAO().getProviderList()
            .test()
            .assertValue(providers -> {
                Provider actualEntity1 = providers.get(0);
                Provider actualEntity2 = providers.get(1);
                return providers.size() == 2
                    && Objects.equals(actualEntity1.getUuid(), expectedProviderEntity1.getUuid())
                    && Objects.equals(actualEntity1.getId(), expectedProviderEntity1.getId())
                    && Objects.equals(actualEntity1.getDisplay(), expectedProviderEntity1.getDisplay())
                    && Objects.equals(actualEntity1.getIdentifier(), expectedProviderEntity1.getIdentifier())
                    && Objects.equals(actualEntity1.getRetired(), expectedProviderEntity1.getRetired())

                    && Objects.equals(actualEntity2.getUuid(), expectedProviderEntity2.getUuid())
                    && Objects.equals(actualEntity2.getId(), expectedProviderEntity2.getId())
                    && Objects.equals(actualEntity2.getDisplay(), expectedProviderEntity2.getDisplay())
                    && Objects.equals(actualEntity2.getIdentifier(), expectedProviderEntity2.getIdentifier())
                    && Objects.equals(actualEntity2.getRetired(), expectedProviderEntity2.getRetired());
            });
    }

    @Test
    public void findProviderByUUID_shouldGetCorrectProviderByUUID() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity2);

        mDatabase.providerRoomDAO().findProviderByUUID(expectedProviderEntity1.getUuid())
            .test()
            .assertValue(provider -> {
                Provider actualEntity = provider;
                return Objects.equals(actualEntity.getUuid(), expectedProviderEntity1.getUuid())
                    && Objects.equals(actualEntity.getId(), expectedProviderEntity1.getId())
                    && Objects.equals(actualEntity.getDisplay(), expectedProviderEntity1.getDisplay())
                    && Objects.equals(actualEntity.getIdentifier(), expectedProviderEntity1.getIdentifier())
                    && Objects.equals(actualEntity.getRetired(), expectedProviderEntity1.getRetired());
            });
    }

    @Test
    public void findProviderById_shouldGetCorrectProviderById() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity2);

        mDatabase.providerRoomDAO().findProviderByID(expectedProviderEntity1.getId())
            .test()
            .assertValue(provider -> {
                Provider actualEntity = provider;
                return Objects.equals(actualEntity.getUuid(), expectedProviderEntity1.getUuid())
                    && Objects.equals(actualEntity.getId(), expectedProviderEntity1.getId())
                    && Objects.equals(actualEntity.getDisplay(), expectedProviderEntity1.getDisplay())
                    && Objects.equals(actualEntity.getIdentifier(), expectedProviderEntity1.getIdentifier())
                    && Objects.equals(actualEntity.getRetired(), expectedProviderEntity1.getRetired());
            });
    }

    @Test
    public void updateProvider_ShouldCorrectlyUpdatedProvider() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        expectedProviderEntity1.setDisplay("name_123_123");

        mDatabase.providerRoomDAO().updateProvider(expectedProviderEntity1);

        mDatabase.providerRoomDAO().findProviderByUUID(expectedProviderEntity1.getUuid())
            .test()
            .assertValue(provider -> {
                Provider actualEntity = provider;
                return Objects.equals(actualEntity.getDisplay(), expectedProviderEntity1.getDisplay());
            });
    }

    @Test
    public void updateProviderByUuid_ShouldCorrectlyUpdatedProvider() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        expectedProviderEntity1.setDisplay("name_123_123");

        mDatabase.providerRoomDAO()
            .updateProviderByUuid(expectedProviderEntity1.getDisplay(), expectedProviderEntity1.getId(), expectedProviderEntity1.getPerson(), expectedProviderEntity1.getUuid(),
                expectedProviderEntity1.getIdentifier());

        mDatabase.providerRoomDAO().findProviderByUUID(expectedProviderEntity1.getUuid())
            .test()
            .assertValue(provider -> {
                Provider actualEntity = provider;
                return Objects.equals(actualEntity.getDisplay(), expectedProviderEntity1.getDisplay());
            });
    }

    @Test
    public void updateProviderUuidById_ShouldCorrectlyUpdatedProviderUuid() {
        final long PRIMARY_KEY_GENERATED = mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        final String UUID_PASSED_AS_PARAMETER = "3201dc4b-e512-4a1e-a0e1-2b50ac84e5ef";

        mDatabase.providerRoomDAO().updateProviderUuidById(PRIMARY_KEY_GENERATED, UUID_PASSED_AS_PARAMETER);

        mDatabase.providerRoomDAO().findProviderByUUID(UUID_PASSED_AS_PARAMETER)
                .test()
                .assertValue(provider -> {
                    Provider actualEntity = provider;
                    return Objects.equals(actualEntity.getUuid(), UUID_PASSED_AS_PARAMETER);
                });
    }

    @Test
    public void deleteProvider_ShouldDeleteProvider() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity2);
        mDatabase.providerRoomDAO().deleteProvider(expectedProviderEntity1);

        mDatabase.providerRoomDAO().getProviderList()
            .test()
            .assertValue(providers -> {
                Provider actualEntity = providers.get(0);
                return providers.size() == 1
                    && Objects.equals(actualEntity.getId(), expectedProviderEntity2.getId());
            });
    }

    @Test
    public void deleteProviderByUuid_ShouldDeleteProvider() {
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity1);
        mDatabase.providerRoomDAO().addProvider(expectedProviderEntity2);
        mDatabase.providerRoomDAO().deleteByUuid(expectedProviderEntity1.getUuid());

        mDatabase.providerRoomDAO().getProviderList()
            .test()
            .assertValue(providers -> {
                Provider actualEntity = providers.get(0);
                return providers.size() == 1
                    && Objects.equals(actualEntity.getId(), expectedProviderEntity2.getId());
            });
    }

    private Provider newProvider(long id, String uuid, String display, String identifier, boolean retired) {
        Provider provider = new Provider();

        provider.setId(id);
        provider.setUuid(uuid);
        provider.setDisplay(display);
        provider.setIdentifier(identifier);
        provider.setRetired(retired);

        return provider;
    }
}
