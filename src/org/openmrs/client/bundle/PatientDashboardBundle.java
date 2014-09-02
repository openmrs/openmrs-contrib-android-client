package org.openmrs.client.bundle;

import org.openmrs.client.models.Patient;

import java.io.Serializable;

public class PatientDashboardBundle implements Serializable {

    private Patient mPatient;

    public PatientDashboardBundle() {
    }

    public Patient getPatient() {
        return mPatient;
    }

    public void setPatient(Patient patient) {
        mPatient = patient;
    }
}
