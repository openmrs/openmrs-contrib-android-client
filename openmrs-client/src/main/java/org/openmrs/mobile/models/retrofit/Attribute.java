
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

    @SerializedName("attributeType")
    @Expose
    private AttributeType attributeType;
    @SerializedName("value")
    @Expose
    private String value;

    /**
     * 
     * @return
     *     The attributeType
     */
    public AttributeType getAttributeType() {
        return attributeType;
    }

    /**
     * 
     * @param attributeType
     *     The attributeType
     */
    public void setAttributeType(AttributeType attributeType) {
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
