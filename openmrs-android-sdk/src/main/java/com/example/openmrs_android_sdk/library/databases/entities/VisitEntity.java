package com.example.openmrs_android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.openmrs_android_sdk.library.models.Resource;

@Entity(tableName = "visits")
public class VisitEntity extends Resource {
    @NonNull
    @ColumnInfo(name = "patient_id")
    private long patientKeyID;
    @ColumnInfo(name = "visit_type")
    private String visitType;
    @ColumnInfo(name = "visit_place")
    private String visitPlace;
    @NonNull
    @ColumnInfo(name = "start_date")
    private String startDate;
    @ColumnInfo(name = "stop_date")
    private String stopDate;

    public VisitEntity() {
    }

    public void setPatientKeyID(long patientKeyID) {
        this.patientKeyID = patientKeyID;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public void setVisitPlace(String visitPlace) {
        this.visitPlace = visitPlace;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public long getPatientKeyID() {
        return patientKeyID;
    }

    public String getVisitType() {
        return visitType;
    }

    public String getVisitPlace() {
        return visitPlace;
    }

    @NonNull
    public String getStartDate() {
        return startDate;
    }

    public String isStartDate() {
        return startDate;
    }

    public String getStopDate() {
        return stopDate;
    }
}
