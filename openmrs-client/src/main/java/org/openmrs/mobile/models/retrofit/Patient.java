
package org.openmrs.mobile.models.retrofit;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Patient extends Resource {

    private boolean synced=false;

    private Long id;

    @SerializedName("identifiers")
    @Expose
    private List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>();

    @SerializedName("person")
    @Expose
    private Person person;

    @SerializedName("voided")
    @Expose
    private Boolean voided;

    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The identifiers
     */
    public List<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    /**
     * 
     * @param identifiers
     *     The identifiers
     */
    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public PatientIdentifier getIdentifier() {
        if (!identifiers.isEmpty()) {
            return identifiers.get(0);
        } else {
            return null;
        }
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

    public boolean isSynced()
    {
        return synced;
    }

    public void setSynced(boolean synced)
    {
        this.synced=synced;
    }



}
