
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
    private Object accessionNumber;
    @SerializedName("obsGroup")
    @Expose
    private Object obsGroup;
    @SerializedName("valueCodedName")
    @Expose
    private Object valueCodedName;
    @SerializedName("groupMembers")
    @Expose
    private Object groupMembers;
    @SerializedName("comment")
    @Expose
    private Object comment;
    @SerializedName("location")
    @Expose
    private Object location;
    @SerializedName("order")
    @Expose
    private Object order;
    @SerializedName("encounter")
    @Expose
    private Object encounter;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("value")
    @Expose
    private Double value;
    @SerializedName("valueModifier")
    @Expose
    private Object valueModifier;
    @SerializedName("formFieldPath")
    @Expose
    private Object formFieldPath;
    @SerializedName("formFieldNamespace")
    @Expose
    private Object formFieldNamespace;
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
    public Object getAccessionNumber() {
        return accessionNumber;
    }

    /**
     *
     * @param accessionNumber
     *     The accessionNumber
     */
    public void setAccessionNumber(Object accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    /**
     *
     * @return
     *     The obsGroup
     */
    public Object getObsGroup() {
        return obsGroup;
    }

    /**
     *
     * @param obsGroup
     *     The obsGroup
     */
    public void setObsGroup(Object obsGroup) {
        this.obsGroup = obsGroup;
    }

    /**
     *
     * @return
     *     The valueCodedName
     */
    public Object getValueCodedName() {
        return valueCodedName;
    }

    /**
     *
     * @param valueCodedName
     *     The valueCodedName
     */
    public void setValueCodedName(Object valueCodedName) {
        this.valueCodedName = valueCodedName;
    }

    /**
     *
     * @return
     *     The groupMembers
     */
    public Object getGroupMembers() {
        return groupMembers;
    }

    /**
     *
     * @param groupMembers
     *     The groupMembers
     */
    public void setGroupMembers(Object groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     *
     * @return
     *     The comment
     */
    public Object getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     *     The comment
     */
    public void setComment(Object comment) {
        this.comment = comment;
    }

    /**
     *
     * @return
     *     The location
     */
    public Object getLocation() {
        return location;
    }

    /**
     *
     * @param location
     *     The location
     */
    public void setLocation(Object location) {
        this.location = location;
    }

    /**
     *
     * @return
     *     The order
     */
    public Object getOrder() {
        return order;
    }

    /**
     *
     * @param order
     *     The order
     */
    public void setOrder(Object order) {
        this.order = order;
    }

    /**
     *
     * @return
     *     The encounter
     */
    public Object getEncounter() {
        return encounter;
    }

    /**
     *
     * @param encounter
     *     The encounter
     */
    public void setEncounter(Object encounter) {
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
    public Object getValueModifier() {
        return valueModifier;
    }

    /**
     *
     * @param valueModifier
     *     The valueModifier
     */
    public void setValueModifier(Object valueModifier) {
        this.valueModifier = valueModifier;
    }

    /**
     *
     * @return
     *     The formFieldPath
     */
    public Object getFormFieldPath() {
        return formFieldPath;
    }

    /**
     *
     * @param formFieldPath
     *     The formFieldPath
     */
    public void setFormFieldPath(Object formFieldPath) {
        this.formFieldPath = formFieldPath;
    }

    /**
     *
     * @return
     *     The formFieldNamespace
     */
    public Object getFormFieldNamespace() {
        return formFieldNamespace;
    }

    /**
     *
     * @param formFieldNamespace
     *     The formFieldNamespace
     */
    public void setFormFieldNamespace(Object formFieldNamespace) {
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
