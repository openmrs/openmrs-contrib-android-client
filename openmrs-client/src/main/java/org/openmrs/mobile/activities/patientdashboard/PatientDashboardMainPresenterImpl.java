package org.openmrs.mobile.activities.patientdashboard;

import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;

public abstract class PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDashboardMainPresenter {

    protected Patient mPatient;

    @Override
    public void deletePatient() {
        new PatientDAO().deletePatient(mPatient.getId());
    }

    @Override
    public long getPatientId() {
        return mPatient.getId();
    }

    @Override
    public void start() {}
}
