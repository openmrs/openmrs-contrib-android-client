package org.openmrs.client.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.application.OpenMRSLogger;

public final class Patient extends Person {
    private OpenMRSLogger logger = OpenMRS.getInstance().getOpenMRSLogger();

    private long id;
    private String identifier;
    private String display;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void patientMapper(JSONObject patientJSON) {
        try {
            personMapper(patientJSON.getJSONObject("person"));
            JSONObject identifierJSON = patientJSON.getJSONArray("identifiers").getJSONObject(0);
            setIdentifier(identifierJSON.getString("identifier"));
        } catch (JSONException e) {
            logger.d(e.toString());
        }
    }
}
