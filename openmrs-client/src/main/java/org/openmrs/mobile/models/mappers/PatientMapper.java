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

package org.openmrs.mobile.models.mappers;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.retrofit.Patient;
import org.openmrs.mobile.retrofit.PatientIdentifier;
import org.openmrs.mobile.retrofit.Person;
import org.openmrs.mobile.retrofit.PersonName;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.Date;

public final class PatientMapper {

    private PatientMapper() {
    }

    public static Patient map(JSONObject json) {
        Patient patient = new Patient();
        Person person = new Person();
        patient.setPerson(person);
        try {
            JSONObject personJSON = json.getJSONObject("person");

            PatientIdentifier patientIdentifier = new PatientIdentifier();
            JSONObject firstIdentifier = json.getJSONArray("identifiers").getJSONObject(0);
            patientIdentifier.setIdentifier(firstIdentifier.getString("identifier"));
            patient.getIdentifiers().add(patientIdentifier);

            patient.setUuid(personJSON.getString("uuid"));
            patient.getPerson().setGender(personJSON.getString("gender"));

            patient.getPerson().setBirthdate(new Date(DateUtils.convertTime(personJSON.getString("birthdate"))));
            JSONObject namesJSON = personJSON.getJSONArray("names").getJSONObject(0);

            PersonName personName = new PersonName();
            personName.setGivenName(namesJSON.getString("givenName"));
            personName.setFamilyName(namesJSON.getString("familyName"));
            personName.setMiddleName(namesJSON.getString("middleName"));
            person.getNames().add(personName);

            person.getAddresses().add(AddressMapper.parseAddress(personJSON.getJSONObject("preferredAddress")));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Patient json : " + e.toString());
        }
        return patient;
    }

}
