package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Results<T> implements Serializable {

    @SerializedName("results")
    @Expose
    private List<T> results = new ArrayList<T>();

    public List<T> getResults() {
        return results;
    }
}
