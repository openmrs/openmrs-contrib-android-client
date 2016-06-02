
package org.openmrs.mobile.models.retrofit;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavedPerson {

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
    private List<Result> attributes = new ArrayList<Result>();
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
    private List<Link> links = new ArrayList<Link>();
    @SerializedName("resourceVersion")
    @Expose
    private String resourceVersion;

    /**
     * 
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * @return
     *     The display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * 
     * @param display
     *     The display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * 
     * @return
     *     The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * 
     * @param gender
     *     The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 
     * @return
     *     The age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 
     * @param age
     *     The age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 
     * @return
     *     The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * 
     * @param birthdate
     *     The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * 
     * @return
     *     The birthdateEstimated
     */
    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    /**
     * 
     * @param birthdateEstimated
     *     The birthdateEstimated
     */
    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    /**
     * 
     * @return
     *     The dead
     */
    public Boolean getDead() {
        return dead;
    }

    /**
     * 
     * @param dead
     *     The dead
     */
    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    /**
     * 
     * @return
     *     The deathDate
     */
    public Object getDeathDate() {
        return deathDate;
    }

    /**
     * 
     * @param deathDate
     *     The deathDate
     */
    public void setDeathDate(Object deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * 
     * @return
     *     The causeOfDeath
     */
    public Object getCauseOfDeath() {
        return causeOfDeath;
    }

    /**
     * 
     * @param causeOfDeath
     *     The causeOfDeath
     */
    public void setCauseOfDeath(Object causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * 
     * @return
     *     The preferredName
     */
    public PreferredName getPreferredName() {
        return preferredName;
    }

    /**
     * 
     * @param preferredName
     *     The preferredName
     */
    public void setPreferredName(PreferredName preferredName) {
        this.preferredName = preferredName;
    }

    /**
     * 
     * @return
     *     The preferredAddress
     */
    public PreferredAddress getPreferredAddress() {
        return preferredAddress;
    }

    /**
     * 
     * @param preferredAddress
     *     The preferredAddress
     */
    public void setPreferredAddress(PreferredAddress preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    /**
     * 
     * @return
     *     The attributes
     */
    public List<Result> getAttributes() {
        return attributes;
    }

    /**
     * 
     * @param attributes
     *     The attributes
     */
    public void setAttributes(List<Result> attributes) {
        this.attributes = attributes;
    }

    /**
     * 
     * @return
     *     The voided
     */
    public Boolean getVoided() {
        return voided;
    }

    /**
     * 
     * @param voided
     *     The voided
     */
    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    /**
     * 
     * @return
     *     The deathdateEstimated
     */
    public Boolean getDeathdateEstimated() {
        return deathdateEstimated;
    }

    /**
     * 
     * @param deathdateEstimated
     *     The deathdateEstimated
     */
    public void setDeathdateEstimated(Boolean deathdateEstimated) {
        this.deathdateEstimated = deathdateEstimated;
    }

    /**
     * 
     * @return
     *     The birthtime
     */
    public Object getBirthtime() {
        return birthtime;
    }

    /**
     * 
     * @param birthtime
     *     The birthtime
     */
    public void setBirthtime(Object birthtime) {
        this.birthtime = birthtime;
    }

    /**
     * 
     * @return
     *     The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The resourceVersion
     */
    public String getResourceVersion() {
        return resourceVersion;
    }

    /**
     * 
     * @param resourceVersion
     *     The resourceVersion
     */
    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

}
