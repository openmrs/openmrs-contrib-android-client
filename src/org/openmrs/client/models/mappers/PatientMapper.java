package org.openmrs.client.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Address;
import org.openmrs.client.models.Patient;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.StringUtils;

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
            patient.setAddress(parseAddress(personJSON.getJSONObject("preferredAddress")));
            patient.setPhoneNumber(personJSON.getJSONArray("attributes").getJSONObject(0).getString("value"));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Patient json : " + e.toString());
        }
        return patient;
    }

    private static Address parseAddress(JSONObject addressJSON) throws JSONException {
        Address address = new Address();
        address.setAddress1(addressJSON.getString("address1"));
        address.setAddress2(addressJSON.getString("address2"));
        address.setCityVillage(addressJSON.getString("cityVillage"));
        address.setCountry(addressJSON.getString("country"));
        address.setPostalCode(addressJSON.getString("postalCode"));
        address.setState(addressJSON.getString("stateProvince"));
        return address;
    }
}
