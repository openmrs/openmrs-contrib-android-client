
package org.openmrs.mobile.models.retrofit;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionOptions implements Serializable {

    @SerializedName("rendering")
    @Expose
    private String rendering;

    @SerializedName("concept")
    @Expose
    private String concept;

    @SerializedName("max")
    @Expose
    private String max;

    @SerializedName("min")
    @Expose
    private String min;

    /**
     * 
     * @return
     *     The rendering
     */
    public String getRendering() {
        return rendering;
    }

    /**
     * 
     * @param rendering
     *     The rendering
     */
    public void setRendering(String rendering) {
        this.rendering = rendering;
    }

    /**
     * 
     * @return
     *     The concept
     */
    public String getConcept() {
        return concept;
    }

    /**
     * 
     * @param concept
     *     The concept
     */
    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

}
