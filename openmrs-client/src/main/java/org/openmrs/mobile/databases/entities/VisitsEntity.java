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
    private String startDate;

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

    public void setStartDate(String startDate) {
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

    public String getStartDate() {
        return startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitsEntity)) return false;

        VisitsEntity entity = (VisitsEntity) o;

        if (patientKeyID != entity.patientKeyID) return false;
        if (visitType != null ? !visitType.equals(entity.visitType) : entity.visitType != null)
            return false;
        if (visitPlace != null ? !visitPlace.equals(entity.visitPlace) : entity.visitPlace != null)
            return false;
        if (!startDate.equals(entity.startDate)) return false;
        return stopDate != null ? stopDate.equals(entity.stopDate) : entity.stopDate == null;
    }

    @Override
    public int hashCode() {
        int result = patientKeyID;
        result = 31 * result + (visitType != null ? visitType.hashCode() : 0);
        result = 31 * result + (visitPlace != null ? visitPlace.hashCode() : 0);
        result = 31 * result + startDate.hashCode();
        result = 31 * result + (stopDate != null ? stopDate.hashCode() : 0);
        return result;
    }
}
