package org.openmrs.client.models;

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
