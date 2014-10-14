package org.openmrs.client.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Location;

public final class LocationMapper {

    private LocationMapper() {
    }

    public static Location map(JSONObject json) {
        Location location = new Location();
        try {
            location.setUuid(json.getString("uuid"));
            location.setDisplay(json.getString("display"));
            location.setName(json.getString("name"));
            location.setDescription(json.getString("description"));
            location.setAddress(AddressMapper.parseAddress(json));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Location json : " + e.toString());
        }
        return location;
    }
}
