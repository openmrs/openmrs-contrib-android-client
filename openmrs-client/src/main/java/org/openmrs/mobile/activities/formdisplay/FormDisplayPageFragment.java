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

import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.models.Answer;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.RangeEditText;
import org.openmrs.mobile.utilities.SelectOneField;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class FormDisplayPageFragment extends ACBaseFragment<FormDisplayContract.Presenter.PagePresenter> implements FormDisplayContract.View.PageView {

    private List<InputField> inputFields =new ArrayList<>();
    private List<SelectOneField> selectOneFields = new ArrayList<>();
    private LinearLayout mParent;

    public static FormDisplayPageFragment newInstance() {
        return new FormDisplayPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_display, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mParent = (LinearLayout) root.findViewById(R.id.sectionContainer);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        FormFieldsWrapper formFieldsWrapper = new FormFieldsWrapper(getInputFields(), getSelectOneFields());
        outState.putSerializable(ApplicationConstants.BundleKeys.FORM_FIELDS_BUNDLE, formFieldsWrapper);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            FormFieldsWrapper formFieldsWrapper = (FormFieldsWrapper) savedInstanceState.getSerializable(ApplicationConstants.BundleKeys.FORM_FIELDS_BUNDLE);
            inputFields = formFieldsWrapper.getInputFields();

            for(InputField field:inputFields){
                View v = getActivity().findViewById(field.getId());
                if(v != null && v instanceof DiscreteSeekBar) {
                    DiscreteSeekBar sb = (DiscreteSeekBar) v;
                    sb.setProgress(field.getValue().intValue());
                }
                if(field.isRed()){
                    RangeEditText ed = (RangeEditText) getActivity().findViewById(field.getId());
                    ed.setTextColor(ContextCompat.getColor(OpenMRS.getInstance(), R.color.red));
                }
            }
            selectOneFields = formFieldsWrapper.getSelectOneFields();
        }
    }

    @Override
    public void attachSectionToView(LinearLayout linearLayout) {
        mParent.addView(linearLayout);
    }

    @Override
    public void attachQuestionToSection(LinearLayout section, LinearLayout question) {
        section.addView(question);
    }

    @Override
    public void createAndAttachNumericQuestionEditText(Question question, LinearLayout sectionLinearLayout) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RangeEditText ed = new RangeEditText(getActivity());
        DiscreteSeekBar dsb = new DiscreteSeekBar(getActivity());
        InputField field = new InputField(question.getQuestionOptions().getConcept());
        InputField inputField = getInputField(field.getConcept());
        if (inputField != null) {
            inputField.setId(field.getId());
        } else {
            field.setConcept(question.getQuestionOptions().getConcept());
            inputFields.add(field);
        }
        sectionLinearLayout.addView(generateTextView(question.getLabel()));

        if ((question.getQuestionOptions().getMax() != null) && (!(question.getQuestionOptions().isAllowDecimal())) ){
            dsb.setMax((int) Double.parseDouble(question.getQuestionOptions().getMax()));
            dsb.setMin((int) Double.parseDouble(question.getQuestionOptions().getMin()));
            dsb.setId(field.getId());
            sectionLinearLayout.addView(dsb,layoutParams);
        }
        else {
            ed.setName(question.getLabel());
            ed.setSingleLine(true);
            ed.setHint(question.getLabel());
            ed.setLowerlimit(-1.0);
            ed.setUpperlimit(-1.0);
            ed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            if (question.getQuestionOptions().isAllowDecimal()) {
                ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                ed.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            ed.setId(field.getId());
            sectionLinearLayout.addView(ed, layoutParams);
        }

    }

    private View generateTextView(String text) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,0,0,0);
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public InputField getInputField(String concept){
        for (InputField inputField : inputFields) {
            if(concept.equals(inputField.getConcept())){
                return inputField;
            }
        }
        return null;
    }

    public SelectOneField getSelectOneField(String concept){
        for (SelectOneField selectOneField: selectOneFields) {
            if(concept.equals(selectOneField.getConcept())){
                return selectOneField;
            }
        }
        return null;
    }

    @Override
    public void createAndAttachSelectQuestionDropdown(Question question, LinearLayout sectionLinearLayout) {
        TextView textView = new TextView(getActivity());
        textView.setPadding(20,0,0,0);
        textView.setText(question.getLabel());
        Spinner spinner = (Spinner) getActivity().getLayoutInflater().inflate(R.layout.form_dropdown, null);

        LinearLayout questionLinearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams questionLinearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questionLinearLayout.setOrientation(LinearLayout.VERTICAL);
        questionLinearLayoutParams.gravity = Gravity.START;
        questionLinearLayout.setLayoutParams(questionLinearLayoutParams);

        List<String> answerLabels = new ArrayList<>();
        for (Answer answer : question.getQuestionOptions().getAnswers()) {
            answerLabels.add(answer.getLabel());
        }

        SelectOneField spinnerField = new SelectOneField(question.getQuestionOptions().getAnswers(),
                question.getQuestionOptions().getConcept());

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, answerLabels);
        spinner.setAdapter(arrayAdapter);

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        questionLinearLayout.addView(textView);
        questionLinearLayout.addView(spinner);

        sectionLinearLayout.setLayoutParams(linearLayoutParams);
        sectionLinearLayout.addView(questionLinearLayout);

        SelectOneField selectOneField = getSelectOneField(spinnerField.getConcept());
        if(selectOneField != null) {
            spinner.setSelection(selectOneField.getChosenAnswerPosition());
            setOnItemSelectedListener(spinner, selectOneField);
        } else {
            setOnItemSelectedListener(spinner, spinnerField);
            selectOneFields.add(spinnerField);
        }
    }

    private void setOnItemSelectedListener(Spinner spinner, final SelectOneField spinnerField) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerField.setAnswer(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerField.setAnswer(-1);
            }
        });
    }

    @Override
    public void createAndAttachSelectQuestionRadioButton(Question question, LinearLayout sectionLinearLayout) {
        TextView textView = new TextView(getActivity());
        textView.setPadding(20,0,0,0);
        textView.setText(question.getLabel());

        RadioGroup radioGroup = new RadioGroup(getActivity());

        
        for (Answer answer : question.getQuestionOptions().getAnswers()) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(answer.getLabel());
            radioGroup.addView(radioButton);
        }

        SelectOneField radioGroupField = new SelectOneField(question.getQuestionOptions().getAnswers(),
                question.getQuestionOptions().getConcept());

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        sectionLinearLayout.addView(textView);
        sectionLinearLayout.addView(radioGroup);

        sectionLinearLayout.setLayoutParams(linearLayoutParams);

        SelectOneField selectOneField = getSelectOneField(radioGroupField.getConcept());
        if(selectOneField != null){
            if (selectOneField.getChosenAnswerPosition() != -1) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(selectOneField.getChosenAnswerPosition());
                radioButton.setChecked(true);
            }
            setOnCheckedChangeListener(radioGroup, selectOneField);
        } else {
            setOnCheckedChangeListener(radioGroup, radioGroupField);
            selectOneFields.add(radioGroupField);
        }
    }

    private void setOnCheckedChangeListener(RadioGroup radioGroup, final SelectOneField radioGroupField) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int idx = radioGroup.indexOfChild(radioButton);
                radioGroupField.setAnswer(idx);
            }
        });
    }

    @Override
    public LinearLayout createQuestionGroupLayout(String questionLabel) {
        LinearLayout questionLinearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = getAndAdjustLinearLayoutParams(questionLinearLayout);

        TextView tv=new TextView(getActivity());
        tv.setText(questionLabel);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
        questionLinearLayout.addView(tv,layoutParams);

        return questionLinearLayout;
    }

    @Override
    public LinearLayout createSectionLayout(String sectionLabel) {
        LinearLayout sectionLinearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = getAndAdjustLinearLayoutParams(sectionLinearLayout);

        TextView tv=new TextView(getActivity());
        tv.setText(sectionLabel);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        tv.setTextColor(ContextCompat.getColor(getActivity(),R.color.primary));
        sectionLinearLayout.addView(tv,layoutParams);

        return sectionLinearLayout;
    }

    private LinearLayout.LayoutParams getAndAdjustLinearLayoutParams(LinearLayout linearLayout) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Resources r = getActivity().getResources();
        float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        layoutParams.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));

        return layoutParams;
    }

    @Override
    public List<SelectOneField> getSelectOneFields() {
        return selectOneFields;
    }

    @Override
    public List<InputField> getInputFields() {
        for (InputField field:inputFields) {
            try{
            RangeEditText ed=(RangeEditText) getActivity().findViewById(field.getId());
            if(!isEmpty(ed)){
                field.setValue(Double.parseDouble(ed.getText().toString()));
                boolean isRed = (ed.getCurrentTextColor()==ContextCompat.getColor(OpenMRS.getInstance(), R.color.red));
                field.setIsRed(isRed);
            }
            else{
                field.setValue(-1.0);
            }
        }
            catch (ClassCastException e ) {
                DiscreteSeekBar dsb = (DiscreteSeekBar) getActivity().findViewById(field.getId());
                field.setValue((double) dsb.getProgress());
            }
        }

        return inputFields;
    }

    @Override
    public void setInputFields(List<InputField> inputFields) {
        this.inputFields = inputFields;
    }

    @Override
    public void setSelectOneFields(List<SelectOneField> selectOneFields) {
        this.selectOneFields = selectOneFields;
    }

    public boolean checkInputFields() {
        boolean allEmpty = true;
        boolean valid=true;
        for (InputField field:inputFields) {
            try {
            RangeEditText ed = (RangeEditText) getActivity().findViewById(field.getId());
            if (!isEmpty(ed)) {
                allEmpty = false;
                if (ed.getText().toString().charAt(0) != '.') {
                    Double inp = Double.parseDouble(ed.getText().toString());
                    if (ed.getUpperlimit() != -1.0 && ed.getUpperlimit() != -1.0 && (ed.getUpperlimit() < inp || ed.getLowerlimit() > inp)) {
                        ed.setTextColor(ContextCompat.getColor(OpenMRS.getInstance(), R.color.red));
                        valid = false;
                    }
                }
                else {
                    ed.setTextColor(ContextCompat.getColor(OpenMRS.getInstance(), R.color.red));
                    valid = false;
                }
            }}
            catch (ClassCastException e){
                DiscreteSeekBar dsb = (DiscreteSeekBar) getActivity().findViewById(field.getId());
                if (dsb.getProgress() > dsb.getMin()) {
                    allEmpty = false;
                }
            }
        }

        for (SelectOneField radioGroupField : selectOneFields) {
            if (radioGroupField.getChosenAnswer() != null) {
                allEmpty = false;
            }
        }

        if (allEmpty) {
            ToastUtil.error("All fields cannot be empty");
            return false;
        }
        return valid;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}
