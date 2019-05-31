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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncounterEntity)) return false;

        EncounterEntity that = (EncounterEntity) o;

        if (visitKeyId != null ? !visitKeyId.equals(that.visitKeyId) : that.visitKeyId != null)
            return false;
        if (!encounterDateTime.equals(that.encounterDateTime)) return false;
        if (encounterType != null ? !encounterType.equals(that.encounterType) : that.encounterType != null)
            return false;
        if (patientUuid != null ? !patientUuid.equals(that.patientUuid) : that.patientUuid != null)
            return false;
        return formUuid != null ? formUuid.equals(that.formUuid) : that.formUuid == null;
    }

    @Override
    public int hashCode() {
        int result = visitKeyId != null ? visitKeyId.hashCode() : 0;
        result = 31 * result + encounterDateTime.hashCode();
        result = 31 * result + (encounterType != null ? encounterType.hashCode() : 0);
        result = 31 * result + (patientUuid != null ? patientUuid.hashCode() : 0);
        result = 31 * result + (formUuid != null ? formUuid.hashCode() : 0);
        return result;
    }
}
