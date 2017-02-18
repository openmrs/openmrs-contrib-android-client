/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

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
