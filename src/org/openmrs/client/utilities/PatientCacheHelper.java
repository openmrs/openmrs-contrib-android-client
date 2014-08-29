package org.openmrs.client.utilities;

import org.openmrs.client.models.Patient;

import java.util.ArrayList;
import java.util.List;

public final class PatientCacheHelper {
    private static List<Patient> sCachedPatients = new ArrayList<Patient>();

    private PatientCacheHelper() {

    }

    public static List<Patient> getCachedPatients() {
        return sCachedPatients;
    }

    public static void addPatient(Patient patient) {
        sCachedPatients.add(patient);
    }

    public static void clearCache() {
        sCachedPatients.clear();
    }
}
