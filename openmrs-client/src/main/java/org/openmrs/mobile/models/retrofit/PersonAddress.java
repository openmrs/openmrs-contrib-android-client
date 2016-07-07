
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PersonAddress implements Serializable {

    @SerializedName("preferred")
    @Expose
    private Boolean preferred;
    @SerializedName("address1")
    @Expose
    private String address2;
    @SerializedName("address2")
    @Expose
    private String address1;
    @SerializedName("cityVillage")
    @Expose
    private String cityVillage;
    @SerializedName("stateProvince")
    @Expose
    private String stateProvince;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("postalCode")
    @Expose
    private String postalCode;

    /**
     * 
     * @return
     *     The preferred
     */
    public Boolean getPreferred() {
        return preferred;
    }

    /**
     * 
     * @param preferred
     *     The preferred
     */
    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    /**
     * 
     * @return
     *     The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * 
     * @param address1
     *     The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    /**
     *
     * @return
     *     The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     *
     * @param address2
     *     The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * 
     * @return
     *     The cityVillage
     */
    public String getAddressString()
    {
        String addr="";
        if(address1!=null)
            addr+=address1+"\n";
        if(address2!=null)
            addr+=address2;
        return addr;
    }
    public String getCityVillage() {
        return cityVillage;
    }

    /**
     * 
     * @param cityVillage
     *     The cityVillage
     */
    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    /**
     * 
     * @return
     *     The stateProvince
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * 
     * @param stateProvince
     *     The stateProvince
     */
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param postalCode
     *     The postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
