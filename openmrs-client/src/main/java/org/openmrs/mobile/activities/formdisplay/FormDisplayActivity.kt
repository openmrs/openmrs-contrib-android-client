/*Circular Viewpager indicator code obtained from:
http://www.androprogrammer.com/2015/06/view-pager-with-circular-indicator.html*/
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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.openmrs.android_sdk.library.models.Page
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.VALUEREFERENCE
import com.openmrs.android_sdk.utilities.FormUtils.getForm
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.SelectOneField
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.bundle.FormFieldsWrapper
import org.openmrs.mobile.databinding.ActivityFormDisplayBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce

@AndroidEntryPoint
class FormDisplayActivity : ACBaseActivity() {
    private lateinit var binding: ActivityFormDisplayBinding

    private val viewModel: FormDisplayMainViewModel by viewModels()

    private lateinit var mDots: Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        intent.extras?.let {
            val formName = it.getString(FORM_NAME)
            supportActionBar!!.title = "$formName Form"
        }

        initViewComponents()
    }

    private fun initViewComponents() {
        var pages: List<Page>? = null
        var formFieldsWrappers: List<FormFieldsWrapper>? = null
        intent.extras?.let {
            val valueRef = it.getString(VALUEREFERENCE)!!
            val form = getForm(valueRef)
            pages = form.pages
            formFieldsWrappers = it.getSerializable(FORM_FIELDS_LIST_BUNDLE) as? List<FormFieldsWrapper>
        }

        val formPageAdapter = FormPageAdapter(supportFragmentManager, pages!!, formFieldsWrappers)
        with(binding) {
            btnNext.setOnClickListener { viewPager.currentItem = viewPager.currentItem + 1 }
            btnSubmit.setOnClickListener { submitForm() }

            viewPager.adapter = formPageAdapter
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    mDots.forEach {
                        it.setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.nonselecteditem_dot))
                    }
                    mDots[position].setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.selecteditem_dot))

                    if (position + 1 == mDots.size) {
                        btnNext.makeGone()
                        btnSubmit.makeVisible()
                    } else {
                        btnNext.makeVisible()
                        btnSubmit.makeGone()
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // No override uses
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // No override uses
                }
            })
        }

        // Set page indicators
        mDots = Array(formPageAdapter.count) {
            ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nonselecteditem_dot))
                val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                binding.viewPagerCountDots.addView(this, params)
            }
        }
        mDots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selecteditem_dot))
        if (mDots.size == 1) {
            binding.btnNext.makeGone()
            binding.btnSubmit.makeVisible()
        }
    }

    private fun submitForm() {
        val inputFields = mutableListOf<InputField>()
        val radioGroupFields = mutableListOf<SelectOneField>()

        (binding.viewPager.adapter as FormPageAdapter).registeredFragments.forEach { pos, frag ->
            val formPageFragment = frag as FormDisplayPageFragment

            if (!formPageFragment.checkInputFields()) return

            inputFields += formPageFragment.getInputFields()
            radioGroupFields += formPageFragment.getSelectOneFields()
        }

        enableSubmitButton(false)
        viewModel.submitForm(inputFields, radioGroupFields).observeOnce(this, Observer { result ->
            when (result) {
                ResultType.EncounterSubmissionSuccess -> {
                    ToastUtil.success(getString(R.string.form_submitted_successfully))
                    finish()
                }
                ResultType.EncounterSubmissionLocalSuccess -> {
                    ToastUtil.notify(getString(R.string.form_data_sync_is_off_message))
                    finish()
                }
                else -> ToastUtil.error(getString(R.string.form_data_submit_error))
            }
            enableSubmitButton(true)
        })
    }

    private fun enableSubmitButton(enabled: Boolean) {
        binding.btnSubmit.isEnabled = enabled
    }
}
