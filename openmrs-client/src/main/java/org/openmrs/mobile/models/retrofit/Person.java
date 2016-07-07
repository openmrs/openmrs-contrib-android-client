
package org.openmrs.mobile.models.retrofit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person implements Serializable {

    @SerializedName("names")
    @Expose
    private List<PersonName> names = new ArrayList<PersonName>();
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("birthdateEstimated")
    @Expose
    private boolean birthdateEstimated;
    @SerializedName("addresses")
    @Expose
    private List<PersonAddress> addresses = new ArrayList<PersonAddress>();
    @SerializedName("attributes")
    @Expose
    private List<PersonAttribute> attributes = new ArrayList<PersonAttribute>();

    /**
     * 
     * @return
     *     The names
     */
    public List<PersonName> getNames() {
        return names;
    }

    public PersonName getName() {
        if (!names.isEmpty()) {
            return names.get(0);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param names
     *     The names
     */
    public void setNames(List<PersonName> names) {
        this.names = names;
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
    public boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    /**
     *
     * @param birthdateEstimated
     *     The birthdate
     */
    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }


    /**
     * 
     * @return
     *     The addresses
     */
    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public PersonAddress getAddress() {
        if (!addresses.isEmpty()) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param addresses
     *     The addresses
     */
    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    /**
     * 
     * @return
     *     The attributes
     */
    public List<PersonAttribute> getAttributes() {
        return attributes;
    }

    /**
     *
     * @param attributes
     *     The attributes
     */
    public void setAttributes(List<PersonAttribute> attributes) {
        this.attributes = attributes;
    }


}
