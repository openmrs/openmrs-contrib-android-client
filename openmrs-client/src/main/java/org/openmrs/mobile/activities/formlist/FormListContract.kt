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
package org.openmrs.mobile.activities.formlist

import org.openmrs.mobile.activities.BaseView
import org.openmrs.mobile.activities.BasePresenterContract

interface FormListContract {
    interface View : BaseView<Presenter?> {
        fun showFormList(forms: Array<String?>?)
        fun startFormDisplayActivity(formName: String?, patientId: Long?, valueRefString: String?, encounterType: String?)
        fun showError(formName: String?)
        fun formCreate(uuid: String?, formName: String?): Boolean?
        fun startAdmissionFormActivity(formName: String?, patientId: Long?, encounterType: String?)
    }

    interface Presenter : BasePresenterContract {
        fun loadFormResourceList()
        fun listItemClicked(position: Int, formName: String?)
    }
}