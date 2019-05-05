package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class SystemSetting extends Resource {

    @Expose
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
