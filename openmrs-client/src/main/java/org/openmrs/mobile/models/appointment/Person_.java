
package org.openmrs.mobile.models.appointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person_ {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("birthdateEstimated")
    @Expose
    private Boolean birthdateEstimated;
    @SerializedName("dead")
    @Expose
    private Boolean dead;
    @SerializedName("deathDate")
    @Expose
    private Object deathDate;
    @SerializedName("causeOfDeath")
    @Expose
    private Object causeOfDeath;
    @SerializedName("preferredName")
    @Expose
    private PreferredName preferredName;
    @SerializedName("preferredAddress")
    @Expose
    private PreferredAddress preferredAddress;
    @SerializedName("attributes")
    @Expose
    private List<Object> attributes = null;
    @SerializedName("voided")
    @Expose
    private Boolean voided;
    @SerializedName("deathdateEstimated")
    @Expose
    private Boolean deathdateEstimated;
    @SerializedName("birthtime")
    @Expose
    private Object birthtime;
    @SerializedName("links")
    @Expose
    private List<Link_________> links = null;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public Object getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Object deathDate) {
        this.deathDate = deathDate;
    }

    public Object getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(Object causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public PreferredName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(PreferredName preferredName) {
        this.preferredName = preferredName;
    }

    public PreferredAddress getPreferredAddress() {
        return preferredAddress;
    }

    public void setPreferredAddress(PreferredAddress preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    public List<Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Object> attributes) {
        this.attributes = attributes;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Boolean getDeathdateEstimated() {
        return deathdateEstimated;
    }

    public void setDeathdateEstimated(Boolean deathdateEstimated) {
        this.deathdateEstimated = deathdateEstimated;
    }

    public Object getBirthtime() {
        return birthtime;
    }

    public void setBirthtime(Object birthtime) {
        this.birthtime = birthtime;
    }

    public List<Link_________> getLinks() {
        return links;
    }

    public void setLinks(List<Link_________> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

}
