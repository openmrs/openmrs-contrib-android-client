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
            patient.setBirthDate(personJSON.getString("birthDate"));
            patient.setDeathDate(personJSON.getString("deathDate"));
            patient.setCauseOfDeath(personJSON.getString("causeOfDeath"));
            JSONObject preferredNameJSON = personJSON.getJSONObject("preferredName");
            patient.setGivenName(preferredNameJSON.getString("givenName"));
            patient.setMiddleName(preferredNameJSON.getString("middleName"));
            patient.setFamilyName(preferredNameJSON.getString("familyName"));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Patient json : " + e.toString());
        }
        return patient;
    }
}
