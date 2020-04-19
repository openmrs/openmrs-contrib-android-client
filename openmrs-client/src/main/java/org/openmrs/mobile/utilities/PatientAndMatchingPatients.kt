package org.openmrs.mobile.utilities

import org.openmrs.mobile.models.Patient
import java.io.Serializable


class PatientAndMatchingPatients(val patient: Patient, val matchingPatientList: List<Patient>) : Serializable