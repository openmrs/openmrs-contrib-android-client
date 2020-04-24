package org.openmrs.mobile.utilities

import java.io.Serializable
import java.util.*


class PatientAndMatchesWrapper : Serializable {
    var matchingPatients: Queue<PatientAndMatchingPatients>

    constructor() {
        matchingPatients = ArrayDeque()
    }

    constructor(matchingPatients: Queue<PatientAndMatchingPatients>) {
        this.matchingPatients = matchingPatients
    }

    fun addToList(element: PatientAndMatchingPatients) {
        matchingPatients.add(element)
    }

    fun remove(element: PatientAndMatchingPatients?) {
        matchingPatients.remove(element)
    }

}
