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

package org.openmrs.client.models.mappers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Location;
import org.openmrs.client.utilities.ApplicationConstants;

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
            location.setParentLocationUuid(findVisitLocationUUID(json));
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse Location json : " + e.toString());
        }
        return location;
    }

    private static String findVisitLocationUUID(JSONObject json) throws JSONException {
        String visitLocationUUID = ApplicationConstants.EMPTY_STRING;

        JSONArray tags = json.getJSONArray("tags");
        for (int i = 0; i < tags.length(); i++) {
            JSONObject tag = tags.getJSONObject(i);
            String tagName = tag.getString("display");
            if ("Visit Location".equals(tagName)) {
                return json.getString("uuid");
            }
        }
        if (!json.isNull("parentLocation")) {
            visitLocationUUID = findVisitLocationUUID(json.getJSONObject("parentLocation"));
        }
        return visitLocationUUID;
    }
}
