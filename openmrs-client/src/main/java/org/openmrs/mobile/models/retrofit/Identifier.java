
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Identifier {

    @SerializedName("identifierType")
    @Expose
    private String identifierType;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("location")
    @Expose
    private String location;

    /**
     * 
     * @return
     *     The identifierType
     */
    public String getIdentifierType() {
        return identifierType;
    }

    /**
     * 
     * @param identifierType
     *     The identifierType
     */
    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    /**
     * 
     * @return
     *     The identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 
     * @param identifier
     *     The identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * 
     * @return
     *     The location
     */
    public String getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
