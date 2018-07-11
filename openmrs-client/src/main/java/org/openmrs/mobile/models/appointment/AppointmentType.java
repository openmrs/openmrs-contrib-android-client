
package org.openmrs.mobile.models.appointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentType {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("links")
    @Expose
    private List<Link___________> links = null;

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

    public List<Link___________> getLinks() {
        return links;
    }

    public void setLinks(List<Link___________> links) {
        this.links = links;
    }

}
