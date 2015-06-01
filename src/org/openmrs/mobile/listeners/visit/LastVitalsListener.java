/**
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

package org.openmrs.mobile.listeners.visit;

import com.android.volley.Response;
import org.json.JSONObject;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.mappers.ObservationMapper;
import org.openmrs.mobile.net.GeneralErrorListener;


public final class LastVitalsListener extends GeneralErrorListener implements Response.Listener<JSONObject> {
    private final OpenMRSLogger mLogger = OpenMRS.getInstance().getOpenMRSLogger();
    private final Long mPatientID;

    public LastVitalsListener(Long patientID) {
        mPatientID = patientID;
    }

    @Override
    public void onResponse(JSONObject response) {
        mLogger.d(response.toString());
        new EncounterDAO().saveLastVitalsEncounter(ObservationMapper.lastVitalsMap(response), mPatientID);
    }

    public Long getPatientID() {
        return mPatientID;
    }
}
