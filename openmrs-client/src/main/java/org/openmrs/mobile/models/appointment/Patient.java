
package org.openmrs.mobile.models.appointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Patient {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("identifiers")
    @Expose
    private List<Identifier> identifiers = null;
    @SerializedName("person")
    @Expose
    private Person_ person;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("links")
    @Expose
    private List<Link__________> links = null;
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Person_ getPerson() {
        return person;
    }

    public void setPerson(Person_ person) {
        this.person = person;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public List<Link__________> getLinks() {
        return links;
    }

    public void setLinks(List<Link__________> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

}
