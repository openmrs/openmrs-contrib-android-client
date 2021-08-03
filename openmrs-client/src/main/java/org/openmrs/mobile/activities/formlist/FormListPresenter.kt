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

import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.utilities.FormService.getFormResourceList
import com.openmrs.android_sdk.utilities.StringUtils.isBlank
import org.openmrs.mobile.activities.BasePresenter

class FormListPresenter : BasePresenter, FormListContract.Presenter {
    private var view: FormListContract.View
    private var patientId: Long
    private var formResourceList: MutableList<FormResourceEntity>? = null
    private var encounterDAO: EncounterDAO

    constructor(view: FormListContract.View, patientId: Long) {
        this.view = view
        this.view.setPresenter(this)
        this.patientId = patientId
        encounterDAO = EncounterDAO()
    }

    constructor(view: FormListContract.View, patientId: Long, encounterDAO: EncounterDAO) {
        this.view = view
        this.view.setPresenter(this)
        this.patientId = patientId
        this.encounterDAO = encounterDAO
    }

    override fun subscribe() {
        loadFormResourceList()
    }

    override fun loadFormResourceList() {
        formResourceList = ArrayList()
        val allFormResourcesList = getFormResourceList()
        for (formResourceEntity in allFormResourcesList) {
            val valueRef = formResourceEntity.resources
            var valueRefString: String? = null
            for (resource in valueRef) {
                if (resource.name == "json") {
                    valueRefString = resource.valueReference
                }
            }
            if (!isBlank(valueRefString)) {
                (formResourceList as ArrayList<FormResourceEntity>).add(formResourceEntity)
            } else {
                if (view.formCreate(formResourceEntity.uuid, formResourceEntity.name!!.toLowerCase())!!) {
                    (formResourceList as ArrayList<FormResourceEntity>).add(formResourceEntity)
                }
            }
        }
        val size = (formResourceList as ArrayList<FormResourceEntity>).size
        formsStringArray = arrayOfNulls(size)
        for (i in 0 until size) {
            formsStringArray!![i] = (formResourceList as ArrayList<FormResourceEntity>).get(i).name
        }
        view.showFormList(formsStringArray)
    }

    override fun listItemClicked(position: Int, formName: String?) {
        val valueRef = formResourceList!![position].resources
        var valueRefString: String? = null
        for (resource in valueRef) {
            if (resource.name == "json") {
                valueRefString = resource.valueReference
            }
        }
        val encounterName = formsStringArray!![position]!!.split("\\(".toRegex()).toTypedArray()[0].trim { it <= ' ' }
        val encType = encounterDAO.getEncounterTypeByFormName(encounterName)
        if (encType != null) {
            val encounterType = encType.uuid
            if (EncounterType.ADMISSION == encounterName) {
                view.startAdmissionFormActivity(formName, patientId, encounterType)
            } else {
                view.startFormDisplayActivity(formName, patientId, valueRefString, encounterType)
            }
        } else {
            view.showError(formName)
        }
    }

    companion object {
        private var formsStringArray: Array<String?>? = null
    }
}