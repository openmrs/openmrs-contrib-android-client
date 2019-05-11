package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "visits")
public class VisitsEntity extends Resource {

    @NonNull
    @ColumnInfo(name = "patient_id")
    private int patientKeyID;

    @ColumnInfo(name = "visit_type")
    private String visitType;

    @ColumnInfo(name = "visit_place")
    private String visitPlace;

    @NonNull
    @ColumnInfo(name = "start_date")
    private boolean startDate;

    @ColumnInfo(name = "stop_date")
    private String stopDate;

    public VisitsEntity() {
    }

    public void setPatientKeyID(int patientKeyID) {
        this.patientKeyID = patientKeyID;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public void setVisitPlace(String visitPlace) {
        this.visitPlace = visitPlace;
    }

    public void setStartDate(boolean startDate) {
        this.startDate = startDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public int getPatientKeyID() {
        return patientKeyID;
    }

    public String getVisitType() {
        return visitType;
    }

    public String getVisitPlace() {
        return visitPlace;
    }

    public boolean isStartDate() {
        return startDate;
    }

    public String getStopDate() {
        return stopDate;
    }
}
