package org.openmrs.mobile.models.retrofit;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenID {

    @SerializedName("identifiers")
    @Expose
    private List<String> identifiers = new ArrayList<String>();

    /**
     *
     * @return
     * The identifiers
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    /**
     *
     * @param identifiers
     * The identifiers
     */
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

}

