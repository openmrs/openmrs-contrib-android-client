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
package org.openmrs.mobile.activities.dialog

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity
import org.openmrs.mobile.activities.addeditpatient.SimilarPatientsRecyclerViewAdapter
import org.openmrs.mobile.activities.login.LoginActivity
import org.openmrs.mobile.activities.login.LoginFragment
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity
import org.openmrs.mobile.activities.patientdashboard.visits.PatientVisitsFragment
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardActivity
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.bundle.CustomDialogBundle
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ToastUtil
import org.openmrs.mobile.utilities.ToastUtil.showShortToast
import java.util.ArrayList
import kotlin.system.exitProcess

/**
 * General class for creating dialog fragment instances
 */
class CustomFragmentDialog : DialogFragment() {
    public enum class OnClickAction {
        SET_URL, SHOW_URL_DIALOG, DISMISS_URL_DIALOG, DISMISS, LOGOUT, FINISH, INTERNET, UNAUTHORIZED, END_VISIT, START_VISIT, LOGIN, REGISTER_PATIENT, CANCEL_REGISTERING, DELETE_PATIENT, MULTI_DELETE_PATIENT, SELECT_LOCATION, DELETE_PROVIDER
    }

    protected lateinit var mInflater: LayoutInflater
    protected var mFieldsLayout: LinearLayout? = null
    protected var mRecyclerView: RecyclerView? = null
    protected lateinit var locationListView: ListView
    protected var mTextView: TextView? = null
    protected var mTitleTextView: TextView? = null
    private var mLeftButton: Button? = null
    private var mRightButton: Button? = null
    protected var mEditText: EditText? = null
    private var mCustomDialogBundle: CustomDialogBundle? = null
    private var itemsToDelete = ArrayList<Patient>()
    protected val mOpenMRS = OpenMRS.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCustomDialogBundle = arguments?.getSerializable(ApplicationConstants.BundleKeys.CUSTOM_DIALOG_BUNDLE) as CustomDialogBundle?
        mCustomDialogBundle?.let {
            if (it.hasLoadingBar()) {
                setStyle(STYLE_NO_TITLE, R.style.LoadingDialogTheme_DialogTheme)
            } else if (it.hasPatientList()) {
                setStyle(STYLE_NO_TITLE, R.style.SimilarPatients_DialogTheme)
            } else {
                setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
            }
        }
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mInflater = inflater
        val dialogLayout = mInflater.inflate(R.layout.fragment_dialog_layout, null, false)
        dialogLayout?.let {
            mFieldsLayout = it.findViewById(R.id.dialogForm)
            setRightButton(dialogLayout)
            setLeftButton(dialogLayout)
        }
        dialog?.setCanceledOnTouchOutside(false)
        buildDialog()
        return dialogLayout
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isDialogAvailable) {
            setBorderless()
            setOnBackListener()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isDialogAvailable) {
            if (mCustomDialogBundle!!.hasLoadingBar() || mCustomDialogBundle!!.hasProgressDialog()) {
                setProgressDialogWidth()
            } else {
                setBorderless()
            }
            setOnBackListener()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (null == manager.findFragmentByTag(tag)) {
            manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog?.setDismissMessage(null)
        }
        super.onDestroyView()
    }

     fun setOnBackListener() {
        dialog?.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK && activity?.javaClass == LoginActivity::class.java) {
                if (OpenMRS.getInstance().serverUrl == ApplicationConstants.EMPTY_STRING) {
                    OpenMRS.getInstance().openMRSLogger.d(getString(R.string.application_exit_logger_message))
                    activity?.onBackPressed()
                    dismiss()
                } else {
                    (activity?.supportFragmentManager?.findFragmentById(R.id.loginContentFrame) as LoginFragment?)?.hideURLDialog()
                    dismiss()
                }
            }
            false
        }
    }

    fun setBorderless() {
        val marginWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                TYPED_DIMENSION_VALUE.toFloat(),
                OpenMRS.getInstance().resources.displayMetrics
        ).toInt()

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val display = this.resources.displayMetrics
            val width = display.widthPixels

            val params = it.attributes
            params.width = width - 2 * marginWidth

            it.attributes = params
        }
    }

    fun setProgressDialogWidth() {
        val marginWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                TYPED_DIMENSION_VALUE.toFloat(),
                OpenMRS.getInstance().resources.displayMetrics
        ).toInt()

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val display = this.resources.displayMetrics
            val width = display.widthPixels

            val params = it.attributes
            params.width = width * 5 / 6 - 2 * marginWidth

            it.attributes = params
        }
    }

    private fun buildDialog() {
        mCustomDialogBundle?.let {
            if (null != it.titleViewMessage) {
                mTitleTextView = addTitleBar(it.titleViewMessage)
            }
            if (null != it.editTextViewMessage) {
                mEditText = addEditTextField(it.editTextViewMessage)
            }
            if (null != it.textViewMessage) {
                mTextView = addTextField(it.textViewMessage)
            }
            if (null != it.leftButtonAction) {
                setLeftButton(it.leftButtonText)
                mLeftButton?.setOnClickListener(onClickActionSolver(it.leftButtonAction))
            }
            if (null != it.rightButtonAction) {
                setRightButton(it.rightButtonText)
                mRightButton?.setOnClickListener(onClickActionSolver(it.rightButtonAction))
            }
            if (it.hasLoadingBar()) {
                addProgressBar(it.titleViewMessage)
                isCancelable = false
            }
            if (it.hasProgressDialog()) {
                addProgressBar(it.progressViewMessage)
                isCancelable = false
            }
            if (null != it.patientsList) {
                mRecyclerView = addRecycleView(it.patientsList, it.newPatient)
            }
            if (null != it.locationList) {
                addSingleChoiceItemsListView(it.locationList)
            }
            if (null != it.selectedItems) {
                itemsToDelete = it.selectedItems
            }
        }
    }

    private fun addRecycleView(patientsList: List<Patient>, newPatient: Patient): RecyclerView {
        val field = mInflater.inflate(R.layout.openmrs_recycle_view, null) as LinearLayout
        val recyclerView: RecyclerView = field.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SimilarPatientsRecyclerViewAdapter(activity, patientsList, newPatient)
        mFieldsLayout?.addView(field)
        recyclerView.setHasFixedSize(false)
        return recyclerView
    }

    private fun addSingleChoiceItemsListView(locationList: List<String>) {
        val field = mInflater.inflate(R.layout.openmrs_single_choice_list_view, null) as LinearLayout
        locationListView = field.findViewById(R.id.singleChoiceListView)
        locationListView.adapter = ArrayAdapter(requireActivity(), R.layout.row_single_checked_layout, locationList)
        locationListView.setItemChecked(locationList.indexOf(mOpenMRS.location), true)
        mFieldsLayout?.addView(field)
    }

    fun addEditTextField(defaultMessage: String?): EditText {
        val field = mInflater.inflate(R.layout.openmrs_edit_text_field, null) as LinearLayout
        val editText = field.findViewById<EditText>(R.id.openmrsEditText)
        if (null != defaultMessage) {
            editText.setText(defaultMessage)
        }
        mFieldsLayout?.addView(field)
        return editText
    }

    fun addTextField(message: String): TextView {
        val field = mInflater.inflate(R.layout.openmrs_text_view_field, null) as LinearLayout
        val textView = field.findViewById<TextView>(R.id.openmrsTextView)
        if (message.contains(getString(R.string.location_dialog_current_location))) {
            val spannableStringBuilder = SpannableStringBuilder(message)
            val styleSpan = StyleSpan(Typeface.BOLD)
            spannableStringBuilder.setSpan(styleSpan, 18, message.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            textView.text = spannableStringBuilder
        } else {
            textView.text = message
        }
        textView.isSingleLine = false

        if (null != mCustomDialogBundle?.locationList) {
            textView.textSize = 18f
        }
        mFieldsLayout?.addView(field, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        return textView
    }

    fun addTitleBar(title: String?): TextView {
        val field = mInflater.inflate(R.layout.openmrs_title_view_field, null) as LinearLayout
        val textView = field.findViewById<TextView>(R.id.openmrsTitleView)
        mCustomDialogBundle?.let {
            if (it.hasProgressDialog() || it.hasLoadingBar()) {
                mFieldsLayout?.orientation = LinearLayout.HORIZONTAL
                val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
                p.weight = 2f
                field.layoutParams = p
                field.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                textView.gravity = Gravity.CENTER
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
        textView.text = title
        textView.isSingleLine = true
        mFieldsLayout?.addView(field)
        return textView
    }

    fun setLeftButton(text: String) {
        mLeftButton?.text = text
        setViewVisible(mLeftButton, true)
    }

    fun setRightButton(text: String) {
        mRightButton?.text = text
        setViewVisible(mRightButton, true)
    }

    private fun setViewVisible(view: View?, visible: Boolean) {
        if (visible) {
            view?.visibility = View.VISIBLE
        } else {
            view?.visibility = View.GONE
        }
    }

    fun setRightButton(dialogLayout: View) {
        mRightButton = dialogLayout.findViewById(R.id.dialogFormButtonsSubmitButton)
    }

    fun setLeftButton(dialogLayout: View) {
        mLeftButton = dialogLayout.findViewById(R.id.dialogFormButtonsCancelButton)
    }

    fun addProgressBar(message: String?) {
        val progressBarLayout = mInflater.inflate(R.layout.dialog_progress, null) as RelativeLayout
        mFieldsLayout?.orientation = LinearLayout.HORIZONTAL
        val p = LinearLayout.LayoutParams(0, dp2px(resources, 100))
        p.weight = 1f
        progressBarLayout.layoutParams = p
        mFieldsLayout?.addView(progressBarLayout)
    }

    public val editTextValue: String
        get() = mEditText?.text?.toString() ?: ""

    private val isDialogAvailable: Boolean
        get() = null != this && null != dialog

    private fun onClickActionSolver(action: OnClickAction): View.OnClickListener {
        return View.OnClickListener {
            when (action) {
                OnClickAction.DISMISS_URL_DIALOG -> {
                    (activity
                            ?.supportFragmentManager
                            ?.findFragmentById(R.id.loginContentFrame) as LoginFragment?)
                            ?.hideURLDialog()
                    dismiss()
                }
                OnClickAction.LOGIN -> {
                    (activity
                            ?.supportFragmentManager
                            ?.findFragmentById(R.id.loginContentFrame) as LoginFragment?)
                            ?.login(true)
                    dismiss()
                }
                OnClickAction.DISMISS -> dismiss()
                OnClickAction.LOGOUT -> {
                    (activity as ACBaseActivity?)!!.logout()
                    dismiss()
                }
                OnClickAction.FINISH -> {
                    activity?.moveTaskToBack(true)
                    Process.killProcess(Process.myPid())
                    exitProcess(1)
                }
                OnClickAction.INTERNET -> {
                    activity?.startActivity(Intent(Settings.ACTION_SETTINGS))
                    dismiss()
                }
                OnClickAction.UNAUTHORIZED -> {
                    (activity as ACBaseActivity?)?.moveUnauthorizedUserToLoginScreen()
                    dismiss()
                }
                OnClickAction.END_VISIT -> {
                    (activity as VisitDashboardActivity?)?.mPresenter?.endVisit()
                    dismiss()
                }
                OnClickAction.START_VISIT -> {
                    doStartVisitAction()
                    dismiss()
                }
                OnClickAction.REGISTER_PATIENT -> {
                    (activity as AddEditPatientActivity?)?.mPresenter?.registerPatient()
                    dismiss()
                }
                OnClickAction.CANCEL_REGISTERING -> {
                    (activity as AddEditPatientActivity?)?.mPresenter?.finishPatientInfoActivity()
                    dismiss()
                }
                OnClickAction.DELETE_PATIENT -> {
                    val patientDashboardActivity = activity as PatientDashboardActivity?
                    patientDashboardActivity?.mPresenter?.deletePatient()
                    dismiss()
                    patientDashboardActivity?.finish()
                }
                OnClickAction.DELETE_PROVIDER -> {
                    val providerDashboardActivity = activity as ProviderDashboardActivity?
                    providerDashboardActivity?.mPresenter?.deleteProvider()
                    dismiss()
                    providerDashboardActivity?.finish()
                }
                OnClickAction.MULTI_DELETE_PATIENT -> {
                    val syncedPatientsActivity = activity as SyncedPatientsActivity?
                    for (patientItem in itemsToDelete) {
                        syncedPatientsActivity?.mPresenter?.deletePatient(patientItem)
                    }
                    dismiss()
                    syncedPatientsActivity?.finish()
                    showShortToast(requireContext(), ToastUtil.ToastType.SUCCESS, R.string.multiple_patients_deleted)
                }
                OnClickAction.SELECT_LOCATION -> {
                    mOpenMRS.location = locationListView.adapter.getItem(locationListView.checkedItemPosition).toString()
                    showShortToast(requireContext(), ToastUtil.ToastType.SUCCESS, R.string.location_successfully_updated)
                    dismiss()
                }
                else -> {
                }
            }
        }
    }

    private fun doStartVisitAction() {
        if (activity is PatientDashboardActivity) {
            val fragments = activity?.supportFragmentManager?.fragments
            var fragment: PatientVisitsFragment? = null
            fragments?.let {
                for (frag in it) {
                    if (frag is PatientVisitsFragment) {
                        fragment = frag
                        break
                    }
                }
            }
            fragment?.startVisit()
        }
    }

    companion object {
        private const val TYPED_DIMENSION_VALUE = 10

        @JvmStatic
        fun newInstance(customDialogBundle: CustomDialogBundle?): CustomFragmentDialog {
            val dialog = CustomFragmentDialog()
            dialog.retainInstance = true
            val bundle = Bundle()
            bundle.putSerializable(ApplicationConstants.BundleKeys.CUSTOM_DIALOG_BUNDLE, customDialogBundle)
            dialog.arguments = bundle
            return dialog
        }

        fun dp2px(resource: Resources, dp: Int): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resource.displayMetrics).toInt()
        }
    }
}