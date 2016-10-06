package org.openmrs.mobile.test;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.models.retrofit.Observation;
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
    JsonDeserializationContext context;

    private JsonObject jsonObject;

    @Before
    public void setup() throws Exception {
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
