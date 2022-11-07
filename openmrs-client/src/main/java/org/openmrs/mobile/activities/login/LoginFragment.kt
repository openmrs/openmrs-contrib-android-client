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
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.community.contact.ContactUsActivity
import org.openmrs.mobile.activities.dashboard.DashboardActivity
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.bundle.CustomDialogBundle
import org.openmrs.mobile.databinding.FragmentLoginBinding
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher
import org.openmrs.mobile.services.FormListService
import org.openmrs.mobile.utilities.URLValidator
import org.openmrs.mobile.utilities.ViewUtils.isEmpty
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import java.util.ArrayList

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    private val isActivityNotNull: Boolean get() = isAdded && activity != null
    private lateinit var loginValidatorWatcher: LoginValidatorWatcher

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupObserver()
        initViewFields()
        initListeners()
        hideURLDialog()

        return binding.root
    }

    fun setupObserver() {
        viewModel.warningDialogLiveData.observe(viewLifecycleOwner, Observer { shouldShowWarning ->
            if (shouldShowWarning) showWarningDialog()
        })
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    when (result.operationType) {
                        OperationType.Login -> showLoadingAnimation()
                        OperationType.LocationsFetching -> showLocationLoadingAnimation()
                    }
                }
                is Result.Success -> {
                    when (result.data) {
                        ResultType.LoginOfflineSuccess -> {
                            ToastUtil.notify(getString(R.string.login_offline_toast_message))
                            onUserAuthenticated()
                            finishLoginActivity()
                        }
                        ResultType.LoginSuccess -> {
                            onUserAuthenticated()
                            finishLoginActivity()
                        }
                        ResultType.LoginInvalidCredentials -> {
                            showInvalidLoginOrPasswordSnackbar()
                            hideLoadingAnimation()
                        }
                        ResultType.LoginOfflineUnsupported -> {
                            ToastUtil.error(getString(R.string.offline_mode_unsupported_in_first_login))
                            hideLoadingAnimation()
                        }
                        ResultType.LoginNoInternetConnection -> {
                            ToastUtil.error(getString(R.string.no_internet_conn_dialog_message))
                            hideLoadingAnimation()
                        }
                        ResultType.LocationsFetchingLocalSuccess -> {
                            initLoginForm(viewModel.locations, viewModel.lastCorrectUrl)
                            startFormListService()
                            hideUrlLoadingAnimation()
                        }
                        ResultType.LocationsFetchingSuccess -> {
                            initLoginForm(viewModel.locations, viewModel.lastCorrectUrl)
                            hideUrlLoadingAnimation()
                        }
                        ResultType.LocationsFetchingNoInternetConnection -> {
                            ToastUtil.error(getString(R.string.no_internet_connection_message))
                            setLocationErrorOccurred(true)
                            hideUrlLoadingAnimation()
                        }
                        else -> throw IllegalStateException()
                    }
                }
                is Result.Error -> {
                    when (result.operationType) {
                        OperationType.Login -> {
                            ToastUtil.error(getString(R.string.login_error))
                            hideLoadingAnimation()
                        }
                        OperationType.LocationsFetching -> {
                            hideUrlLoadingAnimation()
                            showURLErrorSnackbar(result.throwable.message!!)
                            initLoginForm(emptyList(), viewModel.lastCorrectUrl)
                            setLocationErrorOccurred(true)
                        }
                        else -> throw IllegalStateException()
                    }
                }
                else -> throw IllegalStateException()
            }
        })
    }

    fun login(wipeDatabase: Boolean = false) {
        viewModel.login(binding.loginUsernameField.text.toString(),
                binding.loginPasswordField.text.toString(),
                binding.loginUrlField.text.toString(),
                wipeDatabase)
    }

    private fun initViewFields() = with(binding) {
        loginUrlField.setText(viewModel.lastCorrectUrl)
        loginUrlField.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.dark_grey_8x)))
        textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star))
        textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star))
        textInputLayoutUsername.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.dark_grey_8x))
        textInputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.dark_grey_8x))
    }

    private fun initListeners() = with(binding) {
        loginSyncButton.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance())
            val syncState = prefs.getBoolean("sync", true)
            PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit {
                putBoolean("sync", !syncState)
            }
            setSyncButtonState(!syncState)
        }
        loginValidatorWatcher = LoginValidatorWatcher(loginUrlField, loginUsernameField,
                loginPasswordField, locationSpinner, loginButton)
        loginValidatorWatcher.run {
            loginUrlField.onFocusChangeListener = View.OnFocusChangeListener { view: View, hasFocus: Boolean ->
                if ((!isEmpty(loginUrlField) && !hasFocus && isUrlChanged)
                        || (isUrlChanged && !hasFocus && isLocationErrorOccurred)
                        || !isUrlChanged && !hasFocus
                ) {
                    setUrl(loginUrlField.text.toString())
                    loginValidatorWatcher.isUrlChanged = false
                }
            }
        }

        loginUsernameField.onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint))
            } else if (loginUsernameField.text.toString() == "") {
                textInputLayoutUsername.hint = Html.fromHtml(getString(R.string.login_username_hint) + getString(R.string.req_star))
                textInputLayoutUsername.isHintAnimationEnabled = true
            }
        }
        loginPasswordField.onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint))
            } else if (loginPasswordField.text.toString() == "") {
                textInputLayoutPassword.hint = Html.fromHtml(getString(R.string.login_password_hint) + getString(R.string.req_star))
                textInputLayoutPassword.isHintAnimationEnabled = true
            }
        }
        loginButton.setOnClickListener {
            viewModel.showWarningOrLogin(loginUsernameField.text.toString(),
                    loginPasswordField.text.toString(),
                    loginUrlField.text.toString(),
                    false)
        }
        forgotPass.setOnClickListener { startActivity(Intent(context, ContactUsActivity::class.java)) }
        aboutUsTextView.setOnClickListener { openAboutPage() }
    }

    private fun setSyncButtonState(syncEnabled: Boolean) {
        val syncStateStringRes = if (syncEnabled) R.string.login_online else R.string.login_offline
        binding.syncLabel.text = getString(syncStateStringRes)
        binding.loginSyncButton.isChecked = syncEnabled
    }

    private fun showWarningDialog() {
        val dialog = CustomDialogBundle().apply {
            titleViewMessage = getString(R.string.warning_dialog_title)
            textViewMessage = getString(R.string.warning_lost_data_dialog)
            rightButtonText = getString(R.string.dialog_button_ok)
            rightButtonAction = CustomFragmentDialog.OnClickAction.LOGIN
            leftButtonText = getString(R.string.dialog_button_cancel)
            leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        }
        (requireActivity() as LoginActivity).createAndShowDialog(dialog, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG)
    }

    private fun showLoadingAnimation() {
        hideSoftKeys()
        binding.loginFormView.makeGone()
        binding.loginLoading.makeVisible()
    }

    private fun hideLoadingAnimation() {
        binding.loginFormView.makeVisible()
        binding.loginLoading.makeGone()
    }

    private fun showLocationLoadingAnimation() {
        binding.loginButton.isEnabled = false
        binding.locationLoadingProgressBar.makeVisible()
    }

    private fun hideUrlLoadingAnimation() {
        binding.locationLoadingProgressBar.makeGone()
        binding.loginLoading.makeGone()
    }

    private fun initLoginForm(locationsList: List<LocationEntity>, serverURL: String) = with(binding) {
        setLocationErrorOccurred(false)
        viewModel.lastCorrectUrl = serverURL
        loginUrlField.setText(serverURL)
        if (isActivityNotNull) {
            val items = getLocationStringList(locationsList)
            locationSpinner.adapter = LocationArrayAdapter(activity, items)
            loginButton.isEnabled = false
            loginLoading.makeGone()
            loginFormView.makeVisible()
            loginButton.isEnabled = locationsList.isEmpty()
        }
    }

    private fun onUserAuthenticated() {
        OpenMRS.getInstance().applicationContext.apply {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

            val formListServiceIntent = Intent(this, FormListService::class.java)
            startService(formListServiceIntent)

            viewModel.saveLocationsToDatabase(viewModel.locations, binding.locationSpinner.selectedItem.toString())
        }
    }

    private fun startFormListService() {
        if (isActivityNotNull) {
            val i = Intent(context, FormListService::class.java)
            requireActivity().startService(i)
        }
    }

    private fun showURLErrorSnackbar(message: String) {
        if (isActivityNotNull) {
            createSnackbar(message)
                    .setAction(resources.getString(R.string.snackbar_choose)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_server_list)))
                        startActivity(intent)
                    }
                    .show()
        }
    }

    private fun showInvalidLoginOrPasswordSnackbar() {
        val message = getString(R.string.invalid_login_or_password_message)
        if (isActivityNotNull) {
            createSnackbar(message)
                    .setAction(resources.getString(R.string.snackbar_edit)) {
                        binding.loginPasswordField.requestFocus()
                        binding.loginPasswordField.selectAll()
                    }
                    .show()
        }
    }

    private fun createSnackbar(message: String): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
    }

    private fun setLocationErrorOccurred(errorOccurred: Boolean) {
        loginValidatorWatcher.isLocationErrorOccurred = errorOccurred
        binding.loginButton.isEnabled = !errorOccurred
    }

    private fun getLocationStringList(locationList: List<LocationEntity>): List<String?> {
        val list: MutableList<String?> = ArrayList()
        // If spinner is at start option, append a red * to signify requirement
        list.add(Html.fromHtml(getString(R.string.login_location_select) + getString(R.string.req_star)).toString())
        locationList.forEach {
            list.add(it.display)
        }
        return list
    }

    private fun setUrl(url: String) {
        val result = URLValidator.validate(url)
        if (result.isURLValid) {
            viewModel.fetchLocations(result.url)
        } else {
            showURLErrorSnackbar(getString(R.string.invalid_URL_message))
        }
    }

    fun hideURLDialog() {
        if (viewModel.locations.isEmpty()) {
            viewModel.fetchLocations(viewModel.lastCorrectUrl)
        } else {
            initLoginForm(viewModel.locations, viewModel.lastCorrectUrl)
        }
    }

    private fun hideSoftKeys() {
        val view = activity?.currentFocus ?: View(activity)
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun openAboutPage() {
        val userGuideUrl = ApplicationConstants.USER_GUIDE
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(userGuideUrl)
        startActivity(intent)
    }

    private fun finishLoginActivity() = requireActivity().finish()

    override fun onResume() {
        super.onResume()
        val syncState = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance())
                .getBoolean("sync", true)
        setSyncButtonState(syncState)
        hideUrlLoadingAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
