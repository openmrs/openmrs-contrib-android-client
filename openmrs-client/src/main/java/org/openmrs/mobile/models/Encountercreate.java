/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Table(name = "encountercreate")
public class Encountercreate extends Model implements Serializable{

    private Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private Type obscreatetype = new TypeToken<List<Obscreate>>(){}.getType();

    @Column(name = "visit")
    @SerializedName("visit")
    @Expose
    private String visit;

    @Column(name = "patient")
    @SerializedName("patient")
    @Expose
    private String patient;

    @Column(name = "patientid")
    private Long patientId;

    @Column(name = "encounterType")
    @SerializedName("encounterType")
    @Expose
    private String encounterType;

    @SerializedName("form")
    @Expose
    private String formUuid;

    @Column(name = "formname")
    private String formname;

    @Column(name = "synced")
    private boolean synced=false;

    @SerializedName("obs")
    @Expose
    private List<Obscreate> observations = new ArrayList<>();

    @Column(name = "obs")
    private String obslist;

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

    public List<Obscreate> getObservations() {
        return observations;
    }

    public void setObservations(List<Obscreate> observations) {
        this.observations = observations;
    }


    public void setObslist()
    {
        this.obslist = gson.toJson(observations,obscreatetype);
    }

    public void pullObslist() {
        this.observations = gson.fromJson(this.obslist,obscreatetype);
    }



}
