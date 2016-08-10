/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models.retrofit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Observation implements Serializable {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
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
    private String obsGroup;
    @SerializedName("valueCodedName")
    @Expose
    private String valueCodedName;
    @SerializedName("groupMembers")
    @Expose
    private String groupMembers;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("location")
    @Expose
    private Resource location=null;
    @SerializedName("order")
    @Expose
    private String order;
    @SerializedName("encounter")
    @Expose
    private Encounter encounter=null;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("value")
    @Expose
    private Double value;
    @SerializedName("valueModifier")
    @Expose
    private String valueModifier;
    @SerializedName("formFieldPath")
    @Expose
    private String formFieldPath;
    @SerializedName("formFieldNamespace")
    @Expose
    private String formFieldNamespace;
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

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
    public String getObsGroup() {
        return obsGroup;
    }

    /**
     *
     * @param obsGroup
     *     The obsGroup
     */
    public void setObsGroup(String obsGroup) {
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
     *     The groupMembers
     */
    public String getGroupMembers() {
        return groupMembers;
    }

    /**
     *
     * @param groupMembers
     *     The groupMembers
     */
    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
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
     *     The order
     */
    public String getOrder() {
        return order;
    }

    /**
     *
     * @param order
     *     The order
     */
    public void setOrder(String order) {
        this.order = order;
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
     *     The value
     */
    public Double getValue() {
        return value;
    }

    /**
     *
     * @param value
     *     The value
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     *
     * @return
     *     The valueModifier
     */
    public String getValueModifier() {
        return valueModifier;
    }

    /**
     *
     * @param valueModifier
     *     The valueModifier
     */
    public void setValueModifier(String valueModifier) {
        this.valueModifier = valueModifier;
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

    private Long id;
    private Long encounterID;
    private String displayValue;
    private DiagnosisOrder diagnosisOrder;
    private String diagnosisList;
    private DiagnosisCertainty diagnosisCertainty;
    private String diagnosisNote;

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
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public DiagnosisOrder getDiagnosisOrder() {
        return diagnosisOrder;
    }

    public void setDiagnosisOrder(DiagnosisOrder diagnosisOrder) {
        this.diagnosisOrder = diagnosisOrder;
    }

    public String getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(String diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public DiagnosisCertainty getDiagnosisCertainty() {
        return diagnosisCertainty;
    }

    public void setDiagnosisCertainty(DiagnosisCertainty diagnosisCertainty) {
        this.diagnosisCertainty = diagnosisCertainty;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

    public enum DiagnosisCertainty {
        PRESUMED("Presumed diagnosis"), CONFIRMED("Confirmed diagnosis");

        DiagnosisCertainty(String certainty) {
            this.certainty = certainty;
        }

        private String certainty;

        public String getCertainty() {
            return certainty;
        }

        public String getShortCertainty() {
            return certainty.split(" ")[0];
        }

        public static DiagnosisCertainty getCertainty(String certainty) {
            if (certainty.equals(CONFIRMED.getCertainty())) {
                return CONFIRMED;
            } else if (certainty.equals(PRESUMED.getCertainty())) {
                return PRESUMED;
            } else {
                return null;
            }
        }
    }

    public enum DiagnosisOrder {
        PRIMARY("Primary"), SECONDARY("Secondary");

        DiagnosisOrder(String order) {
            this.order = order;
        }

        private String order;

        public String getOrder() {
            return order;
        }

        public static DiagnosisOrder getOrder(String order) {
            if (order.equals(PRIMARY.getOrder())) {
                return PRIMARY;
            } else if  (order.equals(SECONDARY.getOrder())) {
                return SECONDARY;
            } else {
                return null;
            }
        }
    }

}
