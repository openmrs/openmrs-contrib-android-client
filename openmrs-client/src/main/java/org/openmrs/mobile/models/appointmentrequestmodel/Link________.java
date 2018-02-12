
package org.openmrs.mobile.models.appointmentrequestmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link________ {

    @SerializedName("rel")
    @Expose
    private String rel;
    @SerializedName("uri")
    @Expose
    private String uri;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
