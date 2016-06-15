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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.ArrayList;
import java.util.List;

public final class VisitMapper {

    private static final String DISPLAY_KEY = "display";

    private VisitMapper() {
    }

    public static Visit map(JSONObject jsonObject) throws JSONException {
        Visit visit = new Visit();

        visit.setUuid(getStringFromObject(jsonObject,"uuid"));
        JSONObject locationObject = getJSONObject(jsonObject,"location");
        visit.setVisitPlace(getStringFromObject(locationObject,DISPLAY_KEY));
        JSONObject visitTypeObject = jsonObject.getJSONObject("visitType");
        visit.setVisitType(getStringFromObject(visitTypeObject,DISPLAY_KEY));
        visit.setStartDate(DateUtils.convertTime(getStringFromObject(jsonObject,"startDatetime")));
        visit.setStopDate(DateUtils.convertTime(getStringFromObject(jsonObject,"stopDatetime")));
        List<Encounter> encounterList = new ArrayList<Encounter>();

        if (!jsonObject.isNull("encounters")) {
            JSONArray encountersJSONArray = jsonObject.getJSONArray("encounters");
            for (int i = 0; i < encountersJSONArray.length(); i++) {
                Encounter encounter = new Encounter();
                JSONObject encounterJSONObject = encountersJSONArray.getJSONObject(i);
                List<Observation> observationList = new ArrayList<Observation>();
                encounter.setDisplay(getStringFromObject(encounterJSONObject,DISPLAY_KEY));
                encounter.setUuid(getStringFromObject(encounterJSONObject,"uuid"));
                encounter.setEncounterType(Encounter.EncounterType.getType(getStringFromObject(getJSONObject(encounterJSONObject,"encounterType"),DISPLAY_KEY)));
                encounter.setEncounterDatetime(getStringFromObject(encounterJSONObject,"encounterDatetime"));
                JSONArray obsJSONArray = encounterJSONObject.getJSONArray("obs");
                for (int j = 0; j < obsJSONArray.length(); j++) {
                    Observation observation = new Observation();
                    JSONObject observationJSONObject = obsJSONArray.getJSONObject(j);
                    observation.setUuid(getStringFromObject(observationJSONObject,"uuid"));
                    if (Encounter.EncounterType.VITALS.equals(encounter.getEncounterType())) {
                        String[] labelAndValue = getStringFromObject(observationJSONObject,DISPLAY_KEY).split(":");
                        observation.setDisplay(labelAndValue[0]);
                        observation.setDisplayValue(labelAndValue[1]);
                    } else if (Encounter.EncounterType.VISIT_NOTE.equals(encounter.getEncounterType())) {
                        observation = ObservationMapper.diagnosisMap(observationJSONObject);
                    } else {
                        observation.setDisplay(getStringFromObject(observationJSONObject,DISPLAY_KEY));
                    }
                    observationList.add(observation);
                }
                encounter.setObservations(observationList);
                encounterList.add(encounter);
            }
        }
        visit.setEncounters(encounterList);
        return visit;
    }

    public static String getStringFromObject(JSONObject jsonObject,String name) throws JSONException {
        String tempName = "";
        if (jsonObject!=null) {
            if (jsonObject.has(name) && !jsonObject.isNull(name)) {
                tempName = jsonObject.getString(name);
            }
        }
        return tempName;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String name) throws JSONException {
        JSONObject tempObject = null;
        if (jsonObject.has(name) && !jsonObject.isNull(name)){
            tempObject = jsonObject.getJSONObject(name);
        }
        return tempObject;
    }
}