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
package org.openmrs.mobile.activities.syncedpatients

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.StringUtils.notEmpty
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity

@AndroidEntryPoint
class SyncedPatientsActivity : ACBaseActivity() {
    private var query: String? = null
    private var addPatientMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_patients)

        supportActionBar?.let {
            it.elevation = 0f
            it.setDisplayHomeAsUpEnabled(true)
            it.setTitle(R.string.action_synced_patients)
        }

        // Create fragment
        var syncedPatientsFragment = supportFragmentManager.findFragmentById(R.id.syncedPatientsContentFrame) as SyncedPatientsFragment?
        if (syncedPatientsFragment == null) {
            syncedPatientsFragment = SyncedPatientsFragment.newInstance()
        }
        if (!syncedPatientsFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    syncedPatientsFragment, R.id.syncedPatientsContentFrame)
        }
    }

    fun deletePatient(patient: Patient) {
        val syncedPatientsFragment = supportFragmentManager.findFragmentById(R.id.syncedPatientsContentFrame) as SyncedPatientsFragment?
        syncedPatientsFragment?.deletePatient(patient)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.syncbutton -> enableAddPatient(OpenmrsAndroid.getSyncState())
            R.id.actionAddPatients -> {
                val intent = Intent(this, LastViewedPatientsActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> onBackPressed()
            else -> {
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.find_locally_and_add_patients_menu, menu)

        addPatientMenuItem = menu.findItem(R.id.actionAddPatients)
        enableAddPatient(OpenmrsAndroid.getSyncState())

        val searchMenuItem = menu.findItem(R.id.actionSearchLocal)
        val searchView = menu.findItem(R.id.actionSearchLocal).actionView as SearchView
        if (notEmpty(query)) {
            searchMenuItem.expandActionView()
            searchView.setQuery(query, true)
            searchView.clearFocus()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                val syncedPatientsFragment = supportFragmentManager.findFragmentById(R.id.syncedPatientsContentFrame) as SyncedPatientsFragment?
                syncedPatientsFragment?.fetchSyncedPatients(query)
                return true
            }
        })
        return true
    }

    private fun enableAddPatient(enabled: Boolean) {
        val resId = if (enabled) R.drawable.ic_add else R.drawable.ic_add_disabled
        addPatientMenuItem?.let {
            it.isEnabled = enabled
            it.setIcon(resId)
        }
    }
}
