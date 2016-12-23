/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("questionOptions")
    @Expose
    private QuestionOptions questionOptions;

    @SerializedName("questions")
    @Expose
    private List<Question> questions = new ArrayList<Question>();

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

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
     *     The questionOptions
     */
    public QuestionOptions getQuestionOptions() {
        return questionOptions;
    }

    /**
     * 
     * @param questionOptions
     *     The questionOptions
     */
    public void setQuestionOptions(QuestionOptions questionOptions) {
        this.questionOptions = questionOptions;
    }

    /**
     * 
     * @return
     *     The questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * 
     * @param questions
     *     The questions
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
