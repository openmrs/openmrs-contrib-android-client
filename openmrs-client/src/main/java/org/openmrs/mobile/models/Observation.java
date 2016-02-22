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

public class Observation implements Serializable {
    private Long id;
    private Long encounterID;
    private String uuid;
    private String display;
    private String displayValue;
    private DiagnosisOrder diagnosisOrder;
    private String diagnosisList;
    private DiagnosisCertainty diagnosisCertainty;
    private String diagnosisNote;

    public Long getId() {
        return id;
    }

    public Long getEncounterID() {
        return encounterID;
    }

    public void setEncounterID(Long encounterID) {
        this.encounterID = encounterID;
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

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public DiagnosisOrder getDiagnosisOrder() {
        return diagnosisOrder;
    }

    public void setDiagnosisOrder(DiagnosisOrder diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public String getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public DiagnosisCertainty getDiagnosisCertainty() {
        return diagnosisCertainty;
    }

    public void setDiagnosisCertainty(DiagnosisCertainty diagnosisCertainty) {
        this.diagnosisCertainty = diagnosisCertainty;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

    public enum DiagnosisCertainty {
        PRESUMED("Presumed diagnosis"), CONFIRMED("Confirmed diagnosis");

        DiagnosisCertainty(String certainty) {
            this.certainty = certainty;
        }

        private String certainty;

        public String getCertainty() {
            return certainty;
        }

        public String getShortCertainty() {
            return certainty.split(" ")[0];
        }

        public static DiagnosisCertainty getCertainty(String certainty) {
            if (certainty.equals(CONFIRMED.getCertainty())) {
                return CONFIRMED;
            } else if (certainty.equals(PRESUMED.getCertainty())) {
                return PRESUMED;
            } else {
                return null;
            }
        }
    }

    public enum DiagnosisOrder {
        PRIMARY("Primary"), SECONDARY("Secondary");

        DiagnosisOrder(String order) {
            this.order = order;
        }

        private String order;

        public String getOrder() {
            return order;
        }

        public static DiagnosisOrder getOrder(String order) {
            if (order.equals(PRIMARY.getOrder())) {
                return PRIMARY;
            } else if  (order.equals(SECONDARY.getOrder())) {
                return SECONDARY;
            } else {
                return null;
            }
        }
    }
}
