
package org.openmrs.mobile.models.appointmentblocksmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.models.servicestypemodel.Services;

import java.util.List;

public class Result {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("provider")
    @Expose
    private org.openmrs.mobile.models.provider.Result provider;
    @SerializedName("location")
    @Expose
    private org.openmrs.mobile.models.Location location;
    @SerializedName("types")
    @Expose
    private List<Services> types = null;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("links")
    @Expose
    private List<Link____> links = null;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public org.openmrs.mobile.models.provider.Result getProvider() {
        return provider;
    }

    public void setProvider(org.openmrs.mobile.models.provider.Result provider) {
        this.provider = provider;
    }

    public org.openmrs.mobile.models.Location getLocation() {
        return location;
    }

    public void setLocation(org.openmrs.mobile.models.Location location) {
        this.location = location;
    }

    public List<Services> getTypes() {
        return types;
    }

    public void setTypes(List<Services> types) {
        this.types = types;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public List<Link____> getLinks() {
        return links;
    }

    public void setLinks(List<Link____> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

}
