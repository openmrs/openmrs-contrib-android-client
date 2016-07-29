
package org.openmrs.mobile.models.retrofit;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Table(name = "formresource")
public class FormResource extends Model implements Serializable{

    Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    Type formresourcelistType = new TypeToken<List<FormResource>>(){}.getType();

    @Column(name="name")
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("resources")
    @Expose
    private List<FormResource> resources = new ArrayList<FormResource>();

    @Column(name = "resources")
    private String resourcelist;

    @Column(name = "valueReference")
    @SerializedName("valueReference")
    @Expose
    private String valueReference;


    @Column(name = "uuid")
    @SerializedName("uuid")
    @Expose
    private String uuid;

    @Column(name = "display")
    @SerializedName("display")
    @Expose
    private String display;

    @Column(name = "links")
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    public FormResource()
    {
        super();
    }

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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

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

    public void setResourcelist()
    {
        this.resourcelist=gson.toJson(resources,formresourcelistType);
    }

    public List<FormResource> getResourceList() {

        List<FormResource> resourceList=gson.fromJson(this.resourcelist,formresourcelistType);
        return resourceList;
    }

}
