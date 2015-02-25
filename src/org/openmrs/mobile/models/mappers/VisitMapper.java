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
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
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
        visit.setUuid(jsonObject.getString("uuid"));
        visit.setVisitPlace(jsonObject.getJSONObject("location").getString(DISPLAY_KEY));
        visit.setVisitType(jsonObject.getJSONObject("visitType").getString(DISPLAY_KEY));
        visit.setStartDate(DateUtils.convertTime(jsonObject.getString("startDatetime")));
        visit.setStopDate(DateUtils.convertTime(jsonObject.getString("stopDatetime")));
        List<Encounter> encounterList = new ArrayList<Encounter>();

        if (!jsonObject.isNull("encounters")) {
            JSONArray encountersJSONArray = jsonObject.getJSONArray("encounters");
            for (int i = 0; i < encountersJSONArray.length(); i++) {
                Encounter encounter = new Encounter();
                JSONObject encounterJSONObject = encountersJSONArray.getJSONObject(i);
                List<Observation> observationList = new ArrayList<Observation>();
                encounter.setDisplay(encounterJSONObject.getString(DISPLAY_KEY));
                encounter.setUuid(encounterJSONObject.getString("uuid"));
                encounter.setEncounterType(Encounter.EncounterType.getType(encounterJSONObject.getJSONObject("encounterType").getString(DISPLAY_KEY)));
                encounter.setEncounterDatetime(DateUtils.convertTime(encounterJSONObject.getString("encounterDatetime")));
                JSONArray obsJSONArray = encounterJSONObject.getJSONArray("obs");
                for (int j = 0; j < obsJSONArray.length(); j++) {
                    Observation observation = new Observation();
                    JSONObject observationJSONObject = obsJSONArray.getJSONObject(j);
                    observation.setUuid(observationJSONObject.getString("uuid"));
                    if (Encounter.EncounterType.VITALS.equals(encounter.getEncounterType())) {
                        String[] labelAndValue = observationJSONObject.getString(DISPLAY_KEY).split(":");
                        observation.setDisplay(labelAndValue[0]);
                        observation.setDisplayValue(labelAndValue[1]);
                    } else if (Encounter.EncounterType.VISIT_NOTE.equals(encounter.getEncounterType())) {
                        observation = ObservationMapper.diagnosisMap(observationJSONObject);
                    } else {
                        observation.setDisplay(observationJSONObject.getString(DISPLAY_KEY));
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
}
