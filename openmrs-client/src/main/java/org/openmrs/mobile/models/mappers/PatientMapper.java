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

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;

public final class PatientMapper {

    private PatientMapper() {
    }

    public static Patient map(JSONObject json) {
        Patient patient = new Patient();
        try {
            JSONObject personJSON = json.getJSONObject("person");
            patient.setIdentifier(json.getJSONArray("identifiers").getJSONObject(0).getString("identifier"));
            patient.setUuid(personJSON.getString("uuid"));
            patient.setGender(personJSON.getString("gender"));
            patient.setBirthDate(DateUtils.convertTime(personJSON.getString("birthdate")));
            patient.setDeathDate(DateUtils.convertTime(personJSON.getString("deathDate")));
            patient.setCauseOfDeath(personJSON.getString("causeOfDeath"));
            patient.setAge(personJSON.getString("age"));
            JSONObject namesJSON = personJSON.getJSONArray("names").getJSONObject(0);
            patient.setGivenName(namesJSON.getString("givenName"));
            patient.setDisplay(namesJSON.getString("display"));
            patient.setMiddleName(namesJSON.getString("middleName"));
            patient.setFamilyName(namesJSON.getString("familyName"));
            validateAddress(patient, personJSON);
            validatePhoneNumber(patient, personJSON);
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Patient json : " + e.toString());
        }
        return patient;
    }

    private static void validateAddress(Patient patient, JSONObject personJSON) {
        try {
            patient.setAddress(AddressMapper.parseAddress(personJSON.getJSONObject("preferredAddress")));
        } catch (JSONException e) {
            patient.setAddress(null);
        }
    }

    private static void validatePhoneNumber(Patient patient, JSONObject personJSON) {
        try {
            patient.setPhoneNumber(personJSON.getJSONArray("attributes").getJSONObject(0).getString("value"));
        } catch (JSONException e) {
            patient.setPhoneNumber(null);
        }
    }


}
