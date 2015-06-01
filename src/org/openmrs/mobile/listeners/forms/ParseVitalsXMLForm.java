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
package org.openmrs.mobile.listeners.forms;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ParseVitalsXMLForm {

    private static final String VALUE_FIELD = "value";
    private static final String ENCOUNTER_DATETIME_FIELD = "encounter.encounter_datetime";
    private static final String OPENMRS_CONCEPT_FIELD = "openmrs_concept";
    private static final String OBS_FIELD = "obs";
    private static final String[] VITALS_FIELDS =
            {"blood_oxygen_saturation", "diastolic_blood_pressure", "systolic_blood_pressure",
                "respiratory_rate", "pulse", "temperature_c", "weight_kg", "height_cm"};
    private static final String VITALS = "Vitals";

    private Encounter encounter;
    private String text;
    private Observation obs;
    private List<Observation> observationList;

    protected OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected OpenMRSLogger mLogger = mOpenMRS.getOpenMRSLogger();

    public ParseVitalsXMLForm() {

    }

    public Encounter parseVitalsForm(String xmlPath) {
        encounter = new Encounter();
        encounter.setDisplay(VITALS);
        encounter.setEncounterType(Encounter.EncounterType.VITALS);

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            InputStream stream = new FileInputStream(new File(xmlPath));
            parser.setInput(stream, null);

            text = ApplicationConstants.EMPTY_STRING;
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                parseField(parser, event);
                event = parser.next();
            }

        } catch (FileNotFoundException e) {
            mLogger.d("File not found exception", e);
        } catch (XmlPullParserException e) {
            mLogger.d("XmlPullParserException", e);
        } catch (IOException e) {
            mLogger.d("IOException", e);
        }

        return encounter;
    }

    private void parseField(XmlPullParser parser, int event) {
        String name = parser.getName();
        switch (event) {
            case XmlPullParser.START_TAG:
                startTAG(name, parser);
                break;
            case XmlPullParser.TEXT:
                text = parser.getText();
                break;
            case XmlPullParser.END_TAG:
                endTAG(name);
                break;
            default:
                break;
        }
    }

    private void startTAG(String name, XmlPullParser parser) {
        if (name.equals(OBS_FIELD)) {
            observationList = new ArrayList<Observation>();
            text = ApplicationConstants.EMPTY_STRING;
        } else {
            for (String vital : VITALS_FIELDS) {
                if (name.equals(vital)) {
                    obs = new Observation();
                    String display = parser.getAttributeValue(null, OPENMRS_CONCEPT_FIELD);
                    display = display.substring(display.indexOf('^') + 1, display.lastIndexOf('^'));
                    obs.setDisplay(display);
                    text = ApplicationConstants.EMPTY_STRING;
                }
            }
        }
    }

    private void endTAG(String name) {
        if (name.equals(ENCOUNTER_DATETIME_FIELD)) {
            encounter.setEncounterDatetime(DateUtils.convertTime(text));
        } else if (name.equals(OBS_FIELD)) {
            encounter.setObservations(observationList);
        } else if (name.equals(VALUE_FIELD)) {
            obs.setDisplayValue(text);
        } else {
            for (String vital : VITALS_FIELDS) {
                if (name.equals(vital) && !obs.getDisplayValue().equals(ApplicationConstants.EMPTY_STRING)) {
                    observationList.add(obs);
                }
            }
        }
    }

}
