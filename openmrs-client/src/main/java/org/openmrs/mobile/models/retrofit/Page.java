
package org.openmrs.mobile.models.retrofit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Page implements Serializable {

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("sections")
    @Expose
    private List<Section> sections = new ArrayList<Section>();

    /**
     * 
     * @return
     *     The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 
     * @return
     *     The sections
     */
    public List<Section> getSections() {
        return sections;
    }

    /**
     * 
     * @param sections
     *     The sections
     */
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

}
