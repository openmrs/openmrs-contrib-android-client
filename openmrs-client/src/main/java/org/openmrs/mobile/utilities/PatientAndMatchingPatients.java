package org.openmrs.mobile.utilities;

import org.openmrs.mobile.models.Patient;

import java.io.Serializable;
import java.util.List;

public class PatientAndMatchingPatients implements Serializable {

    private Patient patient;
    private List<Patient> matchingPatientList;

    public PatientAndMatchingPatients(Patient patient, List<Patient> matchingPatientList) {
        this.patient = patient;
        this.matchingPatientList = matchingPatientList;
    }

    public Patient getPatient() {
        return patient;
    }

    public List<Patient> getMatchingPatientList() {
        return matchingPatientList;
    }
}
