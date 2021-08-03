package com.openmrs.android_sdk.library.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.openmrs.android_sdk.library.models.Resource;


/**
 * The type Encounter entity.
 */
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

    /**
     * Instantiates a new Encounter entity.
     */
    public EncounterEntity() {
    }

    /**
     * Sets visit key id.
     *
     * @param visitKeyId the visit key id
     */
    public void setVisitKeyId(String visitKeyId) {
        this.visitKeyId = visitKeyId;
    }

    /**
     * Sets encounter date time.
     *
     * @param encounterDateTime the encounter date time
     */
    public void setEncounterDateTime(@NonNull String encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    /**
     * Sets encounter type.
     *
     * @param encounterType the encounter type
     */
    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    /**
     * Sets patient uuid.
     *
     * @param patientUuid the patient uuid
     */
    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    /**
     * Sets form uuid.
     *
     * @param formUuid the form uuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * Sets location uuid.
     *
     * @param locationUuid the location uuid
     */
    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    /**
     * Sets encounter provider uuid.
     *
     * @param encounterProviderUuid the encounter provider uuid
     */
    public void setEncounterProviderUuid(String encounterProviderUuid) {
        this.encounterProviderUuid = encounterProviderUuid;
    }

    /**
     * Gets visit key id.
     *
     * @return the visit key id
     */
    public String getVisitKeyId() {
        return visitKeyId;
    }

    /**
     * Gets encounter date time.
     *
     * @return the encounter date time
     */
    @NonNull
    public String getEncounterDateTime() {
        return encounterDateTime;
    }

    /**
     * Gets encounter type.
     *
     * @return the encounter type
     */
    public String getEncounterType() {
        return encounterType;
    }

    /**
     * Gets patient uuid.
     *
     * @return the patient uuid
     */
    public String getPatientUuid() {
        return patientUuid;
    }

    /**
     * Gets form uuid.
     *
     * @return the form uuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * Gets location uuid.
     *
     * @return the location uuid
     */
    public String getLocationUuid() {
        return locationUuid;
    }

    /**
     * Gets encounter provider uuid.
     *
     * @return the encounter provider uuid
     */
    public String getEncounterProviderUuid() {
        return encounterProviderUuid;
    }
}