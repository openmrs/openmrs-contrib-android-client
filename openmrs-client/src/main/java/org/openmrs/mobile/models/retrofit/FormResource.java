
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormResource extends Resource {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("resources")
    @Expose
    private List<FormResource> resources = new ArrayList<FormResource>();
    @SerializedName("valueReference")
    @Expose
    private String valueReference;

    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FormResource> getResources() {
        return resources;
    }

    public void setResources(List<FormResource> resources) {
        this.resources = resources;
    }

}
