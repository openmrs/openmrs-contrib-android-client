package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "observations")
public class ObservationEntity extends Resource {

    @NonNull
    @ColumnInfo(name = "encounter_id")
    private int encounterKeyID;

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

    public void setEncounterKeyID(int encounterKeyID) {
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

    public int getEncounterKeyID() {
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
