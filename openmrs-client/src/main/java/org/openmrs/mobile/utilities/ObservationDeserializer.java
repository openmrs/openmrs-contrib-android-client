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

package org.openmrs.mobile.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Observation;

import java.lang.reflect.Type;

public class ObservationDeserializer implements JsonDeserializer<Observation> {

    private static final String UUID_KEY = "uuid";
    private static final String DISPLAY_KEY = "display";
    private static final String VALUE_KEY = "value";

    @Override
    public Observation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        Observation observation = new Observation();
        observation.setUuid(jsonObject.get(UUID_KEY).getAsString());
        observation.setDisplay(jsonObject.get(DISPLAY_KEY).getAsString());

        JsonElement conceptJson = jsonObject.get("concept");
        if (conceptJson != null && "Visit Diagnoses".equals(conceptJson.getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
            JsonArray diagnosisDetailJSONArray = jsonObject.get("groupMembers").getAsJsonArray();
            for (int i = 0; i < diagnosisDetailJSONArray.size(); i++) {

                JsonObject diagnosisDetails = diagnosisDetailJSONArray.get(i).getAsJsonObject();
                String diagnosisDetail = diagnosisDetails.get("concept").getAsJsonObject().get(DISPLAY_KEY).getAsString();

                if ("Diagnosis order".equals(diagnosisDetail)) {
                    observation.setDiagnosisOrder(
                            diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                } else if ("Diagnosis certainty".equals(diagnosisDetail)) {
                    observation.setDiagnosisCertanity(
                            diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                } else {
                    try {
                        observation.setDiagnosisList(diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                    }
                    catch (IllegalStateException e) {
                        observation.setDiagnosisList(diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsString());
                    }
                }
            }
        } else if (conceptJson != null && "Text of encounter note".equals(conceptJson.getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
            observation.setDiagnosisNote(jsonObject.getAsJsonObject().get(VALUE_KEY).getAsString());
        }
        if (conceptJson != null) {
            Concept concept = new Concept();
            concept.setUuid(conceptJson.getAsJsonObject().get(UUID_KEY).getAsString());
            observation.setConcept(concept);
        }
        return observation;
    }

}
