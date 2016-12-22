package org.openmrs.mobile.utilities;

import org.openmrs.mobile.models.Answer;

import java.io.Serializable;
import java.util.List;

public class SelectOneField implements Serializable{

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

}
