package org.openmrs.client.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Patient;

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
            patient.setBirthDate(personJSON.getString("birthdate"));
            patient.setDeathDate(personJSON.getString("deathDate"));
            patient.setCauseOfDeath(personJSON.getString("causeOfDeath"));
            patient.setAge(personJSON.getString("age"));
            JSONObject namesJSON = personJSON.getJSONArray("names").getJSONObject(0);
            patient.setGivenName(namesJSON.getString("givenName"));
            patient.setDisplay(namesJSON.getString("display"));
            patient.setMiddleName(namesJSON.getString("middleName"));
            patient.setFamilyName(namesJSON.getString("familyName"));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Patient json : " + e.toString());
        }
        return patient;
    }
}
