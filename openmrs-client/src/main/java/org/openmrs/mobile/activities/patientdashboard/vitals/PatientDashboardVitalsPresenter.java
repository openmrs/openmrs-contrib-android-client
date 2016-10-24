package org.openmrs.mobile.activities.patientdashboard.vitals;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.Patient;

public class PatientDashboardVitalsPresenter implements PatientDashboardContract.PatientVitalsPresenter {

    private PatientDashboardContract.ViewPatientVitals mPatientVitalsView;

    private Patient mPatient;

    public PatientDashboardVitalsPresenter(Patient patient, PatientDashboardContract.ViewPatientVitals mPatientVitalsView) {
        this.mPatient = patient;
        this.mPatientVitalsView = mPatientVitalsView;
        this.mPatientVitalsView.setPresenter(this);
    }

    @Override
    public void start() {
        Encounter mVitalsEncounter = new EncounterDAO().getLastVitalsEncounter(mPatient.getUuid());
        if (mVitalsEncounter != null) {
            mPatientVitalsView.showEncounterVitals(mVitalsEncounter);
        }
        else {
            mPatientVitalsView.showNoVitalsNotification();
        }
    }
}
