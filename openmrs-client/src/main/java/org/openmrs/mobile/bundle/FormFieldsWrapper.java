/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import org.openmrs.mobile.models.Answer;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.models.Section;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectOneField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FormFieldsWrapper implements Serializable, Parcelable {

    private List<InputField> inputFields;
    private List<SelectOneField> selectOneFields;

    public FormFieldsWrapper() {}

    public FormFieldsWrapper(List<InputField> inputFields, List<SelectOneField> selectOneFields) {
        this.inputFields = inputFields;
        this.selectOneFields = selectOneFields;
    }

    public List<InputField> getInputFields() {
        return inputFields;
    }

    public void setInputFields(List<InputField> inputFields) {
        this.inputFields = inputFields;
    }

    public List<SelectOneField> getSelectOneFields() {
        return selectOneFields;
    }

    public void setSelectOneFields(List<SelectOneField> selectOneFields) {
        this.selectOneFields = selectOneFields;
    }


    public static ArrayList<FormFieldsWrapper> create(Encounter encounter){
        ArrayList<FormFieldsWrapper> formFieldsWrapperList = new ArrayList<>();

        List<Page> pages = encounter.getForm().getPages();
        for (Page page : pages) {
            FormFieldsWrapper formFieldsWrapper = new FormFieldsWrapper();
            List<InputField> inputFieldList = new LinkedList<>();
            List<SelectOneField> selectOneFieldList = new LinkedList<>();
            List<Section> sections = page.getSections();
            for (Section section : sections) {
                List<Question> questions = section.getQuestions();
                for (Question questionGroup : questions) {
                    for (Question question : questionGroup.getQuestions()) {
                        if(question.getQuestionOptions().getRendering().equals("number")) {
                            String conceptUuid = question.getQuestionOptions().getConcept();
                            InputField inputField = new InputField(conceptUuid);
                            inputField.setValue(getValue(encounter.getObservations(), conceptUuid));
                            inputFieldList.add(inputField);
                        } else if (question.getQuestionOptions().getRendering().equals("select") || question.getQuestionOptions().getRendering().equals("radio")) {
                            String conceptUuid = question.getQuestionOptions().getConcept();
                            SelectOneField selectOneField =
                                    new SelectOneField(question.getQuestionOptions().getAnswers(), conceptUuid);
                            Answer chosenAnswer = new Answer();
                            chosenAnswer.setConcept(conceptUuid);
                            chosenAnswer.setLabel(getValue(encounter.getObservations(), conceptUuid).toString());
                            selectOneField.setChosenAnswer(chosenAnswer);
                            selectOneFieldList.add(selectOneField);
                        }
                    }
                }
            }
            formFieldsWrapper.setSelectOneFields(selectOneFieldList);
            formFieldsWrapper.setInputFields(inputFieldList);
            formFieldsWrapperList.add(formFieldsWrapper);
        }
        return formFieldsWrapperList;
    }

    private static Double getValue(List<Observation> observations, String conceptUuid) {
        for (Observation observation : observations) {
            if(observation.getConcept().getUuid().equals(conceptUuid)){
                return Double.valueOf(observation.getDisplayValue());
            }
        }
        return -1.0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.inputFields);
        dest.writeList(this.selectOneFields);
    }

    protected FormFieldsWrapper(Parcel in) {
        this.inputFields = new ArrayList<InputField>();
        in.readList(this.inputFields, InputField.class.getClassLoader());
        this.selectOneFields = new ArrayList<SelectOneField>();
        in.readList(this.selectOneFields, SelectOneField.class.getClassLoader());
    }

    public static final Creator<FormFieldsWrapper> CREATOR = new Creator<FormFieldsWrapper>() {
        @Override
        public FormFieldsWrapper createFromParcel(Parcel source) {
            return new FormFieldsWrapper(source);
        }

        @Override
        public FormFieldsWrapper[] newArray(int size) {
            return new FormFieldsWrapper[size];
        }
    };
}
