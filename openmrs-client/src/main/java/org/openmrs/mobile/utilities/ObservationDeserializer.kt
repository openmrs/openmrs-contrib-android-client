package org.openmrs.mobile.utilities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.openmrs.mobile.models.Concept
import org.openmrs.mobile.models.Observation
import java.lang.reflect.Type


class ObservationDeserializer : JsonDeserializer<Observation> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Observation {
        val jsonObject = json.asJsonObject
        val observation = Observation()
        observation.uuid = jsonObject[UUID_KEY].asString
        observation.display = jsonObject[DISPLAY_KEY].asString
        val conceptJson = jsonObject["concept"]
        if (conceptJson != null && "Visit Diagnoses" == conceptJson.asJsonObject[DISPLAY_KEY].asString) {
            val diagnosisDetailJSONArray = jsonObject["groupMembers"].asJsonArray
            for (i in 0 until diagnosisDetailJSONArray.size()) {
                val diagnosisDetails = diagnosisDetailJSONArray[i].asJsonObject
                val diagnosisDetail = diagnosisDetails["concept"].asJsonObject[DISPLAY_KEY].asString
                if ("Diagnosis order" == diagnosisDetail) {
                    observation.diagnosisOrder = diagnosisDetails.asJsonObject[VALUE_KEY].asJsonObject[DISPLAY_KEY].asString
                } else if ("Diagnosis certainty" == diagnosisDetail) {
                    observation.setDiagnosisCertanity(
                            diagnosisDetails.asJsonObject[VALUE_KEY].asJsonObject[DISPLAY_KEY].asString)
                } else {
                    try {
                        observation.diagnosisList = diagnosisDetails.asJsonObject[VALUE_KEY].asJsonObject[DISPLAY_KEY].asString
                    } catch (e: IllegalStateException) {
                        observation.diagnosisList = diagnosisDetails.asJsonObject[VALUE_KEY].asString
                    }
                }
            }
        } else if (conceptJson != null && "Text of encounter note" == conceptJson.asJsonObject[DISPLAY_KEY].asString) {
            observation.diagnosisNote = jsonObject.asJsonObject[VALUE_KEY].asString
        }
        if (conceptJson != null) {
            val concept = Concept()
            concept.uuid = conceptJson.asJsonObject[UUID_KEY].asString
            observation.concept = concept
        }
        return observation
    }

    companion object {
        private const val UUID_KEY = "uuid"
        private const val DISPLAY_KEY = "display"
        private const val VALUE_KEY = "value"
    }
}
