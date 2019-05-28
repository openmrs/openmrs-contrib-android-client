package org.openmrs.mobile.databases.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.Resource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "encounter_create")
public class EncounterCreateEntity extends Resource {

    @ColumnInfo(name = "visit")
    @SerializedName("visit")
    @Expose
    private String visit;

    @ColumnInfo(name = "patient")
    @SerializedName("patient")
    @Expose
    private String patient;

    @ColumnInfo(name = "patientid")
    private Long patientId;

    @ColumnInfo(name = "encounterType")
    @SerializedName("encounterType")
    @Expose
    private String encounterType;

    @SerializedName("form")
    @Expose
    private String formUuid;

    @ColumnInfo(name = "formname")
    private String formname;

    @ColumnInfo(name = "synced")
    private boolean synced=false;

    @ColumnInfo(name = "obs")
    @SerializedName("obs")
    @Expose
    private List<ObscreateEntity> observations = new ArrayList<>();

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }


    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public List<ObscreateEntity> getObservations() {
        return observations;
    }

    public void setObservations(List<ObscreateEntity> observations) {
        this.observations = observations;
    }

}
