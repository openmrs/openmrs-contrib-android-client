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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.utilities.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Encounter extends Resource implements Serializable{

    private Long id;

    @SerializedName("encounterDatetime")
    @Expose
    private String encounterDatetime;
    @SerializedName("patient")
    @Expose
    private Patient patient;
    @SerializedName("location")
    @Expose
    private Resource location;
    @SerializedName("form")
    @Expose
    private Form form;
    @SerializedName("encounterType")
    @Expose
    private EncounterType encounterType;
    @SerializedName("obs")
    @Expose
    private List<Observation> observations = new ArrayList<Observation>();
    @SerializedName("orders")
    @Expose
    private List<Object> orders = new ArrayList<Object>();
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("visit")
    @Expose
    private Visit visit;
    @SerializedName("encounterProviders")
    @Expose
    private List<Resource> encounterProviders = new ArrayList<Resource>();
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

    private Long visitID;
    private String patientUUID;

    public Long getId() {
        return id;
    }

    public Long getVisitID() {
        return visitID;
    }

    public void setVisitID(Long visitID) {
        this.visitID = visitID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientUUID() {
        return patientUUID;
    }

    public void setPatientUUID(String patientUUID) {
        this.patientUUID = patientUUID;
    }

    /**
     *
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     *
     * @return
     *     The display
     */
    public String getDisplay() {
        return display;
    }

    /**
     *
     * @param display
     *     The display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     *
     * @return
     *     The encounterDatetime
     */
    public Long getEncounterDatetime() {
        return DateUtils.convertTime(encounterDatetime);
    }

    public String getEncounterDate(){
        return encounterDatetime;
    }
    /**
     *
     * @param encounterDatetime
     *     The encounterDatetime
     */
    public void setEncounterDatetime(String encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

    /**
     *
     * @return
     *     The patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     *
     * @param patient
     *     The patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     *
     * @return
     *     The location
     */
    public Resource getLocation() {
        return location;
    }

    /**
     *
     * @param location
     *     The location
     */
    public void setLocation(Resource location) {
        this.location = location;
    }

    /**
     *
     * @return
     *     The form
     */
    public Form getForm() {
        return form;
    }

    /**
     *
     * @param form
     *     The form
     */
    public void setForm(Form form) {
        this.form = form;
    }

    /**
     *
     * @return
     *     The encounterTypeToken
     */
    public EncounterType getEncounterType() {
        return encounterType;
    }


    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    /**
     * 
     * @return
     *     The obs
     */
    public List<Observation> getObservations() {
        return observations;
    }

    /**
     * 
     *     The obs
     */
    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }
    /**
     * 
     * @return
     *     The orders
     */
    public List<Object> getOrders() {
        return orders;
    }

    /**
     * 
     * @param orders
     *     The orders
     */
    public void setOrders(List<Object> orders) {
        this.orders = orders;
    }

    /**
     * 
     * @return
     *     The voided
     */
    public Boolean getVoided() {
        return voided;
    }

    /**
     * 
     * @param voided
     *     The voided
     */
    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    /**
     * 
     * @return
     *     The visit
     */
    public Visit getVisit() {
        return visit;
    }

    /**
     *
     * @param visit
     *     The visit
     */
    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    /**
     * 
     * @return
     *     The encounterProviders
     */
    public List<Resource> getEncounterProviders() {
        return encounterProviders;
    }

    /**
     * 
     * @param encounterProviders
     *     The encounterProviders
     */
    public void setEncounterProviders(List<Resource> encounterProviders) {
        this.encounterProviders = encounterProviders;
    }

    /**
     * 
     * @return
     *     The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The resourceVersion
     */
    public String getResourceVersion() {
        return resourceVersion;
    }

    /**
     * 
     * @param resourceVersion
     *     The resourceVersion
     */
    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public String getFormUuid(){
        if(form != null)
            return form.getUuid();
        else
            return null;
    }

}
