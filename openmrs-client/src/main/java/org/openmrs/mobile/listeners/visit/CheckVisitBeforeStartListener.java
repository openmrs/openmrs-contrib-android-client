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

import org.json.JSONObject;
import org.openmrs.mobile.activities.PatientListActivity;
import org.openmrs.mobile.activities.PatientDashboardActivity;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.models.retrofit.Patient;

public class CheckVisitBeforeStartListener extends FindVisitsByPatientUUIDListener {
    private final PatientListActivity mCaller;
    private static final String CHECKING = "Checking: ";

    public CheckVisitBeforeStartListener(String patientUUID, long patientID, PatientListActivity callerAdapter) {
        super(patientUUID, patientID, callerAdapter);
        mCaller = callerAdapter;
    }

    public CheckVisitBeforeStartListener(String patientUUID, long patientID, PatientDashboardActivity callerAdapter) {
        super(patientUUID, patientID, callerAdapter);
        mCaller = null;
    }

    @Override
    public void onResponse(JSONObject response) {
        super.onResponse(response);
        mLogger.i(CHECKING + response.toString());
        if (mCaller != null) {
            if (new VisitDAO().isPatientNowOnVisit(mPatientID)) {
                mCaller.startCheckedFormEntryForResult();
            } else {
                // it will start new visit if confirmed
                mCaller.showNoVisitDialog();
            }
        } else {
            if (new VisitDAO().isPatientNowOnVisit(mPatientID)) {
                Patient patient = new PatientDAO().findPatientByID(String.valueOf(mPatientID));
                mCallerPDA.showStartVisitImpossibleDialog(patient.getDisplay());
            } else {
                new VisitsManager().startVisit(
                        VisitsHelper.createStartVisitListener(mPatientUUID, mPatientID, mCallerPDA));
            }
        }
    }
}
