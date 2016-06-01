
package org.openmrs.mobile.models.patientreg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttributeType {

    @SerializedName("uuid")
    @Expose
    private String uuid;

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

}
