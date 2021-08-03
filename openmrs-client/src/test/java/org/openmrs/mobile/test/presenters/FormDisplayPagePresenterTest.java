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

import com.openmrs.android_sdk.library.models.Page;
import com.openmrs.android_sdk.library.models.Question;
import com.openmrs.android_sdk.library.models.QuestionOptions;
import com.openmrs.android_sdk.library.models.Section;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.formdisplay.FormDisplayContract;
import org.openmrs.mobile.activities.formdisplay.FormDisplayPagePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@PrepareForTest({OpenMRS.class})
public class FormDisplayPagePresenterTest extends ACUnitTestBase {

    @Mock
    private FormDisplayContract.View.PageView mFormDisplayPageView;
    @Mock
    private Page mPage;
    @Mock
    private OpenMRS openMRS;

    private FormDisplayPagePresenter presenter;
    private String sectionLabel = "section label";
    private String questionLabel = "question label";

    @Before
    public void setUp() {
        presenter = new FormDisplayPagePresenter(mFormDisplayPageView, mPage);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
    }

    @Test
    public void subscribe_renderGroup() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "group");
        Mockito.lenient().when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        verify(mFormDisplayPageView).attachQuestionToSection(sectionLayout, questionLayout);
    }

    @Test
    public void subscribe_renderNumber() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "number");
        Mockito.lenient().when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        Question question = sectionList.get(0).getQuestions().get(0);
        verify(mFormDisplayPageView).createAndAttachNumericQuestionEditText(question, sectionLayout);
    }

    @Test
    public void subscribe_renderSelect() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "select");
        Mockito.lenient().when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

        presenter.subscribe();
        verify(mFormDisplayPageView).createSectionLayout(sectionList.get(0).getLabel());
        verify(mFormDisplayPageView).attachSectionToView(sectionLayout);
        Question question = sectionList.get(0).getQuestions().get(0);
        verify(mFormDisplayPageView).createAndAttachSelectQuestionDropdown(question, sectionLayout);
    }

    @Test
    public void subscribe_renderRadio() {
        List<Section> sectionList = getSectionList(sectionLabel, questionLabel, "radio");
        Mockito.lenient().when(mPage.getSections()).thenReturn(sectionList);

        LinearLayout sectionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createSectionLayout(sectionLabel)).thenReturn(sectionLayout);

        LinearLayout questionLayout = new LinearLayout(openMRS.getApplicationContext());
        Mockito.lenient().when(mFormDisplayPageView.createQuestionGroupLayout(questionLabel)).thenReturn(questionLayout);

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
