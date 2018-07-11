
package org.openmrs.mobile.models.timeblocks;

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
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("appointmentBlock")
    @Expose
    private AppointmentBlock appointmentBlock;
    @SerializedName("countOfAppointments")
    @Expose
    private Integer countOfAppointments;
    @SerializedName("unallocatedMinutes")
    @Expose
    private Integer unallocatedMinutes;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("links")
    @Expose
    private List<Link_____> links = null;
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

    public AppointmentBlock getAppointmentBlock() {
        return appointmentBlock;
    }

    public void setAppointmentBlock(AppointmentBlock appointmentBlock) {
        this.appointmentBlock = appointmentBlock;
    }

    public Integer getCountOfAppointments() {
        return countOfAppointments;
    }

    public void setCountOfAppointments(Integer countOfAppointments) {
        this.countOfAppointments = countOfAppointments;
    }

    public Integer getUnallocatedMinutes() {
        return unallocatedMinutes;
    }

    public void setUnallocatedMinutes(Integer unallocatedMinutes) {
        this.unallocatedMinutes = unallocatedMinutes;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public List<Link_____> getLinks() {
        return links;
    }

    public void setLinks(List<Link_____> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

}
