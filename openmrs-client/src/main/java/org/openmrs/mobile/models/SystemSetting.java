package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class SystemSetting extends Resource {

    @Expose
    private String property;
    @Expose
    private String value;
    @Expose
    private String description;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
