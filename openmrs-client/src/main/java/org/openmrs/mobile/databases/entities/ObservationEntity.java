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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObservationEntity)) return false;

        ObservationEntity entity = (ObservationEntity) o;

        if (encounterKeyID != entity.encounterKeyID) return false;
        if (displayValue != null ? !displayValue.equals(entity.displayValue) : entity.displayValue != null)
            return false;
        if (diagnosisOrder != null ? !diagnosisOrder.equals(entity.diagnosisOrder) : entity.diagnosisOrder != null)
            return false;
        if (diagnosisList != null ? !diagnosisList.equals(entity.diagnosisList) : entity.diagnosisList != null)
            return false;
        if (diagnosisCertainty != null ? !diagnosisCertainty.equals(entity.diagnosisCertainty) : entity.diagnosisCertainty != null)
            return false;
        if (diagnosisNote != null ? !diagnosisNote.equals(entity.diagnosisNote) : entity.diagnosisNote != null)
            return false;
        return conceptuuid != null ? conceptuuid.equals(entity.conceptuuid) : entity.conceptuuid == null;
    }

    @Override
    public int hashCode() {
        int result = encounterKeyID;
        result = 31 * result + (displayValue != null ? displayValue.hashCode() : 0);
        result = 31 * result + (diagnosisOrder != null ? diagnosisOrder.hashCode() : 0);
        result = 31 * result + (diagnosisList != null ? diagnosisList.hashCode() : 0);
        result = 31 * result + (diagnosisCertainty != null ? diagnosisCertainty.hashCode() : 0);
        result = 31 * result + (diagnosisNote != null ? diagnosisNote.hashCode() : 0);
        result = 31 * result + (conceptuuid != null ? conceptuuid.hashCode() : 0);
        return result;
    }
}
