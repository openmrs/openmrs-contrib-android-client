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
package org.openmrs.mobile.activities.formadmission

import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Resource
import org.openmrs.mobile.activities.BasePresenterContract
import org.openmrs.mobile.activities.BaseView

interface FormAdmissionContract {
    interface View : BaseView<Presenter> {
        fun updateProviderAdapter(providerList: List<Provider?>?)
        fun showToast(error: String?)
        fun updateLocationAdapter(locationList: List<LocationEntity?>?)
        fun enableSubmitButton(value: Boolean)
        fun quitFormEntry()
        fun updateEncounterRoleList(encounterRoleList: List<Resource?>?)
    }

    interface Presenter : BasePresenterContract {
        fun getProviders(formAdmissionFragment: FormAdmissionFragment?)
        fun updateViews(providerList: List<Provider?>?)
        fun getLocation(url: String?, formAdmissionFragment: FormAdmissionFragment?)
        fun getEncounterRoles(formAdmissionFragment: FormAdmissionFragment?)
        fun createEncounter(providerUUID: String?, locationUUID: String?, encounterRoleUUID: String?)
    }
}