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

package org.openmrs.mobile.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import org.openmrs.mobile.models.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectOneField implements Serializable, Parcelable {

    private String concept = null;
    private Answer chosenAnswer = null;
    private List<Answer> answerList;

    public SelectOneField(List<Answer> answerList, String concept) {
        this.answerList = answerList;
        this.concept = concept;
    }

    public void setAnswer(int answerPosition) {
        if (answerPosition < answerList.size()) {
            chosenAnswer = answerList.get(answerPosition);
        }
        if (answerPosition == -1) {
            chosenAnswer = null;
        }
    }

    public void setChosenAnswer(Answer chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public Answer getChosenAnswer() {
        return chosenAnswer;
    }

    public String getConcept() {
        return concept;
    }

    public int getChosenAnswerPosition() {
        return answerList.indexOf(chosenAnswer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.concept);
        dest.writeSerializable(this.chosenAnswer);
        dest.writeList(this.answerList);
    }

    protected SelectOneField(Parcel in) {
        this.concept = in.readString();
        this.chosenAnswer = (Answer) in.readSerializable();
        this.answerList = new ArrayList<Answer>();
        in.readList(this.answerList, Answer.class.getClassLoader());
    }

    public static final Parcelable.Creator<SelectOneField> CREATOR = new Parcelable.Creator<SelectOneField>() {
        @Override
        public SelectOneField createFromParcel(Parcel source) {
            return new SelectOneField(source);
        }

        @Override
        public SelectOneField[] newArray(int size) {
            return new SelectOneField[size];
        }
    };
}
