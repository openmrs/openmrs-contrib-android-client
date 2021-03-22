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
package org.openmrs.mobile.activities.addeditpatient

import android.content.DialogInterface
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.libraries.places.api.Places
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.utilities.ApplicationConstants

class AddEditPatientActivity : ACBaseActivity() {
    @JvmField
    var mPresenter: AddEditPatientContract.Presenter? = null
    private var addEditPatientFragment: AddEditPatientFragment? = null
    private var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_patient_info)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setTitle(R.string.action_register_patient)
        }

        // Create fragment
        addEditPatientFragment = supportFragmentManager.findFragmentById(R.id.patientInfoContentFrame) as AddEditPatientFragment?
        if (addEditPatientFragment == null) {
            addEditPatientFragment = AddEditPatientFragment.newInstance()
        }
        if (!addEditPatientFragment!!.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    addEditPatientFragment!!, R.id.patientInfoContentFrame)
        }

        //Check if bundle includes patient ID
        var patientBundle = savedInstanceState
        if (patientBundle != null) {
            patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE)
        } else {
            patientBundle = intent.extras
        }
        var patientID: String = "PatientID"
        if (patientBundle != null) {
            patientID = patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE).toString()
        }
        val countries = listOf(*resources.getStringArray(R.array.countries_array))
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = this.packageManager.getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("Package Manager", e.message.toString())
        }
        val bundle = applicationInfo!!.metaData
        val googleMapToken = bundle.getString("com.google.android.geo.API_KEY")
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, googleMapToken!!)
        }
        val placesClient = Places.createClient(this)

        // Create the mPresenter
        Log.i("ADDEDIT", "$patientID and $addEditPatientFragment")
        mPresenter = AddEditPatientPresenter(addEditPatientFragment!!, countries, patientID, placesClient, applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!mPresenter!!.isRegisteringPatient) {
            val createDialog = addEditPatientFragment!!.areFieldsNotEmpty()
            if (createDialog) {
                showInfoLostDialog()
            } else {
                if (!mPresenter!!.isRegisteringPatient) {
                    super.onBackPressed()
                }
            }
        }
    }

    /**
     * The method creates a warning dialog when the user presses back button while registering a patient
     */
    private fun showInfoLostDialog() {
        val alertDialogBuilder = AlertDialog.Builder(
                this)
        alertDialogBuilder.setTitle(R.string.dialog_title_reset_patient)
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.dialog_message_data_lost)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_stay) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                .setNegativeButton(R.string.dialog_button_leave) { dialog: DialogInterface?, id: Int ->
                    // Finish the activity
                    super.onBackPressed()
                    finish()
                }
        alertDialog = alertDialogBuilder.create()
        alertDialog!!.show()
    }

    override fun onPause() {
        if (alertDialog != null) {
            // Dismiss and clear the dialog to prevent Window leaks
            alertDialog!!.dismiss()
            alertDialog = null
        }
        super.onPause()
    }
}