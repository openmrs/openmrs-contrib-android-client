package org.openmrs.client.bundle;

import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;

import java.io.Serializable;
import java.util.List;

public class PatientDashboardBundle implements Serializable {

    private Patient mPatient;
    private List<Visit> mPatientVisits;

    public PatientDashboardBundle() {
    }

    public Patient getPatient() {
        return mPatient;
    }

    public void setPatient(Patient patient) {
        mPatient = patient;
    }

    public List<Visit> getPatientVisits() {
        return mPatientVisits;
    }

    public void setPatientVisits(List<Visit> patientVisits) {
        mPatientVisits = patientVisits;
    }
}
