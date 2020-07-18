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

package org.openmrs.mobile.databases;

import org.openmrs.mobile.databases.entities.ConceptEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.models.Observation;

import java.util.ArrayList;
import java.util.List;

public class AppDatabaseHelper {
    public AppDatabaseHelper() {
    }

    public ObservationEntity observationToEntity(Observation obs, long encounterID) {
        ObservationEntity observationEntity = new ObservationEntity();
        observationEntity.setId(obs.getId());
        observationEntity.setUuid(obs.getUuid());
        observationEntity.setDisplay(obs.getDisplay());
        observationEntity.setEncounterKeyID(encounterID);
        observationEntity.setDisplayValue(obs.getDisplayValue());
        observationEntity.setDiagnosisOrder(obs.getDiagnosisOrder());
        observationEntity.setDiagnosisList(obs.getDiagnosisList());
        observationEntity.setDiagnosisCertainty(obs.getDiagnosisCertainty());
        observationEntity.setDiagnosisNote(obs.getDiagnosisNote());
        if (obs.getConcept() != null) {
            observationEntity.setConceptuuid(obs.getConcept().getUuid());
        } else {
            observationEntity.setConceptuuid(null);
        }
        return observationEntity;
    }

    public List<Observation> observationEntityToObservation(List<ObservationEntity> observationEntityList) {
        List<Observation> observationList = new ArrayList<>();
        for (ObservationEntity entity : observationEntityList) {
            Observation obs = new Observation();
            obs.setId(entity.getId());
            obs.setEncounterID(entity.getEncounterKeyID());
            obs.setUuid(entity.getUuid());
            obs.setDisplay(entity.getDisplay());
            obs.setDisplayValue(entity.getDisplayValue());
            obs.setDiagnosisOrder(entity.getDiagnosisOrder());
            obs.setDiagnosisList(entity.getDiagnosisList());
            obs.setDiagnosisCertanity(entity.getDiagnosisCertainty());
            obs.setDiagnosisNote(entity.getDiagnosisNote());
            ConceptEntity concept = new ConceptEntity();
            concept.setUuid(entity.getConceptuuid());
            obs.setConcept(concept);
            observationList.add(obs);
        }
        return observationList;
    }
}
