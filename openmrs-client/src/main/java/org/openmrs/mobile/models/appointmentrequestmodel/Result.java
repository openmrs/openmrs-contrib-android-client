
package org.openmrs.mobile.models.appointmentrequestmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("patient")
    @Expose
    private Patient patient;
    @SerializedName("appointmentType")
    @Expose
    private AppointmentType appointmentType;
    @SerializedName("provider")
    @Expose
    private Provider provider;
    @SerializedName("requestedBy")
    @Expose
    private RequestedBy requestedBy;
    @SerializedName("requestedOn")
    @Expose
    private String requestedOn;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("minTimeFrameValue")
    @Expose
    private Integer minTimeFrameValue;
    @SerializedName("minTimeFrameUnits")
    @Expose
    private String minTimeFrameUnits;
    @SerializedName("maxTimeFrameValue")
    @Expose
    private Integer maxTimeFrameValue;
    @SerializedName("maxTimeFrameUnits")
    @Expose
    private String maxTimeFrameUnits;
    @SerializedName("notes")
    @Expose
    private String notes;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public RequestedBy getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(RequestedBy requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(String requestedOn) {
        this.requestedOn = requestedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMinTimeFrameValue() {
        return minTimeFrameValue;
    }

    public void setMinTimeFrameValue(Integer minTimeFrameValue) {
        this.minTimeFrameValue = minTimeFrameValue;
    }

    public String getMinTimeFrameUnits() {
        return minTimeFrameUnits;
    }

    public void setMinTimeFrameUnits(String minTimeFrameUnits) {
        this.minTimeFrameUnits = minTimeFrameUnits;
    }

    public Integer getMaxTimeFrameValue() {
        return maxTimeFrameValue;
    }

    public void setMaxTimeFrameValue(Integer maxTimeFrameValue) {
        this.maxTimeFrameValue = maxTimeFrameValue;
    }

    public String getMaxTimeFrameUnits() {
        return maxTimeFrameUnits;
    }

    public void setMaxTimeFrameUnits(String maxTimeFrameUnits) {
        this.maxTimeFrameUnits = maxTimeFrameUnits;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
