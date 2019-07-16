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

package org.openmrs.mobile.activities.formlist;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.widget.Toolbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class FormListActivity extends ACBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_form_list);

        // Create fragment
        FormListFragment formListFragment =
                (FormListFragment) getSupportFragmentManager().findFragmentById(R.id.formListContentFrame);
        if (formListFragment == null) {
            formListFragment = FormListFragment.newInstance();
        }
        if (!formListFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    formListFragment, R.id.formListContentFrame);
        }

        Bundle bundle = getIntent().getExtras();
        Long mPatientID = null;
        if(bundle != null)
        {
            mPatientID = bundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        }

        // Create the presenter
        new FormListPresenter(formListFragment, mPatientID);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
