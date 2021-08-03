package com.openmrs.android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.openmrs.android_sdk.library.models.Resource;

/**
 * The type Observation entity.
 */
@Entity(tableName = "observations")
public class ObservationEntity extends Resource {
    @NonNull
    @ColumnInfo(name = "encounter_id")
    private long encounterKeyID;
    @ColumnInfo(name = "displayValue")
    private String displayValue;
    @ColumnInfo(name = "diagnosisOrder")
    private String diagnosisOrder;
    @ColumnInfo(name = "diagnosisList")
    private String diagnosisList;
    @ColumnInfo(name = "diagnosisCertainty")
    private String diagnosisCertainty;
    @ColumnInfo(name = "diagnosisNote")
    private String diagnosisNote;
    @ColumnInfo(name = "conceptUuid")
    private String conceptuuid;

    /**
     * Instantiates a new Observation entity.
     */
    public ObservationEntity() {
    }

    /**
     * Sets encounter key id.
     *
     * @param encounterKeyID the encounter key id
     */
    public void setEncounterKeyID(long encounterKeyID) {
        this.encounterKeyID = encounterKeyID;
    }

    /**
     * Sets display value.
     *
     * @param displayValue the display value
     */
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Sets diagnosis order.
     *
     * @param diagnosisOrder the diagnosis order
     */
    public void setDiagnosisOrder(String diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    /**
     * Sets diagnosis list.
     *
     * @param diagnosisList the diagnosis list
     */
    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    /**
     * Sets diagnosis certainty.
     *
     * @param diagnosisCertainty the diagnosis certainty
     */
    public void setDiagnosisCertainty(String diagnosisCertainty) {
        this.diagnosisCertainty = diagnosisCertainty;
    }

    /**
     * Sets diagnosis note.
     *
     * @param diagnosisNote the diagnosis note
     */
    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

    /**
     * Sets conceptuuid.
     *
     * @param conceptuuid the conceptuuid
     */
    public void setConceptuuid(String conceptuuid) {
        this.conceptuuid = conceptuuid;
    }

    /**
     * Gets encounter key id.
     *
     * @return the encounter key id
     */
    public long getEncounterKeyID() {
        return encounterKeyID;
    }

    /**
     * Gets display value.
     *
     * @return the display value
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Gets diagnosis order.
     *
     * @return the diagnosis order
     */
    public String getDiagnosisOrder() {
        return diagnosisOrder;
    }

    /**
     * Gets diagnosis list.
     *
     * @return the diagnosis list
     */
    public String getDiagnosisList() {
        return diagnosisList;
    }

    /**
     * Gets diagnosis certainty.
     *
     * @return the diagnosis certainty
     */
    public String getDiagnosisCertainty() {
        return diagnosisCertainty;
    }

    /**
     * Gets diagnosis note.
     *
     * @return the diagnosis note
     */
    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    /**
     * Gets conceptuuid.
     *
     * @return the conceptuuid
     */
    public String getConceptuuid() {
        return conceptuuid;
    }
}
