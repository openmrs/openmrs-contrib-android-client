package org.openmrs.client.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.client.models.Visit;
import org.openmrs.client.utilities.DateUtils;

public final class VisitMapper {

    private VisitMapper() {
    }

    public static Visit map(JSONObject jsonObject) throws JSONException {
        Visit visit = new Visit();
        visit.setUuid(jsonObject.getString("uuid"));
        visit.setDisplay(jsonObject.getString("display"));
        visit.setStartDate(DateUtils.convertTime(jsonObject.getString("startDatetime")));
        visit.setStopDate(DateUtils.convertTime(jsonObject.getString("stopDatetime")));
        return visit;
    }
}
