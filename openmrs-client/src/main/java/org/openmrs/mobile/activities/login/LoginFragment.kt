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
package org.openmrs.mobile.activities.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.community.contact.ContactUsActivity
import org.openmrs.mobile.activities.dashboard.DashboardActivity
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog
import org.openmrs.mobile.api.FormListService
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.bundle.CustomDialogBundle
import org.openmrs.mobile.databases.entities.LocationEntity
import org.openmrs.mobile.databinding.FragmentLoginBinding
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.StringUtils.notEmpty
import org.openmrs.mobile.utilities.ToastUtil.ToastType
import org.openmrs.mobile.utilities.ToastUtil.showShortToast
import org.openmrs.mobile.utilities.URLValidator.validate

class LoginFragment : ACBaseFragment<LoginContract.Presenter?>(), LoginContract.View {
    private val initialUrl = OpenMRS.getInstance().serverUrl
    protected var mOpenMRS = OpenMRS.getInstance()
    private var binding: FragmentLoginBinding? = null
    private var mRootView: View? = null
    private var loginValidatorWatcher: LoginValidatorWatcher? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mRootView = binding!!.root
        initViewFields()
        initListeners()
        if (mLastCorrectURL == ApplicationConstants.EMPTY_STRING) {
            binding!!.loginUrlField.setText(OpenMRS.getInstance().serverUrl)
            mLastCorrectURL = OpenMRS.getInstance().serverUrl
        } else {
            binding!!.loginUrlField.setText(mLastCorrectURL)
        }
        hideURLDialog()
        return mRootView
    }

    private fun initListeners() {
        binding!!.loginSyncButton.setOnClickListener { view: View? ->
            val prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance())
            val syncState = prefs.getBoolean("sync", true)
            val editor = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit()
            editor.putBoolean("sync", !syncState)
            editor.apply()
            setSyncButtonState(!syncState)
        }
        loginValidatorWatcher = LoginValidatorWatcher(binding!!.loginUrlField, binding!!.loginUsernameField,
                binding!!.loginPasswordField, binding!!.locationSpinner, binding!!.loginButton)
        binding!!.loginUrlField.onFocusChangeListener = OnFocusChangeListener { view: View, hasFocus: Boolean ->
            if ((notEmpty(binding!!.loginUrlField.text.toString())
                            && !view.isFocused
                            && loginValidatorWatcher!!.isUrlChanged)
                    || (loginValidatorWatcher!!.isUrlChanged && !view.isFocused
                            && loginValidatorWatcher!!.isLocationErrorOccurred)
                    || !loginValidatorWatcher!!.isUrlChanged && !view.isFocused) {
                (activity
                        ?.getSupportFragmentManager()
                        ?.findFragmentById(R.id.loginContentFrame) as LoginFragment?)
                        ?.setUrl(binding!!.loginUrlField.text.toString())
                loginValidatorWatcher!!.isUrlChanged = false
            }
        }
        binding!!.loginUsernameField.onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding!!.textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint))
            } else if (binding!!.loginUsernameField.text.toString() == "") {
                binding!!.textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star))
                binding!!.textInputLayoutUsername.isHintAnimationEnabled = true
            }
        }
        binding!!.loginPasswordField.onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding!!.textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint))
            } else if (binding!!.loginPasswordField.text.toString() == "") {
                binding!!.textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star))
                binding!!.textInputLayoutPassword.isHintAnimationEnabled = true
            }
        }
        binding!!.loginButton.setOnClickListener { view: View? ->
            mPresenter!!.login(binding!!.loginUsernameField.text.toString(),
                    binding!!.loginPasswordField.text.toString(),
                    binding!!.loginUrlField.text.toString(),
                    initialUrl)
        }
        binding!!.forgotPass.setOnClickListener { view: View? -> startActivity(Intent(context, ContactUsActivity::class.java)) }
        binding!!.aboutUsTextView.setOnClickListener { view: View? -> openAboutPage() }
    }

    private fun initViewFields() {
        binding!!.textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star))
        binding!!.textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star))
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance())
        val syncState = prefs.getBoolean("sync", true)
        setSyncButtonState(syncState)
        hideUrlLoadingAnimation()
    }

    override fun hideSoftKeys() {
        var view = this.requireActivity().currentFocus
        if (view == null) {
            view = View(this.activity)
        }
        val inputMethodManager = this.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {
        mPresenter = presenter
    }

    fun openAboutPage() {
        val userGuideUrl = ApplicationConstants.USER_GUIDE
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(userGuideUrl)
        startActivity(intent)
    }

    private fun setSyncButtonState(syncEnabled: Boolean) {
        if (syncEnabled) {
            binding!!.syncLabel.text = getString(R.string.login_online)
        } else {
            binding!!.syncLabel.text = getString(R.string.login_offline)
        }
        binding!!.loginSyncButton.isChecked = syncEnabled
    }

    override fun showWarningDialog() {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.warning_dialog_title)
        bundle.textViewMessage = getString(R.string.warning_lost_data_dialog)
        bundle.rightButtonText = getString(R.string.dialog_button_ok)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.LOGIN
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        (this.activity as LoginActivity?)!!.createAndShowDialog(bundle, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG)
    }

    override fun showLoadingAnimation() {
        binding!!.loginFormView.visibility = View.GONE
        binding!!.loginLoading.visibility = View.VISIBLE
    }

    override fun hideLoadingAnimation() {
        binding!!.loginFormView.visibility = View.VISIBLE
        binding!!.loginLoading.visibility = View.GONE
    }

    override fun showLocationLoadingAnimation() {
        binding!!.loginButton.isEnabled = false
        binding!!.locationLoadingProgressBar.visibility = View.VISIBLE
    }

    override fun hideUrlLoadingAnimation() {
        binding!!.locationLoadingProgressBar.visibility = View.GONE
        binding!!.loginLoading.visibility = View.GONE
    }

    override fun finishLoginActivity() {
        requireActivity().finish()
    }

    override fun initLoginForm(locationsList: List<LocationEntity?>?, serverURL: String?) {
        setLocationErrorOccurred(false)
        mLastCorrectURL = serverURL!!
        binding!!.loginUrlField.setText(serverURL)
        mLocationsList = locationsList as List<LocationEntity>?
        if (isActivityNotNull) {
            val items = getLocationStringList(locationsList!!)
            val adapter = LocationArrayAdapter(this.activity, items)
            binding!!.locationSpinner.adapter = adapter
            binding!!.loginButton.isEnabled = false
            binding!!.loginLoading.visibility = View.GONE
            binding!!.loginFormView.visibility = View.VISIBLE
            if (locationsList.isEmpty()) {
                binding!!.loginButton.isEnabled = true
            } else {
                binding!!.loginButton.isEnabled = false
            }
        }
    }

    override fun userAuthenticated() {
        val intent = Intent(mOpenMRS.applicationContext, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mOpenMRS.applicationContext.startActivity(intent)
        val formListServiceIntent = Intent(mOpenMRS.applicationContext, FormListService::class.java)
        mOpenMRS.applicationContext.startService(formListServiceIntent)
        mPresenter!!.saveLocationsToDatabase(mLocationsList, binding!!.locationSpinner.selectedItem.toString())
    }

    override fun startFormListService() {
        if (isActivityNotNull) {
            val i = Intent(context, FormListService::class.java)
            requireActivity().startService(i)
        }
    }

    override fun showInvalidURLSnackbar(message: String?) {
        if (isActivityNotNull) {
            createSnackbar(message!!)
                    .setAction(resources.getString(R.string.snackbar_choose)) { view: View? ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_server_list)))
                        startActivity(intent)
                    }
                    .show()
        }
    }

    override fun showInvalidURLSnackbar(messageID: Int) {
        if (isActivityNotNull) {
            createSnackbar(getString(messageID))
                    .setAction(resources.getString(R.string.snackbar_choose)) { view: View? ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_server_list)))
                        startActivity(intent)
                    }
                    .show()
        }
    }

    override fun showInvalidLoginOrPasswordSnackbar() {
        val message = resources.getString(R.string.invalid_login_or_password_message)
        if (isActivityNotNull) {
            createSnackbar(message)
                    .setAction(resources.getString(R.string.snackbar_edit)) { view: View? ->
                        binding!!.loginPasswordField.requestFocus()
                        binding!!.loginPasswordField.selectAll()
                    }
                    .show()
        }
    }

    private fun createSnackbar(message: String): Snackbar {
        return Snackbar
                .make(mRootView!!, message, Snackbar.LENGTH_LONG)
    }

    override fun setLocationErrorOccurred(errorOccurred: Boolean) {
        loginValidatorWatcher!!.isLocationErrorOccurred = errorOccurred
        binding!!.loginButton.isEnabled = !errorOccurred
    }

    override fun showToast(message: String?, toastType: ToastType?) {
        if (activity != null) {
            showShortToast(requireActivity(), toastType!!, message!!)
        }
    }

    override fun showToast(textId: Int, toastType: ToastType?) {
        if (activity != null) {
            showShortToast(requireActivity(), toastType!!, resources.getString(textId))
        }
    }

    private fun getLocationStringList(locationList: List<LocationEntity>): List<String?> {
        val list: MutableList<String?> = ArrayList()
        //If spinner is at start option, append a red * to signify requirement
        list.add(Html.fromHtml(getString(R.string.login_location_select) + getString(R.string.req_star)).toString())
        for (i in locationList.indices) {
            list.add(locationList[i].display)
        }
        return list
    }

    fun setUrl(url: String?) {
        val result = validate(url!!)
        if (result.isURLValid) {
            mPresenter!!.loadLocations(result.url)
        } else {
            showInvalidURLSnackbar(resources.getString(R.string.invalid_URL_message))
        }
    }

    fun hideURLDialog() {
        if (mLocationsList == null) {
            mPresenter!!.loadLocations(mLastCorrectURL)
        } else {
            initLoginForm(mLocationsList!!, mLastCorrectURL)
        }
    }

    fun login() {
        mPresenter!!.authenticateUser(binding!!.loginUsernameField.text.toString(),
                binding!!.loginPasswordField.text.toString(),
                binding!!.loginUrlField.text.toString())
    }

    fun login(wipeDatabase: Boolean) {
        mPresenter!!.authenticateUser(binding!!.loginUsernameField.text.toString(),
                binding!!.loginPasswordField.text.toString(),
                binding!!.loginUrlField.text.toString(), wipeDatabase)
    }

    private val isActivityNotNull: Boolean
        get() = isAdded && activity != null

    companion object {
        private var mLastCorrectURL = ""
        private var mLocationsList: List<LocationEntity>? = null
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}