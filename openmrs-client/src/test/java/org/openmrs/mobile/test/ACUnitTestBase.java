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

package org.openmrs.mobile.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.activeandroid.Cache;
import com.activeandroid.TableInfo;
import com.activeandroid.content.ContentProvider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Results;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({ Cache.class, TableInfo.class, Context.class,
        ContentResolver.class, ContentProvider.class, ContentValues.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.activeandroid.content.ContentProvider")
public abstract class ACUnitTestBase {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    protected void mockActiveAndroidContext() {
        PowerMockito.mockStatic(Cache.class);
        PowerMockito.mockStatic(ContentProvider.class);
        TableInfo tableInfo = PowerMockito.mock(TableInfo.class);
        Context context = PowerMockito.mock(Context.class);
        ContentResolver resolver = PowerMockito.mock(ContentResolver.class);
        SQLiteDatabase sqliteDb = PowerMockito.mock(SQLiteDatabase.class);
        ContentValues vals = PowerMockito.mock(ContentValues.class);

        try {
            PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(vals);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(Cache.openDatabase()).thenReturn(sqliteDb);
        when(context.getContentResolver()).thenReturn(resolver);
        doNothing().when(resolver).notifyChange(any(Uri.class), any(ContentObserver.class));
        when(tableInfo.getFields()).thenReturn(new ArrayList<>());
        when(tableInfo.getTableName()).thenReturn("TestTable");
        when(Cache.getTableInfo(any(Class.class))).thenReturn(tableInfo);
        when(Cache.getContext()).thenReturn(context);
        when(ContentProvider.createUri(anyObject(), anyLong())).thenReturn(null);
    }

    protected Patient createPatient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setUuid("patient_one_uuid"+id);
        patient.setPerson(createPerson(id));
        patient.setIdentifiers(Collections.singletonList(createIdentifier(id)));
        return patient;
    }

    protected PatientIdentifier createIdentifier(Long id) {
        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setIdentifier("some_identifier_" + id);
        return identifier;
    }

    protected Person createPerson(Long id) {
        Person person = new Person();
        person.setNames(Collections.singletonList(createPersonName(id)));
        person.setAddresses(Collections.singletonList(createPersonAddress(id)));
        person.setGender("M");
        person.setBirthdate("25-02-2016");
        return person;
    }

    protected PersonAddress createPersonAddress(Long id) {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("address_1_" + id);
        personAddress.setAddress2("address_2_" + id);
        personAddress.setCityVillage("city_" + id);
        personAddress.setStateProvince("state_" + id);
        personAddress.setCountry("country_" + id);
        personAddress.setPostalCode("postal_code_" + id);
        return personAddress;
    }

    protected PersonName createPersonName(Long id) {
        PersonName personName = new PersonName();
        personName.setGivenName("given_name_" + id);
        personName.setMiddleName("middle_name_" + id);
        personName.setFamilyName("family_name_" + id);
        return personName;
    }

    protected Patient createPatient(Long id, String identifier) {
        Patient patient = createPatient(id);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier(identifier);
        patient.setIdentifiers(Collections.singletonList(patientIdentifier));
        return patient;
    }

    protected <T> Call<Results<T>> mockSuccessCall(List<T> list) {
        return new MockSuccessResponse<>(list);
    }


    protected  <T> Call<T> mockSuccessCall(T object) {
        return new MockSuccessResponse<>(object);
    }

    protected <T> Call<T> mockErrorCall(int code){
        return new MockErrorResponse<>(code);
    }

    protected <T> Call<T> mockFailureCall() {
        Throwable throwable = Mockito.mock(Throwable.class);
        return new MockFailure<>(throwable);
    }
}
