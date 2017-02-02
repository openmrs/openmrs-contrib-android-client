package org.openmrs.mobile.activities.patientdashboard;

import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Patient;

import rx.schedulers.Schedulers;

public abstract class PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientDashboardMainPresenter {

    protected Patient mPatient;

    @Override
    public void deletePatient() {
        new PatientDAO().deletePatient(mPatient.getId());
        new VisitDAO().deleteVisitsByPatientId(mPatient.getId())
                .observeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public long getPatientId() {
        return mPatient.getId();
    }

    @Override
    public abstract void start();
}
