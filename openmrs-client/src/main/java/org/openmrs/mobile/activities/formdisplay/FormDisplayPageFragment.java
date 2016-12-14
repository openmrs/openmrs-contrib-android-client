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

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.retrofit.Question;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.RangeEditText;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FormDisplayPageFragment extends Fragment implements FormDisplayContract.View.PageView {

    List<InputField> inputFields =new ArrayList<>();
    private FormDisplayContract.Presenter.PagePresenter mPresenter;
    private LinearLayout mParent;

    public static FormDisplayPageFragment newInstance() {
        return new FormDisplayPageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_display, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mParent = (LinearLayout) root.findViewById(R.id.sectionContainer);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
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

        Resources r = getActivity().getResources();
        float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());

        layoutParams.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));

        RangeEditText ed = new RangeEditText(getActivity());
        ed.setName(question.getLabel());
        ed.setSingleLine(true);
        if (question.getQuestionOptions().getMax()!=null)
        {   ed.setHint(question.getLabel()+" ["+question.getQuestionOptions().getMin()+"-"+
                question.getQuestionOptions().getMax()+"]");
            ed.setUpperlimit(Double.parseDouble(question.getQuestionOptions().getMax()));
            ed.setLowerlimit(Double.parseDouble(question.getQuestionOptions().getMin()));
        }
        else {
            ed.setHint(question.getLabel());
            ed.setLowerlimit(-1.0);
            ed.setUpperlimit(-1.0);
        }
        ed.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        int id= InputField.generateViewId();
        InputField field=new InputField();
        ed.setId(id);
        field.setId(id);
        field.setConcept(question.getQuestionOptions().getConcept());
        inputFields.add(field);
        sectionLinearLayout.addView(ed,layoutParams);
    }

    @Override
    public LinearLayout createQuestionGroupLayout(String questionLabel) {
        LinearLayout questionLinearLayout = new LinearLayout(getActivity());
        questionLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questionLinearLayout.setGravity(Gravity.CENTER);

        Resources r = getActivity().getResources();
        float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());

        layoutParams.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));

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
        sectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //get resources

        Resources r = getActivity().getResources();
        float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());

        layoutParams.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));

        TextView tv=new TextView(getActivity());
        tv.setText(sectionLabel);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        tv.setTextColor(ContextCompat.getColor(getActivity(),R.color.primary));
        sectionLinearLayout.addView(tv,layoutParams);

        return sectionLinearLayout;
    }

    @Override
    public List<InputField> getInputFields() {
        for (InputField field:inputFields) {
            RangeEditText ed=(RangeEditText) getActivity().findViewById(field.getId());
            if(!isEmpty(ed))
                field.setValue(Double.parseDouble(ed.getText().toString()));
        }
        return inputFields;
    }

    public boolean checkfields() {
        boolean emp=true, valid=true;
        for (InputField field:inputFields) {
            RangeEditText ed=(RangeEditText) getActivity().findViewById(field.getId());
            if(!isEmpty(ed)) {
                emp = false;

                Double inp = Double.parseDouble(ed.getText().toString());
                if (ed.getUpperlimit() != -1.0 && ed.getUpperlimit() != -1.0) {
                    if (ed.getUpperlimit() < inp || ed.getLowerlimit() > inp) {
                        ToastUtil.error("Value for " + ed.getName() + " is out of range. Value should be between "+
                                ed.getLowerlimit()+" and "+ed.getUpperlimit());
                        ed.setTextColor(ContextCompat.getColor(OpenMRS.getInstance(),R.color.red));
                        valid=false;
                    }
                }
            }
        }
        if (emp) {
            ToastUtil.error("All fields cannot be empty");
            return false;
        }
        return valid;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void setPresenter(FormDisplayContract.Presenter presenter) {
        this.mPresenter = (FormDisplayContract.Presenter.PagePresenter) presenter;
    }
}
