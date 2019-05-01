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

package org.openmrs.mobile.test;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ObservationDeserializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ObservationDeserializerTest {

    private static final String DIAGNOSIS_LIST = "Chronic intractable pain";
    private static final String DIAGNOSIS_CERTAINTY = "Presumed diagnosis";
    private static final String DIAGNOSIS_ORDER = "Primary";

    @Mock
    private JsonDeserializationContext context;

    private JsonObject jsonObject;

    @Before
    public void setUp() throws Exception {
        File jsonResponseFile = new File("src/test/java/org/openmrs/mobile/test/retrofitMocks/", "obsWithDiagnosisDataExampleResponse.json");
        String response = getMockResponseFromFile(jsonResponseFile);
        JsonParser jsonParser = new JsonParser();
        jsonObject = jsonParser.parse(response).getAsJsonObject();
    }

    @Test
    public void shouldDeserializeObservationWithDiagnosisData() {
        Observation observation = new ObservationDeserializer().deserialize(jsonObject, Observation.class, context);
        assertThat(observation.getDiagnosisList(), is(equalTo(DIAGNOSIS_LIST)));
        assertThat(observation.getDiagnosisCertainty(), is(equalTo(DIAGNOSIS_CERTAINTY)));
        assertThat(observation.getDiagnosisOrder(), is(equalTo(DIAGNOSIS_ORDER)));
    }

    private String getMockResponseFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

}
