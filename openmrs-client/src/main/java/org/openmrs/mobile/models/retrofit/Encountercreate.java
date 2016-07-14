
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Encountercreate {

    @SerializedName("visit")
    @Expose
    private String visit;
    @SerializedName("patient")
    @Expose
    private String patient;
    @SerializedName("encounterType")
    @Expose
    private String encounterType;
    @SerializedName("obs")
    @Expose
    private List<Obscreate> observations = new ArrayList<>();

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
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

    public List<Obscreate> getObservations() {
        return observations;
    }

    public void setObservations(List<Obscreate> observations) {
        this.observations = observations;
    }



}
