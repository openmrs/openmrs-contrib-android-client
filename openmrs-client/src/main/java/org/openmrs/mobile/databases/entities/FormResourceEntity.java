package org.openmrs.mobile.databases.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.activeandroid.annotation.Column;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.openmrs.mobile.models.FormResource;
import org.openmrs.mobile.models.Link;
import org.openmrs.mobile.models.Resource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "form_resources")
public class FormResourceEntity extends Resource {

    @ColumnInfo(name="name")
    @SerializedName("name")
    @Expose
    private String name;

    @ColumnInfo(name = "resources")
    @SerializedName("resources")
    @Expose
    private List<FormResourceEntity> resources = new ArrayList<>();

    @ColumnInfo(name = "valueReference")
    @SerializedName("valueReference")
    @Expose
    private String valueReference;

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

    public List<FormResourceEntity> getResources() {
        return resources;
    }

    public void setResources(List<FormResourceEntity> resources) {
        this.resources = resources;
    }

}
