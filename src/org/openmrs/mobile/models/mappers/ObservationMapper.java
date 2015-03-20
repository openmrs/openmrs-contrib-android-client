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
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.ArrayList;
import java.util.List;

public final class ObservationMapper {

    private static final String DISPLAY_KEY = "display";
    private static final String VALUE_KEY = "value";
    private static final String UUID_KEY = "uuid";

    private ObservationMapper() {
    }

    public static Observation diagnosisMap(JSONObject observationJSONObject) throws JSONException {
        Observation observation = new Observation();
        observation.setUuid(observationJSONObject.getString(UUID_KEY));
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

    public static Encounter lastVitalsMap(JSONObject jsonObject) {
        Encounter encounter = null;
        try {
            encounter = parseLastVitalsAfterFormSending(jsonObject);
        } catch (JSONException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse LastVitals encounter. Trying to parse different JSON model.");
            try {
                encounter = parseOtherLastVitalsEncounterResponse(jsonObject);
            } catch (JSONException je) {
                OpenMRS.getInstance().getOpenMRSLogger().d(je.toString());
                OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse LastVitals encounter different JSON model.");
            } catch (NullPointerException ne) {
                OpenMRS.getInstance().getOpenMRSLogger().d(ne.toString());
                OpenMRS.getInstance().getOpenMRSLogger().d("Failed to parse LastVitals encounter! Response returned empty encounter type!");
                return null;
            }
        }
        return encounter;
    }

    private static Encounter parseLastVitalsAfterFormSending(JSONObject jsonObject) throws JSONException {
        Encounter encounter = new Encounter();
        List<Observation> observationList = new ArrayList<Observation>();
        JSONObject obsObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("obs").getJSONObject(0);
        JSONArray groupMembers = obsObject.getJSONArray("groupMembers");
        JSONObject encounterJSON = obsObject.getJSONObject("encounter");
        for (int i = 0; i < groupMembers.length(); i++) {
            JSONObject object = groupMembers.getJSONObject(i);
            Observation observation = new Observation();
            observation.setDisplay(object.getString(DISPLAY_KEY));
            observation.setDisplayValue(object.getString(VALUE_KEY));
            observation.setUuid(object.getString(UUID_KEY));
            observationList.add(observation);
        }
        encounter.setUuid(encounterJSON.getString(UUID_KEY));
        encounter.setEncounterDatetime(DateUtils.convertTime(encounterJSON.getString("encounterDatetime")));
        encounter.setEncounterType(Encounter.EncounterType.VITALS);
        encounter.setDisplay(encounterJSON.getString(DISPLAY_KEY));
        encounter.setObservations(observationList);
        return encounter;
    }

    private static Encounter parseOtherLastVitalsEncounterResponse(JSONObject jsonObject) throws JSONException {
        Encounter encounter = new Encounter();
        List<Observation> observationList = new ArrayList<Observation>();
        JSONArray obsArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("obs");
        for (int i = 0; i < obsArray.length(); i++) {
            JSONObject obsObject = obsArray.getJSONObject(i);
            Observation observation = new Observation();
            observation.setDisplay(obsObject.getString(DISPLAY_KEY));
            observation.setDisplayValue(obsObject.getString(VALUE_KEY));
            observation.setUuid(obsObject.getString(UUID_KEY));
            observationList.add(observation);
            if (obsArray.length() - 1 == i) {
                JSONObject encounterJSON = obsObject.getJSONObject("encounter");
                encounter.setUuid(encounterJSON.getString(UUID_KEY));
                encounter.setEncounterDatetime(DateUtils.convertTime(encounterJSON.getString("encounterDatetime")));
                encounter.setEncounterType(Encounter.EncounterType.VITALS);
                encounter.setDisplay(encounterJSON.getString(DISPLAY_KEY));
                encounter.setObservations(observationList);
            }
        }
        // it means that form is empty
        if (null == encounter.getEncounterType()) {
            throw new NullPointerException();
        }
        return encounter;
    }

    public static Observation vitalsMap(JSONObject observationJSONObject) throws JSONException {
        Observation observation = new Observation();
        observation.setUuid(observationJSONObject.getString(UUID_KEY));
        String[] labelAndValue = observationJSONObject.getString(DISPLAY_KEY).split(":");
        observation.setDisplay(labelAndValue[0]);
        observation.setDisplayValue(labelAndValue[1]);

        return observation;
    }
}
