/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.widget.LinearLayout;

import org.openmrs.mobile.models.retrofit.Page;
import org.openmrs.mobile.models.retrofit.Question;
import org.openmrs.mobile.models.retrofit.Section;

import java.util.List;

public class FormDisplayPagePresenter implements FormDisplayContract.Presenter.PagePresenter {

    private FormDisplayContract.View.PageView mFormDisplayPageView;
    private Page mPage;

    public FormDisplayPagePresenter(FormDisplayContract.View.PageView mFormPageView, Page page) {
        this.mFormDisplayPageView = mFormPageView;
        this.mPage = page;
        this.mFormDisplayPageView.setPresenter(this);
    }

    @Override
    public void start() {
        List<Section> sectionList = mPage.getSections();
        for (Section section:sectionList) {
            addSection(section);
        }
    }

    private void addSection(Section section) {
        LinearLayout sectionLinearLayout = mFormDisplayPageView.createSectionLayout(section.getLabel());
        mFormDisplayPageView.attachSectionToView(sectionLinearLayout);

        for (Question question:section.getQuestions()) {
            addQuestion(question,sectionLinearLayout);
        }

    }


    private void addQuestion(Question question, LinearLayout sectionLinearLayout) {
        if (question.getQuestionOptions().getRendering().equals("group")) {
            LinearLayout questionLinearLayout = mFormDisplayPageView.createQuestionGroupLayout(question.getLabel());
            mFormDisplayPageView.attachQuestionToSection(sectionLinearLayout, questionLinearLayout);

            for(Question subquestion:question.getQuestions()) {
                addQuestion(subquestion,questionLinearLayout);
            }
        }

        if(question.getQuestionOptions().getRendering().equals("number")) {
            mFormDisplayPageView.createAndAttachNumericQuestionEditText(question, sectionLinearLayout);
        }
    }

}
