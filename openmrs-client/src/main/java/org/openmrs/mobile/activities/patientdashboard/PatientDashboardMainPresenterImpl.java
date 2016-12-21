package org.openmrs.mobile.activities.patientdashboard;

import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.retrofit.Patient;

public abstract class PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDashboardMainPresenter {

    protected Patient mPatient;

    @Override
    public void deletePatient() {
        new PatientDAO().deletePatient(mPatient.getId());
    }

    @Override
    public void start() {}
}
