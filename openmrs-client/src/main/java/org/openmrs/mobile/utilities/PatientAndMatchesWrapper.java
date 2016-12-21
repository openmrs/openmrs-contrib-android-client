package org.openmrs.mobile.utilities;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;

public class PatientAndMatchesWrapper implements Serializable{

    private Queue<PatientAndMatchingPatients> matchingPatients;

    public PatientAndMatchesWrapper() {
        matchingPatients = new ArrayDeque<>();
    }

    public PatientAndMatchesWrapper(Queue<PatientAndMatchingPatients> matchingPatients) {
        this.matchingPatients = matchingPatients;
    }

    public void addToList(PatientAndMatchingPatients element){
        matchingPatients.add(element);
    }

    public void remove(PatientAndMatchingPatients element){
        matchingPatients.remove(element);
    }

    public Queue<PatientAndMatchingPatients> getMatchingPatients() {
        return matchingPatients;
    }

    public void setMatchingPatients(Queue<PatientAndMatchingPatients> matchingPatients) {
        this.matchingPatients = matchingPatients;
    }
}
