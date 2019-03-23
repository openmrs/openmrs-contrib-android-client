package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "encounters")
public class EncounterEntity extends Resource {

    @ColumnInfo(name = "visit_id")
    private String visitKeyId;

    @NonNull
    @ColumnInfo(name = "encounterDatetime")
    private String encounterDateTime;

    @ColumnInfo(name = "type")
    private String encounterType;

    @ColumnInfo(name = "patient_uuid")
    private String patientUuid;

    @ColumnInfo(name = "form_uuid")
    private String formUuid;

    public EncounterEntity() {
    }

    public void setVisitKeyId(String visitKeyId) {
        this.visitKeyId = visitKeyId;
    }

    public void setEncounterDateTime(@NonNull String encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getVisitKeyId() {
        return visitKeyId;
    }

    @NonNull
    public String getEncounterDateTime() {
        return encounterDateTime;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }
}