/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.activities.formdisplay

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.openmrs.android_sdk.library.models.Page
import com.openmrs.android_sdk.library.models.Question
import com.openmrs.android_sdk.library.models.Section
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_FIELDS_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_PAGE_BUNDLE
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.RangeEditText
import com.openmrs.android_sdk.utilities.SelectOneField
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.bundle.FormFieldsWrapper
import org.openmrs.mobile.databinding.FragmentFormDisplayBinding
import org.openmrs.mobile.utilities.ViewUtils.isEmpty
import java.util.ArrayList
import kotlin.math.roundToInt

@AndroidEntryPoint
class FormDisplayPageFragment : BaseFragment() {
    private var _binding: FragmentFormDisplayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FormDisplayPageViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormDisplayBinding.inflate(inflater, container, false)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        createFormViews()

        return binding.root
    }

    private fun createFormViews() = viewModel.page.sections.forEach { addSection(it) }

    private fun addSection(section: Section) {
        val sectionContainer: LinearLayout = createSectionLayout(section.label!!)
        binding.sectionsParentContainer.addView(sectionContainer)
        section.questions.forEach { addQuestion(it, sectionContainer) }
    }

    private fun createSectionLayout(sectionLabel: String): LinearLayout {
        val sectionContainer = LinearLayout(activity)
        val layoutParams = getAndAdjustLinearLayoutParams(sectionContainer)
        val labelTextView = TextView(activity).apply {
            text = sectionLabel
            gravity = Gravity.CENTER_HORIZONTAL
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
        }
        sectionContainer.addView(labelTextView, layoutParams)
        return sectionContainer
    }

    private fun addQuestion(question: Question, sectionContainer: LinearLayout) {
        when (question.questionOptions!!.rendering) {
            "group" -> {
                val questionGroupContainer: LinearLayout = createQuestionGroupLayout(question.label!!)
                sectionContainer.addView(questionGroupContainer)
                question.questions.forEach { subQuestion -> addQuestion(subQuestion, questionGroupContainer) }
            }
            "number" -> createAndAttachNumericQuestionEditText(question, sectionContainer)
            "select" -> createAndAttachSelectQuestionDropdown(question, sectionContainer)
            "radio" -> createAndAttachSelectQuestionRadioButton(question, sectionContainer)
        }
    }

    private fun createQuestionGroupLayout(questionLabel: String): LinearLayout {
        val questionGroupContainer = LinearLayout(activity)
        val layoutParams = getAndAdjustLinearLayoutParams(questionGroupContainer)
        val labelTextView = TextView(activity).apply {
            text = questionLabel
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
        }
        questionGroupContainer.addView(labelTextView, layoutParams)
        return questionGroupContainer
    }

    private fun createAndAttachNumericQuestionEditText(question: Question, sectionContainer: LinearLayout) {
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sectionContainer.addView(generateTextView(question.label))

        val inputField = viewModel.getOrCreateInputField(question.questionOptions!!.concept!!)

        val options = question.questionOptions!!
        if (options.min != null && options.max != null && !options.isAllowDecimal) {
            val dsb = DiscreteSeekBar(activity).apply {
                min = options.min!!.toInt()
                max = options.max!!.toInt()
                id = inputField.id
            }
            dsb.progress = inputField.value.toInt()
            sectionContainer.addView(dsb, layoutParams)
            setOnProgressChangeListener(dsb, inputField)
        } else {
            val ed = RangeEditText(activity).apply {
                name = question.label
                hint = question.label
                isSingleLine = true
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                inputType = if (options.isAllowDecimal) {
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                } else {
                    InputType.TYPE_CLASS_NUMBER
                }
                id = inputField.id
            }
            if (inputField.hasValue) {
                ed.setText(inputField.value.toString())
                ed.setSelection(ed.length())
            }
            sectionContainer.addView(ed, layoutParams)
            setOnTextChangedListener(ed, inputField)
        }
    }

    private fun createAndAttachSelectQuestionDropdown(question: Question, sectionContainer: LinearLayout) {
        val textView = TextView(activity).apply {
            setPadding(20, 0, 0, 0)
            text = question.label
        }

        val questionLinearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
        }

        val questionLinearLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = questionLinearLayoutParams
        }

        val answerLabels = ArrayList<String?>()
        question.questionOptions!!.answers!!.forEach {
            answerLabels.add(it.label)
        }

        val spinner = layoutInflater.inflate(R.layout.form_dropdown, null) as Spinner
        spinner.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, answerLabels as List<Any?>)

        val spinnerField = SelectOneField(question.questionOptions!!.answers!!, question.questionOptions!!.concept!!)
        val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        questionLinearLayout.addView(textView)
        questionLinearLayout.addView(spinner)
        sectionContainer.layoutParams = linearLayoutParams
        sectionContainer.addView(questionLinearLayout)

        val selectOneField = viewModel.findSelectOneFieldById(spinnerField.concept!!)
        if (selectOneField != null) {
            spinner.setSelection(selectOneField.chosenAnswerPosition)
            setOnItemSelectedListener(spinner, selectOneField)
        } else {
            setOnItemSelectedListener(spinner, spinnerField)
            viewModel.selectOneFields.add(spinnerField)
        }
    }

    private fun createAndAttachSelectQuestionRadioButton(question: Question, sectionContainer: LinearLayout) {
        val textView = TextView(activity).apply {
            setPadding(20, 0, 0, 0)
            text = question.label
        }
        val radioGroup = RadioGroup(activity)
        question.questionOptions!!.answers!!.forEach {
            val radioButton = RadioButton(activity)
            radioButton.text = it.label
            radioGroup.addView(radioButton)
        }
        val radioGroupField = SelectOneField(question.questionOptions!!.answers!!, question.questionOptions!!.concept!!)
        val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sectionContainer.addView(textView)
        sectionContainer.addView(radioGroup)
        sectionContainer.layoutParams = linearLayoutParams

        val selectOneField = viewModel.findSelectOneFieldById(radioGroupField.concept!!)
        if (selectOneField != null) {
            if (selectOneField.chosenAnswerPosition != -1) {
                val radioButton = radioGroup.getChildAt(selectOneField.chosenAnswerPosition) as RadioButton
                radioButton.isChecked = true
            }
            setOnCheckedChangeListener(radioGroup, selectOneField)
        } else {
            setOnCheckedChangeListener(radioGroup, radioGroupField)
            viewModel.selectOneFields.add(radioGroupField)
        }
    }

    private fun setOnProgressChangeListener(dsb: DiscreteSeekBar, inputField: InputField) {
        dsb.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                inputField.value = value.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                // No override uses
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                // No override uses
            }

        })
    }

    private fun setOnTextChangedListener(et: EditText, inputField: InputField) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No override uses
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No override uses
            }

            override fun afterTextChanged(s: Editable?) {
                inputField.value = if (!s.isNullOrEmpty()) s.toString().toDouble() else InputField.DEFAULT_VALUE
            }

        })
    }

    private fun setOnItemSelectedListener(spinner: Spinner, spinnerField: SelectOneField) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                spinnerField.setAnswer(i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                spinnerField.setAnswer(-1)
            }
        }
    }

    private fun setOnCheckedChangeListener(radioGroup: RadioGroup, radioGroupField: SelectOneField) {
        radioGroup.setOnCheckedChangeListener { radioGroup1: RadioGroup, i: Int ->
            val radioButton = radioGroup1.findViewById<View>(i)
            val idx = radioGroup1.indexOfChild(radioButton)
            radioGroupField.setAnswer(idx)
        }
    }

    private fun getAndAdjustLinearLayoutParams(linearLayout: LinearLayout): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        val margin = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                .roundToInt()
        layoutParams.setMargins(margin, margin, margin, margin)
        return layoutParams
    }

    private fun generateTextView(text: String?): View {
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(10, 0, 0, 0)
        val textView = TextView(activity)
        textView.text = text
        textView.layoutParams = layoutParams
        return textView
    }

    fun checkInputFields(): Boolean {
        var allEmpty = true
        var valid = true
        for (field in viewModel.inputFields) {
            try {
                val ed: RangeEditText = requireActivity().findViewById(field.id)
                if (!isEmpty(ed)) {
                    allEmpty = false
                    if (!ed.validInput || ed.outOfRange) {
                        ed.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        valid = false
                    }
                }
            } catch (e: ClassCastException) {
                val dsb: DiscreteSeekBar = requireActivity().findViewById(field.id)
                if (dsb.progress > dsb.min) allEmpty = false
            }
        }
        for (radioGroupField in viewModel.selectOneFields) {
            if (radioGroupField.chosenAnswer != null) allEmpty = false
        }

        if (allEmpty) ToastUtil.error(getString(R.string.all_fields_empty_error_message))
        else if (!valid) ToastUtil.error(getString(R.string.invalid_inputs))

        return !allEmpty && valid
    }

    fun getInputFields() = viewModel.inputFields

    fun getSelectOneFields() = viewModel.selectOneFields

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(page: Page, formFieldsWrapper: FormFieldsWrapper?) = FormDisplayPageFragment().apply {
            arguments = bundleOf(
                    FORM_PAGE_BUNDLE to page,
                    FORM_FIELDS_BUNDLE to formFieldsWrapper
            )
        }
    }
}
