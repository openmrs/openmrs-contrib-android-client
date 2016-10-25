/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models.retrofit;

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
