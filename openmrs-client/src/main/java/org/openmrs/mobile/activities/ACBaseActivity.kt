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
package org.openmrs.mobile.activities

import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Preconditions
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.community.contact.AboutActivity
import org.openmrs.mobile.activities.community.contact.ContactUsActivity
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog
import org.openmrs.mobile.activities.introduction.SplashActivity
import org.openmrs.mobile.activities.login.LoginActivity
import org.openmrs.mobile.activities.settings.SettingsActivity
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.bundle.CustomDialogBundle
import org.openmrs.mobile.dao.LocationDAO
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.databases.entities.LocationEntity
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.net.AuthorizationManager
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ForceClose
import org.openmrs.mobile.utilities.LanguageUtils.getLanguage
import org.openmrs.mobile.utilities.NetworkUtils.hasNetwork
import org.openmrs.mobile.utilities.NetworkUtils.isOnline
import org.openmrs.mobile.utilities.ThemeUtils.isDarkModeActivated
import org.openmrs.mobile.utilities.ToastUtil
import org.openmrs.mobile.utilities.ToastUtil.setAppVisible
import org.openmrs.mobile.utilities.ToastUtil.showShortToast
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.*

abstract class ACBaseActivity : AppCompatActivity() {
    @JvmField
    protected val mOpenMRS = OpenMRS.getInstance()
    protected val mOpenMRSLogger = mOpenMRS.openMRSLogger
    private var mFragmentManager: FragmentManager? = null
    private var mAuthorizationManager: AuthorizationManager? = null
    private var mCustomFragmentDialog: CustomFragmentDialog? = null
    @JvmField
    protected var mSnackbar: Snackbar? = null
    private var mSyncbutton: MenuItem? = null
    private var locationList: MutableList<String?>? = null
    private var mIntentFilter: IntentFilter? = null
    private var alertDialog: AlertDialog? = null
    private val mPasswordChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showCredentialChangedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ForceClose(this))
        setupTheme()
        setupLanguage()
        mFragmentManager = supportFragmentManager
        mAuthorizationManager = AuthorizationManager()
        locationList = ArrayList()
        val extras = intent.extras
        if (extras != null) {
            val flag = extras.getBoolean(ApplicationConstants.FLAG)
            val errorReport = extras.getString(ApplicationConstants.ERROR)
            if (flag) {
                showAppCrashDialog(errorReport)
            }
        }
        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction(ApplicationConstants.BroadcastActions.AUTHENTICATION_CHECK_BROADCAST_ACTION)
    }

    override fun onResume() {
        super.onResume()
        setupTheme()
        setupLanguage()
        invalidateOptionsMenu()
        if (this !is LoginActivity && !mAuthorizationManager!!.isUserLoggedIn
                && this !is ContactUsActivity && this !is SplashActivity) {
            mAuthorizationManager!!.moveToLoginActivity()
        }
        registerReceiver(mPasswordChangedReceiver, mIntentFilter)
        setAppVisible(true)
    }

    override fun onPause() {
        unregisterReceiver(mPasswordChangedReceiver)
        super.onPause()
        setAppVisible(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.basic_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        mSyncbutton = menu.findItem(R.id.syncbutton)
        val logoutMenuItem = menu.findItem(R.id.actionLogout)
        if (logoutMenuItem != null) {
            logoutMenuItem.title = getString(R.string.action_logout) + " " + mOpenMRS.username
        }
        if (mSyncbutton != null) {
            val syncState = isOnline()
            setSyncButtonState(syncState)
        }
        return true
    }

    private fun setSyncButtonState(syncState: Boolean) {
        if (syncState) {
            mSyncbutton!!.setIcon(R.drawable.ic_sync_on)
        } else {
            mSyncbutton!!.setIcon(R.drawable.ic_sync_off)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ApplicationConstants.RequestCodes.START_SETTINGS_REQ_CODE) {
            recreate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.actionSettings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), ApplicationConstants.RequestCodes.START_SETTINGS_REQ_CODE)
                true
            }
            R.id.actionContact -> {
                startActivity(Intent(this, ContactUsActivity::class.java))
                true
            }
            R.id.actionSearchLocal -> true
            R.id.actionLogout -> {
                showLogoutDialog()
                true
            }
            R.id.syncbutton -> {
                val syncState = OpenMRS.getInstance().syncState
                when {
                    syncState -> {
                        OpenMRS.getInstance().syncState = false
                        setSyncButtonState(false)
                        showNoInternetConnectionSnackbar()
                        showShortToast(applicationContext, ToastUtil.ToastType.NOTICE, R.string.disconn_server)
                    }
                    hasNetwork() -> {
                        OpenMRS.getInstance().syncState = true
                        setSyncButtonState(true)
                        val intent = Intent("org.openmrs.mobile.intent.action.SYNC_PATIENTS")
                        applicationContext.sendBroadcast(intent)
                        showShortToast(applicationContext, ToastUtil.ToastType.NOTICE, R.string.reconn_server)
                        if (mSnackbar != null) {
                            mSnackbar!!.dismiss()
                        }
                        showShortToast(applicationContext, ToastUtil.ToastType.SUCCESS, R.string.connected_to_server_message)
                    }
                    else -> {
                        showNoInternetConnectionSnackbar()
                    }
                }
                true
            }
            R.id.actionLocation -> {
                if (locationList!!.isNotEmpty()) {
                    locationList!!.clear()
                }
                val observableList = LocationDAO().locations
                observableList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getLocationList())
                true
            }
            R.id.actionAbout -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getLocationList(): Observer<List<LocationEntity>> {
        return object : Observer<List<LocationEntity>> {
            override fun onCompleted() {
                showLocationDialog(locationList)
            }

            override fun onError(e: Throwable) {
                mOpenMRSLogger.e(e.message)
            }

            override fun onNext(locations: List<LocationEntity>) {
                for (locationItem in locations) {
                    locationList!!.add(locationItem.name)
                }
            }
        }
    }

    open fun showNoInternetConnectionSnackbar() {
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.no_internet_connection_message), Snackbar.LENGTH_INDEFINITE)
        val sbView = mSnackbar!!.view
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        mSnackbar!!.show()
    }

    fun logout() {
        mOpenMRS.clearUserPreferencesData()
        mAuthorizationManager!!.moveToLoginActivity()
        showShortToast(applicationContext, ToastUtil.ToastType.SUCCESS, R.string.logout_success)
        AppDatabase.getDatabase(applicationContext).close()
    }

    private fun showCredentialChangedDialog() {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.credentials_changed_dialog_title)
        bundle.textViewMessage = getString(R.string.credentials_changed_dialog_message)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.LOGOUT
        bundle.rightButtonText = getString(R.string.ok)
        mCustomFragmentDialog = CustomFragmentDialog.newInstance(bundle)
        mCustomFragmentDialog?.isCancelable = false
        mCustomFragmentDialog?.retainInstance = true
        mCustomFragmentDialog?.show(mFragmentManager!!, ApplicationConstants.DialogTAG.CREDENTIAL_CHANGED_DIALOG_TAG)
    }

    private fun showLogoutDialog() {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.logout_dialog_title)
        bundle.textViewMessage = getString(R.string.logout_dialog_message)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.LOGOUT
        bundle.rightButtonText = getString(R.string.logout_dialog_button)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG)
    }

    fun showStartVisitImpossibleDialog(title: CharSequence?) {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.start_visit_unsuccessful_dialog_title)
        bundle.textViewMessage = getString(R.string.start_visit_unsuccessful_dialog_message, title)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.rightButtonText = getString(R.string.dialog_button_ok)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_IMPOSSIBLE_DIALOG_TAG)
    }

    fun showStartVisitDialog(title: CharSequence?) {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.start_visit_dialog_title)
        bundle.textViewMessage = getString(R.string.start_visit_dialog_message, title)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.START_VISIT
        bundle.rightButtonText = getString(R.string.dialog_button_confirm)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.START_VISIT_DIALOG_TAG)
    }

    fun showDeletePatientDialog() {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.action_delete_patient)
        bundle.textViewMessage = getString(R.string.delete_patient_dialog_message)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.DELETE_PATIENT
        bundle.rightButtonText = getString(R.string.dialog_button_confirm)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.DELETE_PATIENT_DIALOG_TAG)
    }

    fun showDeleteProviderDialog() {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.dialog_title_are_you_sure)
        bundle.textViewMessage = getString(R.string.dialog_provider_retired)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.DELETE_PROVIDER
        bundle.rightButtonText = getString(R.string.dialog_button_confirm)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.DELETE_PROVIDER_DIALOG_TAG)
    }

    fun showMultiDeletePatientDialog(selectedItems: ArrayList<Patient?>?) {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.delete_multiple_patients)
        bundle.textViewMessage = getString(R.string.delete_multiple_patients_dialog_message)
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.MULTI_DELETE_PATIENT
        bundle.rightButtonText = getString(R.string.dialog_button_confirm)
        bundle.selectedItems = selectedItems
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.MULTI_DELETE_PATIENT_DIALOG_TAG)
    }

    private fun showLocationDialog(locationList: List<String?>?) {
        val bundle = CustomDialogBundle()
        bundle.titleViewMessage = getString(R.string.location_dialog_title)
        bundle.textViewMessage = getString(R.string.location_dialog_current_location) + " " + mOpenMRS.location
        bundle.locationList = locationList
        bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.SELECT_LOCATION
        bundle.rightButtonText = getString(R.string.dialog_button_select_location)
        bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
        bundle.leftButtonText = getString(R.string.dialog_button_cancel)
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOCATION_DIALOG_TAG)
    }

    fun createAndShowDialog(bundle: CustomDialogBundle?, tag: String?) {
        val instance = CustomFragmentDialog.newInstance(bundle)
        instance.show(mFragmentManager!!, tag)
    }

    fun moveUnauthorizedUserToLoginScreen() {
        AppDatabase.getDatabase(applicationContext).close()
        mOpenMRS.clearUserPreferencesData()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    }

    fun showProgressDialog(dialogMessageId: Int) {
        showProgressDialog(getString(dialogMessageId))
    }

    fun dismissCustomFragmentDialog() {
        if (mCustomFragmentDialog != null) {
            mCustomFragmentDialog!!.dismiss()
        }
    }

    private fun showProgressDialog(dialogMessage: String?) {
        val bundle = CustomDialogBundle()
        bundle.setProgressDialog(true)
        bundle.titleViewMessage = dialogMessage
        mCustomFragmentDialog = CustomFragmentDialog.newInstance(bundle)
        mCustomFragmentDialog?.isCancelable = false
        mCustomFragmentDialog?.retainInstance = true
        mCustomFragmentDialog?.show(mFragmentManager!!, dialogMessage)
    }

    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment, frameId: Int) {
        Preconditions.checkNotNull(fragmentManager)
        Preconditions.checkNotNull(fragment)
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }

    private fun showAppCrashDialog(error: String?) {
        val alertDialogBuilder = AlertDialog.Builder(
                this)
        alertDialogBuilder.setTitle(R.string.crash_dialog_title)
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.crash_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.crash_dialog_positive_button) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                .setNegativeButton(R.string.crash_dialog_negative_button) { dialog: DialogInterface?, id: Int -> finishAffinity() }
                .setNeutralButton(R.string.crash_dialog_neutral_button) { dialog: DialogInterface?, id: Int ->
                    val filename = (OpenMRS.getInstance().openMRSDir
                            + File.separator + mOpenMRSLogger.logFilename)
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_SUBJECT, R.string.error_email_subject_app_crashed)
                    email.putExtra(Intent.EXTRA_TEXT, error)
                    email.putExtra(Intent.EXTRA_STREAM, Uri.parse(ApplicationConstants.URI_FILE + filename))
                    //need this to prompts email client only
                    email.type = ApplicationConstants.MESSAGE_RFC_822
                    startActivity(Intent.createChooser(email, getString(R.string.choose_a_email_client)))
                }
        alertDialog = alertDialogBuilder.create()
        alertDialog!!.show()
    }

    fun setupTheme() {
        if (isDarkModeActivated()) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun setupLanguage() {
        val lang = getLanguage()
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    override fun onDestroy() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.cancel()
        }
        super.onDestroy()
    }
}