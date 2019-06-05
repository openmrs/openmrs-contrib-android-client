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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.mobile.models.IdentifierType;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ResourceSerializer;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ResourceSerializerTest {

    @Mock
    private JsonSerializationContext context;

    @Test
    public void shouldNotThrowNPEWhenCollectionIsNull(){
        when(context.serialize(any())).thenReturn(getJsonObject());
        Patient patient = generatePatient(false);
        patient.setIdentifiers(null);
        JsonElement serialize = new ResourceSerializer().serialize(patient, Patient.class, context);
        assertThat(serialize.toString(), not(containsString("\"identifiers\":")));
    }

    @Test
    public void shouldNotSerializeFieldWithoutExposeAnnotation(){
        when(context.serialize(any())).thenReturn(getJsonObject());
        Patient patient = generatePatient(false);
        patient.setId(10000L);
        JsonElement serialize = new ResourceSerializer().serialize(patient, Patient.class, context);
        assertThat(serialize.toString(), not(containsString("\"id\":")));
    }

    private JsonElement getJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fullObject", "true");
        return jsonObject;
    }

    private Patient generatePatient(boolean withPersonUuid) {
        Patient patient = new Patient();

        if (withPersonUuid) {
            patient.setUuid("PersonUUID");
        }
        patient.setIdentifiers(Arrays.asList(generateIdentifier()));
        updatePatientDetails(patient);
        return patient;
    }

    private Patient updatePatientDetails(Patient patient) {
        patient.setBirthdate(DateUtils.convertTime(System.currentTimeMillis()));
        PersonName  personName = new PersonName();
        personName.setFamilyName("family");
        personName.setGivenName("given");
        personName.setMiddleName("middle");
        patient.setNames(Arrays.asList(personName));
        patient.setGender("M");
        return patient;
    }

    private PatientIdentifier generateIdentifier() {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        IdentifierType identifierType = new IdentifierType();
        identifierType.setUuid("identifierTypeUUID");
        patientIdentifier.setIdentifierType(identifierType);
        Location location = new Location();
        location.setUuid("locationUUID");
        patientIdentifier.setLocation(location);
        patientIdentifier.setUuid("patientIdentifierUUID");
        return patientIdentifier;
    }

}
