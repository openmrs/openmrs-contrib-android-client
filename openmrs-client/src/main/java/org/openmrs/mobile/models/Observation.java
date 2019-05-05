/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Observation extends Resource implements Serializable {

    @SerializedName("concept")
    @Expose
    private Concept concept;

    private Long id;
    private Long encounterID;
    private String displayValue;

    private String diagnosisList;
    private String diagnosisCertainty;
    private String diagnosisOrder;

    private String diagnosisNote;

    /**
     *
     * @return
     *     The concept
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     *
     * @param concept
     *     The concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

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

    public String getDisplayValue() {
        if (displayValue == null && display != null && display.contains(":")) {
            setDisplayValue(display.split(":")[1]);
        }
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getShortDiagnosisCertainty() {
        return diagnosisCertainty.split(" ")[0];
    }
    public String getDiagnosisCertainty() {
        return diagnosisCertainty;
    }
    public void setDiagnosisCertanity(String certanity) {
        this.diagnosisCertainty = certanity;
    }

    public String getDiagnosisOrder() {
        return diagnosisOrder;
    }
    public void setDiagnosisOrder(String diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public String getDiagnosisList() {
        return diagnosisList;
    }
    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

}
