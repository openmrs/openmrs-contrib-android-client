
package org.openmrs.mobile.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonAttribute {

    @SerializedName("attributeType")
    @Expose
    private PersonAttributeType attributeType;
    @SerializedName("value")
    @Expose
    private String value;

    /**
     * 
     * @return
     *     The attributeType
     */
    public PersonAttributeType getAttributeType() {
        return attributeType;
    }

    /**
     * 
     * @param attributeType
     *     The attributeType
     */
    public void setAttributeType(PersonAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * 
     * @return
     *     The value
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
