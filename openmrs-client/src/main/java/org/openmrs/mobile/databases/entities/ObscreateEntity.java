package org.openmrs.mobile.databases.entities;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.models.Resource;

@Entity(tableName = "obscreates")
public class ObscreateEntity extends Resource {

    @SerializedName("person")
    @Expose
    private String person;
    @SerializedName("obsDatetime")
    @Expose
    private String obsDatetime;
    @SerializedName("concept")
    @Expose
    private String concept;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("encounter")
    @Expose
    private String encounter;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(String obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getEncounter() {
        return encounter;
    }

    public void setEncounter(String encounter) {
        this.encounter = encounter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
