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

public class VisitItemDTO implements Serializable {
    private Long visitID;
    private String patientName;
    private String patientIdentifier;
    private String visitPlace;
    private String visitType;
    private Long visitStart;

    public VisitItemDTO(Long visitID, String patientName, String patientIdentifier, String visitPlace, String visitType, Long visitStart) {
        this.visitID = visitID;
        this.patientName = patientName;
        this.patientIdentifier = patientIdentifier;
        this.visitPlace = visitPlace;
        this.visitType = visitType;
        this.visitStart = visitStart;
    }

    public Long getVisitID() {
        return visitID;
    }

    public void setVisitID(Long visitID) {
        this.visitID = visitID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public String getVisitPlace() {
        return visitPlace;
    }

    public void setVisitPlace(String visitPlace) {
        this.visitPlace = visitPlace;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Long getVisitStart() {
        return visitStart;
    }

    public void setVisitStart(Long visitStart) {
        this.visitStart = visitStart;
    }
}
