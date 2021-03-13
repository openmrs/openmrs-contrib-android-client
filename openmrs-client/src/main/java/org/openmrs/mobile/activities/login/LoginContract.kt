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

import org.openmrs.mobile.activities.BasePresenterContract
import org.openmrs.mobile.activities.BaseView
import org.openmrs.mobile.databases.entities.LocationEntity
import org.openmrs.mobile.utilities.ToastUtil.ToastType

interface LoginContract {
    interface View : BaseView<Presenter?> {
        fun hideSoftKeys()
        override fun setPresenter(presenter: Presenter?)
        fun showWarningDialog()
        fun showLoadingAnimation()
        fun hideLoadingAnimation()
        fun showLocationLoadingAnimation()
        fun hideUrlLoadingAnimation()
        fun finishLoginActivity()
        fun showInvalidURLSnackbar(message: String?)
        fun showInvalidURLSnackbar(messageID: Int)
        fun showInvalidLoginOrPasswordSnackbar()
        fun setLocationErrorOccurred(errorOccurred: Boolean)
        fun showToast(message: String?, toastType: ToastType?)
        fun showToast(textId: Int, toastType: ToastType?)
        fun initLoginForm(locationList: List<LocationEntity?>?, url: String?)
        fun userAuthenticated()
        fun startFormListService()
    }

    interface Presenter : BasePresenterContract {
        fun authenticateUser(username: String?, password: String?, url: String?)
        fun authenticateUser(username: String?, password: String?, url: String?, wipeDatabase: Boolean)
        fun login(username: String?, password: String?, url: String?, oldUrl: String?)
        fun saveLocationsToDatabase(locationList: List<LocationEntity?>?, selectedLocation: String?)
        fun loadLocations(url: String?)
    }
}