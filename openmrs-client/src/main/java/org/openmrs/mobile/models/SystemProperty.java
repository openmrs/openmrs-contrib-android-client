package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SystemProperty {
    @SerializedName("value")
    @Expose
    String conceptUUID;

    @SerializedName("display")
    @Expose
    String display;

    public SystemProperty(String conceptUUID, String display) {
        this.conceptUUID = conceptUUID;
        this.display = display;
    }

    public String getConceptUUID() {
        return conceptUUID;
    }

    public String getDisplay() {
        return display;
    }
}
