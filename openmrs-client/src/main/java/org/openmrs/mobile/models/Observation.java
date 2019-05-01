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

import java.io.Serializable;


public class Observation extends Resource implements Serializable {

    @SerializedName("concept")
    @Expose
    private Concept concept;
    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("obsDatetime")
    @Expose
    private String obsDatetime;
    @SerializedName("accessionNumber")
    @Expose
    private int accessionNumber;
    @SerializedName("obsGroup")
    @Expose
    private Observation obsGroup;
    @SerializedName("valueCodedName")
    @Expose
    private String valueCodedName;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("location")
    @Expose
    private Resource location=null;
    @SerializedName("encounter")
    @Expose
    private Encounter encounter=null;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("formFieldPath")
    @Expose
    private String formFieldPath;
    @SerializedName("formFieldNamespace")
    @Expose
    private String formFieldNamespace;
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

    private Long id;
    private Long encounterID;
    private String displayValue;

    private String diagnosisList;
    private String diagnosisCertainty;
    private String diagnosisOrder;

    private String diagnosisNote;

    /**
     *
     * @return
     *     The concept
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     *
     * @param concept
     *     The concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    /**
     *
     * @return
     *     The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     *
     * @param person
     *     The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     *
     * @return
     *     The obsDatetime
     */
    public String getObsDatetime() {
        return obsDatetime;
    }

    /**
     *
     * @param obsDatetime
     *     The obsDatetime
     */
    public void setObsDatetime(String obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    /**
     *
     * @return
     *     The accessionNumber
     */
    public int getAccessionNumber() {
        return accessionNumber;
    }

    /**
     *
     * @param accessionNumber
     *     The accessionNumber
     */
    public void setAccessionNumber(int accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    /**
     *
     * @return
     *     The obsGroup
     */
    public Observation getObsGroup() {
        return obsGroup;
    }

    /**
     *
     * @param obsGroup
     *     The obsGroup
     */
    public void setObsGroup(Observation obsGroup) {
        this.obsGroup = obsGroup;
    }

    /**
     *
     * @return
     *     The valueCodedName
     */
    public String getValueCodedName() {
        return valueCodedName;
    }

    /**
     *
     * @param valueCodedName
     *     The valueCodedName
     */
    public void setValueCodedName(String valueCodedName) {
        this.valueCodedName = valueCodedName;
    }

    /**
     *
     * @return
     *     The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     *     The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     *     The encounter
     */
    public Encounter getEncounter() {
        return encounter;
    }

    /**
     *
     * @param encounter
     *     The encounter
     */
    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
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
     *     The formFieldPath
     */
    public String getFormFieldPath() {
        return formFieldPath;
    }

    /**
     *
     * @param formFieldPath
     *     The formFieldPath
     */
    public void setFormFieldPath(String formFieldPath) {
        this.formFieldPath = formFieldPath;
    }

    /**
     *
     * @return
     *     The formFieldNamespace
     */
    public String getFormFieldNamespace() {
        return formFieldNamespace;
    }

    /**
     *
     * @param formFieldNamespace
     *     The formFieldNamespace
     */
    public void setFormFieldNamespace(String formFieldNamespace) {
        this.formFieldNamespace = formFieldNamespace;
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

    public Long getId() {
        return id;
    }

    public Long getEncounterID() {
        return encounterID;
    }

    public void setEncounterID(Long encounterID) {
        this.encounterID = encounterID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayValue() {
        if (displayValue == null && display != null && display.contains(":")) {
            setDisplayValue(display.split(":")[1]);
        }
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getShortDiagnosisCertainty() {
        return diagnosisCertainty.split(" ")[0];
    }
    public String getDiagnosisCertainty() {
        return diagnosisCertainty;
    }
    public void setDiagnosisCertanity(String certanity) {
        this.diagnosisCertainty = certanity;
    }

    public String getDiagnosisOrder() {
        return diagnosisOrder;
    }
    public void setDiagnosisOrder(String diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public String getDiagnosisList() {
        return diagnosisList;
    }
    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

}
