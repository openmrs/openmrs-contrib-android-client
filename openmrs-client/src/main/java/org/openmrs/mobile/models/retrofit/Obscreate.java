
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Obscreate {

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
    private Double value;
    @SerializedName("encounter")
    @Expose
    private String encounter;

    /**
     * 
     * @return
     *     The person
     */
    public String getPerson() {
        return person;
    }

    /**
     * 
     * @param person
     *     The person
     */
    public void setPerson(String person) {
        this.person = person;
    }

    /**
     * 
     * @return
     *     The obsDatetime
     */
    public String getObsDatetime() {
        return obsDatetime;
    }

    /**
     * 
     * @param obsDatetime
     *     The obsDatetime
     */
    public void setObsDatetime(String obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    /**
     * 
     * @return
     *     The concept
     */
    public String getConcept() {
        return concept;
    }

    /**
     * 
     * @param concept
     *     The concept
     */
    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getEncounter() {
        return encounter;
    }

    /**
     *
     * @param concept
     *     The concept
     */
    public void setEncounter(String encounter) {
        this.encounter = encounter;
    }

    /**
     * 
     * @return
     *     The value
     */
    public Double getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(Double value) {
        this.value = value;
    }

}
