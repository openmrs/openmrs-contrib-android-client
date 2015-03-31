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

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.openmrs.provider.OpenMRSInstanceProviderAPI;
import org.odk.collect.android.widgets.QuestionWidget;
import org.odk.collect.android.widgets.SelectOneWidget;
import org.odk.collect.android.widgets.StringWidget;
import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Mapping;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.MappingSolver;

public class FormEntryActivity extends org.odk.collect.android.activities.FormEntryActivity {

    private String mPatientUUID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            mPatientUUID = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
            mFormName = savedInstanceState.getString(ApplicationConstants.BundleKeys.FORM_NAME);
        } else {
            mPatientUUID = getIntent().getExtras().getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, mPatientUUID);
        outState.putString(ApplicationConstants.BundleKeys.FORM_NAME, mFormName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected View createView(int event, boolean advancingPage) {
        View view;
        if (FormEntryController.EVENT_END_OF_FORM == event) {
            FormController formController = Collect.getInstance().getFormController();
            View endView = View.inflate(this, R.layout.form_entry_end, null);
            ((TextView) endView.findViewById(org.odk.collect.android.R.id.description))
                    .setText(getString(org.odk.collect.android.R.string.save_enter_data_description,
                            formController.getFormTitle()));

            isInstanceComplete(true);

            // edittext to change the displayed name of the instance
            final EditText saveAs = (EditText) endView
                    .findViewById(org.odk.collect.android.R.id.save_name);

            // disallow carriage returns in the name
            InputFilter returnFilter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start,
                                           int end, Spanned dest, int dstart, int dend) {
                    for (int i = start; i < end; i++) {
                        if (Character.getType((source.charAt(i))) == Character.CONTROL) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            saveAs.setFilters(new InputFilter[]{returnFilter});

            String saveName = formController.getSubmissionMetadata().instanceName;
            if (saveName == null) {
                // no meta/instanceName field in the form -- see if we have a
                // name for this instance from a previous save attempt...
                if (getContentResolver().getType(getIntent().getData()) == OpenMRSInstanceProviderAPI.InstanceColumns.CONTENT_ITEM_TYPE) {
                    Uri instanceUri = getIntent().getData();
                    Cursor instance = null;
                    try {
                        instance = getContentResolver().query(instanceUri,
                                null, null, null, null);
                        if (instance.getCount() == 1) {
                            instance.moveToFirst();
                            saveName = instance
                                    .getString(instance
                                            .getColumnIndex(OpenMRSInstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
                        }
                    } finally {
                        if (instance != null) {
                            instance.close();
                        }
                    }
                }
                if (saveName == null) {
                    // last resort, default to the form title
                    saveName = formController.getFormTitle();
                }
                // present the prompt to allow user to name the form
                TextView sa = (TextView) endView
                        .findViewById(org.odk.collect.android.R.id.save_form_as);
                sa.setVisibility(View.VISIBLE);
                saveAs.setText(saveName);
                saveAs.setEnabled(true);
                saveAs.setVisibility(View.VISIBLE);
            } else {
                // if instanceName is defined in form, this is the name -- no
                // revisions
                // display only the name, not the prompt, and disable edits
                TextView sa = (TextView) endView
                        .findViewById(R.id.save_form_as);
                sa.setVisibility(View.GONE);
                saveAs.setText(saveName);
                saveAs.setEnabled(false);
                saveAs.setBackgroundColor(Color.WHITE);
                saveAs.setVisibility(View.VISIBLE);
            }

            // Create 'save' button
            ((Button) endView.findViewById(R.id.save_upload_button))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Form is marked as 'saved' here.
                            if (saveAs.getText().length() < 1) {
                                Toast.makeText(FormEntryActivity.this,
                                        R.string.save_as_error,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                saveDataToDisk(EXIT, true, saveAs.getText().toString());
                            }
                        }
                    });
            return endView;
        } else {
            view = super.createView(event, advancingPage);
            applyFormMapping((ViewGroup) view);
        }
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
        for (Mapping mapping : MappingSolver.getFormMapping(mFormName, mPatientUUID)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (RESULT_OK == resultCode) {
            setResult(resultCode, intent);
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
