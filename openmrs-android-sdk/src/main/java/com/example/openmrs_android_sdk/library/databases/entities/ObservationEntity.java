package com.example.openmrs_android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.openmrs_android_sdk.library.models.Resource;

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

    public ObservationEntity() {
    }

    public void setEncounterKeyID(long encounterKeyID) {
        this.encounterKeyID = encounterKeyID;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public void setDiagnosisOrder(String diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public void setDiagnosisCertainty(String diagnosisCertainty) {
        this.diagnosisCertainty = diagnosisCertainty;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

    public void setConceptuuid(String conceptuuid) {
        this.conceptuuid = conceptuuid;
    }

    public long getEncounterKeyID() {
        return encounterKeyID;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getDiagnosisOrder() {
        return diagnosisOrder;
    }

    public String getDiagnosisList() {
        return diagnosisList;
    }

    public String getDiagnosisCertainty() {
        return diagnosisCertainty;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public String getConceptuuid() {
        return conceptuuid;
    }
}
