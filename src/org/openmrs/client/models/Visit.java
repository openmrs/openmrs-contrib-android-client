package org.openmrs.client.models;

import java.io.Serializable;

public class Visit implements Serializable {
    private Long id;
    private String uuid;
    private Long patientID;
    private String visitType;
    private String visitPlace;
    private Long startDate;
    private Long stopDate;

    public Visit() {
    }

    public Long getId() {
        return id;
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

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Long getStopDate() {
        return stopDate;
    }

    public void setStopDate(Long stopDate) {
        this.stopDate = stopDate;
    }

    public Long getPatientID() {
        return patientID;
    }

    public void setPatientID(Long patientID) {
        this.patientID = patientID;
    }

    public String getVisitPlace() {
        return visitPlace;
    }

    public void setVisitPlace(String visitPlace) {
        this.visitPlace = visitPlace;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
}
