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

package org.openmrs.mobile.test.presenters;

import android.widget.LinearLayout;

import com.activeandroid.Cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.formdisplay.FormDisplayContract;
import org.openmrs.mobile.activities.formdisplay.FormDisplayPagePresenter;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.models.QuestionOptions;
import org.openmrs.mobile.models.Section;
import org.openmrs.mobile.test.ACUnitTestBase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FormDisplayPagePresenterTest extends ACUnitTestBase {

    @Mock
    private FormDisplayContract.View.PageView mFormDisplayPageView;
    @Mock
    private Page mPage;

    private FormDisplayPagePresenter presenter;
    private String sectionLabel = "section label";
    private String questionLabel = "question label";

    @Before
    public void setUp() {
        presenter = new FormDisplayPagePresenter(mFormDisplayPageView, mPage);
    }

    @Test
    public  void subscribe_renderGroup() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "group");
        when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        verify(mFormDisplayPageView).attachQuestionToSection(sectionLayout, questionLayout);
    }

    @Test
    public  void subscribe_renderNumber() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "number");
        when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        Question question = sectionList.get(0).getQuestions().get(0);
        verify(mFormDisplayPageView).createAndAttachNumericQuestionEditText(question, sectionLayout);
    }

    @Test
    public  void subscribe_renderSelect() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "select");
        when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        Question question = sectionList.get(0).getQuestions().get(0);
        verify(mFormDisplayPageView).createAndAttachSelectQuestionDropdown(question, sectionLayout);
    }

    @Test
    public  void subscribe_renderRadio() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "radio");
        when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(Cache.getContext());
        when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        Question question = sectionList.get(0).getQuestions().get(0);
        verify(mFormDisplayPageView).createAndAttachSelectQuestionRadioButton(question, sectionLayout);
    }

    private List<Section> getSectionList(String sectionLabel, String questionLabel, String renderType) {
        List<Section> sectionList = new ArrayList<>();

        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setLabel(questionLabel);

        QuestionOptions options = new QuestionOptions();
        options.setRendering(renderType);

        question.setQuestionOptions(options);
        questions.add(question);

        Section section = new Section();
        section.setQuestions(questions);
        section.setLabel(sectionLabel);

        sectionList.add(section);
        return sectionList;
    }
}
