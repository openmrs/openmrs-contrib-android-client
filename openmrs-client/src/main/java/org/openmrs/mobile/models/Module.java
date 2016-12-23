package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class Module extends Resource {

    @Expose
    private String name;
    @Expose
    private String version;
    @Expose
    private String packageName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
