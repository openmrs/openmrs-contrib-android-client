
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {

    @SerializedName("givenName")
    @Expose
    private String givenName;
    @SerializedName("familyName")
    @Expose
    private String familyName;

    /**
     * 
     * @return
     *     The givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * 
     * @param givenName
     *     The givenName
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * 
     * @return
     *     The familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * 
     * @param familyName
     *     The familyName
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

}
