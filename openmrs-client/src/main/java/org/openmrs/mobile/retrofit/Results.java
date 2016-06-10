package org.openmrs.mobile.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Results<T> {

    @SerializedName("results")
    @Expose
    private List<T> results = new ArrayList<T>();

    public List<T> getResults() {
        return results;
    }
}
