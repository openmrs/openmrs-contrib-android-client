package com.example.openmrs_android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.openmrs_android_sdk.library.models.Resource;


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
    @ColumnInfo(name = "location_uuid")
    private String locationUuid;
    @ColumnInfo(name = "encounter_provider_uuid")
    private String encounterProviderUuid;

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

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public void setEncounterProviderUuid(String encounterProviderUuid) {
        this.encounterProviderUuid = encounterProviderUuid;
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

    public String getLocationUuid() {
        return locationUuid;
    }

    public String getEncounterProviderUuid() {
        return encounterProviderUuid;
    }
}