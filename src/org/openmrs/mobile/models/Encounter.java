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

package org.openmrs.mobile.models;

import java.io.Serializable;
import java.util.List;

public class Encounter implements Serializable {
    private Long id;
    private Long visitID;
    private String uuid;
    private String display;
    private Long encounterDatetime;
    private EncounterType encounterType;
    private List<Observation> observations;
    private Long patientID;

    public Long getId() {
        return id;
    }

    public Long getVisitID() {
        return visitID;
    }

    public void setVisitID(Long visitID) {
        this.visitID = visitID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Long getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(Long encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public Long getPatientID() {
        return patientID;
    }

    public void setPatientID(Long patientID) {
        this.patientID = patientID;
    }

    public enum EncounterType {
        VITALS("Vitals"), VISIT_NOTE("Visit Note"), DISCHARGE("Discharge"), ADMISSION("Admission");

        EncounterType(String type) {
            this.type = type;
        }

        private String type;

        public String getType() {
            return type;
        }

        public static EncounterType getType(String type) {
            if (type.equals(VISIT_NOTE.getType())) {
                return VISIT_NOTE;
            } else if (type.equals(DISCHARGE.getType())) {
                return DISCHARGE;
            } else if (type.equals(ADMISSION.getType())) {
                return ADMISSION;
            } else {
                return VITALS;
            }
        }
    }
}
