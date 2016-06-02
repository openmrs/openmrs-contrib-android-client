
package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.List;

public class Location {

    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();

    /**
     *
     * @return
     *     The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     *
     * @param results
     *     The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

}
