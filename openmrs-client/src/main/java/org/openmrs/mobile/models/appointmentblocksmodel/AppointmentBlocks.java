
package org.openmrs.mobile.models.appointmentblocksmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentBlocks {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}
