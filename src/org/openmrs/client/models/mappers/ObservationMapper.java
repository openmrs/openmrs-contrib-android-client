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
import org.openmrs.client.models.Observation;

public final class ObservationMapper {

    private static final String DISPLAY_KEY = "display";
    private static final String VALUE_KEY = "value";

    private ObservationMapper() {
    }

    public static Observation diagnosisMap(JSONObject observationJSONObject) throws JSONException {
        Observation observation = new Observation();
        observation.setUuid(observationJSONObject.getString("uuid"));
        observation.setDisplay(observationJSONObject.getString(DISPLAY_KEY));

        if ("Visit Diagnoses".equals(observationJSONObject.getJSONObject("concept").getString(DISPLAY_KEY))) {
            JSONArray diagnosisDetailJSONArray = observationJSONObject.getJSONArray("groupMembers");
            for (int i = 0; i < diagnosisDetailJSONArray.length(); i++) {
                JSONObject diagnosisDetails = diagnosisDetailJSONArray.getJSONObject(i);

                String diagnosisDetail = diagnosisDetails.getJSONObject("concept").getString(DISPLAY_KEY);

                if ("Diagnosis order".equals(diagnosisDetail)) {
                    observation.setDiagnosisOrder(Observation.DiagnosisOrder.getOrder(
                            diagnosisDetails.getJSONObject(VALUE_KEY).getString(DISPLAY_KEY)));
                } else if ("Diagnosis certainty".equals(diagnosisDetail)) {
                    observation.setDiagnosisCertainty(Observation.DiagnosisCertainty.getCertainty(
                            diagnosisDetails.getJSONObject(VALUE_KEY).getString(DISPLAY_KEY)));
                } else {
                    observation.setDiagnosisList(diagnosisDetails.getJSONObject(VALUE_KEY).getString(DISPLAY_KEY));
                }
            }
        } else if ("Text of encounter note".equals(observationJSONObject.getJSONObject("concept").getString(DISPLAY_KEY))) {
            observation.setDiagnosisNote(observationJSONObject.getString(VALUE_KEY));
        }
        return observation;
    }

    public static Observation vitalsMap(JSONObject observationJSONObject) throws JSONException {
        Observation observation = new Observation();
        observation.setUuid(observationJSONObject.getString("uuid"));
        String[] labelAndValue = observationJSONObject.getString(DISPLAY_KEY).split(":");
        observation.setDisplay(labelAndValue[0]);
        observation.setDisplayValue(labelAndValue[1]);

        return observation;
    }
}
