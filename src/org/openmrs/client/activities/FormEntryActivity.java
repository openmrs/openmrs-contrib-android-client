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

package org.openmrs.client.activities;

import android.view.View;
import android.view.ViewGroup;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.widgets.QuestionWidget;
import org.odk.collect.android.widgets.SelectOneWidget;
import org.odk.collect.android.widgets.StringWidget;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Mapping;
import org.openmrs.client.utilities.MappingSolver;

public class FormEntryActivity extends org.odk.collect.android.activities.FormEntryActivity {

    @Override
    protected View createView(int event, boolean advancingPage) {
        View view = super.createView(event, advancingPage);
        applyFormMapping((ViewGroup) view);
        return view;
    }

    private void applyFormMapping(ViewGroup scrollView) {
        ViewGroup contentGroup = (ViewGroup) scrollView.getChildAt(0); //Root of ODK View is ScrollView
        for (int i = 0; i <= contentGroup.getChildCount(); i++) {
            View view = contentGroup.getChildAt(i);
            if (view instanceof QuestionWidget) {
                setMapping((QuestionWidget) view);
            }
            if (view instanceof ViewGroup) {
                applyFormMapping((ViewGroup) view);
            }
        }
    }

    private void setMapping(QuestionWidget qWidget) {
        String questionName = getQuestionName(qWidget.getPrompt());
        for (Mapping mapping : MappingSolver.getFormMapping(mFormName)) {
            if (questionName.contains(mapping.getQuestion())) {
                try {
                    if (qWidget instanceof StringWidget) {
                        StringWidget stringWidget = (StringWidget) qWidget;
                        stringWidget.setAnswer(mapping.getAnswer());
                    } else if (qWidget instanceof SelectOneWidget) {
                        SelectOneWidget selectOneWidget = (SelectOneWidget) qWidget;
                        selectOneWidget.setAnswer(mapping.getAnswer());
                    }
                } catch (NullPointerException ex) {
                    OpenMRS.getInstance().getOpenMRSLogger().d("Inserting mapped object failed. No such instance for selected mapping.");
                } catch (ClassCastException ex) {
                    OpenMRS.getInstance().getOpenMRSLogger().d("Inserting mapped object failed. Question isn't string widget type");
                }
            }
        }
    }

    private String getQuestionName(FormEntryPrompt prompt) {
        FormIndex formIndex = prompt.getIndex();
        TreeReference reference = formIndex.getReference();
        int refLvl = reference.getRefLevel();
        int size = reference.size();
        return reference.getName(size + refLvl);
    }
}
