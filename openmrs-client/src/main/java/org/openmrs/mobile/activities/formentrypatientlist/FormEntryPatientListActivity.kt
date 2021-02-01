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
package org.openmrs.mobile.activities.formentrypatientlist

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity

class FormEntryPatientListActivity : ACBaseActivity() {
    private var mPresenter: FormEntryPatientListContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_form_entry_patient_list)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = 0f
            actionBar.setTitle(R.string.action_form_entry)
        }

        // Create fragment
        var formEntryPatientListFragment = supportFragmentManager.findFragmentById(R.id.formEntryPatientListContentFrame) as FormEntryPatientListFragment?
        if (formEntryPatientListFragment == null) {
            formEntryPatientListFragment = FormEntryPatientListFragment.newInstance()
        }
        if (!formEntryPatientListFragment.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    formEntryPatientListFragment, R.id.formEntryPatientListContentFrame)
        }
        // Create the presenter
        mPresenter = FormEntryPatientListPresenter(formEntryPatientListFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.form_entry_patient_list_menu, menu)
        val mFindPatientMenuItem = menu.findItem(R.id.actionSearchRemoteFormEntry)
        val findPatientView: SearchView = mFindPatientMenuItem.actionView as SearchView

        // Search function
        findPatientView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                findPatientView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                mPresenter?.setQuery(query)
                mPresenter?.updatePatientsList()
                return true
            }
        })
        return true
    }
}