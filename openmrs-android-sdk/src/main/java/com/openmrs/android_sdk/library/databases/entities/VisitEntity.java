package com.openmrs.android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.openmrs.android_sdk.library.models.Resource;

/**
 * The type Visit entity.
 */
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

    /**
     * Instantiates a new Visit entity.
     */
    public VisitEntity() {
    }

    /**
     * Sets patient key id.
     *
     * @param patientKeyID the patient key id
     */
    public void setPatientKeyID(long patientKeyID) {
        this.patientKeyID = patientKeyID;
    }

    /**
     * Sets visit type.
     *
     * @param visitType the visit type
     */
    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    /**
     * Sets visit place.
     *
     * @param visitPlace the visit place
     */
    public void setVisitPlace(String visitPlace) {
        this.visitPlace = visitPlace;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets stop date.
     *
     * @param stopDate the stop date
     */
    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    /**
     * Gets patient key id.
     *
     * @return the patient key id
     */
    public long getPatientKeyID() {
        return patientKeyID;
    }

    /**
     * Gets visit type.
     *
     * @return the visit type
     */
    public String getVisitType() {
        return visitType;
    }

    /**
     * Gets visit place.
     *
     * @return the visit place
     */
    public String getVisitPlace() {
        return visitPlace;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    @NonNull
    public String getStartDate() {
        return startDate;
    }

    /**
     * Is start date string.
     *
     * @return the string
     */
    public String isStartDate() {
        return startDate;
    }

    /**
     * Gets stop date.
     *
     * @return the stop date
     */
    public String getStopDate() {
        return stopDate;
    }
}
