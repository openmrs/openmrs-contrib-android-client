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
package org.openmrs.mobile.bundle

import com.openmrs.android_sdk.library.models.Answer
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.SelectOneField
import java.io.Serializable
import java.util.ArrayList
import java.util.LinkedList

class FormFieldsWrapper : Serializable {

    lateinit var inputFields: List<InputField>
    lateinit var selectOneFields: List<SelectOneField>

    companion object {
        fun create(encounter: Encounter): ArrayList<FormFieldsWrapper> {
            val formFieldsWrapperList = ArrayList<FormFieldsWrapper>()
            val pages = encounter.form!!.pages
            for (page in pages) {
                val formFieldsWrapper = FormFieldsWrapper()
                val inputFieldList: MutableList<InputField> = LinkedList()
                val selectOneFieldList: MutableList<SelectOneField> = LinkedList()
                val sections = page.sections
                for (section in sections) {
                    val questions = section.questions
                    for (questionGroup in questions) {
                        for (question in questionGroup.questions) {
                            if (question.questionOptions!!.rendering == "number") {
                                val conceptUuid = question.questionOptions!!.concept
                                val inputField = InputField(conceptUuid!!)
                                inputField.value = getValue(encounter.observations, conceptUuid)
                                inputFieldList.add(inputField)
                            } else if (question.questionOptions!!.rendering == "select" || question.questionOptions!!.rendering == "radio") {
                                val conceptUuid = question.questionOptions!!.concept
                                val selectOneField = SelectOneField(question.questionOptions!!.answers!!, conceptUuid!!)
                                val chosenAnswer = Answer()
                                chosenAnswer.concept = conceptUuid
                                chosenAnswer.label = getValue(encounter.observations, conceptUuid).toString()
                                selectOneField.chosenAnswer = chosenAnswer
                                selectOneFieldList.add(selectOneField)
                            }
                        }
                    }
                }
                formFieldsWrapper.selectOneFields = selectOneFieldList
                formFieldsWrapper.inputFields = inputFieldList
                formFieldsWrapperList.add(formFieldsWrapper)
            }
            return formFieldsWrapperList
        }

        private fun getValue(observations: List<Observation>, conceptUuid: String?): Double {
            for (observation in observations) {
                if (observation.concept!!.uuid == conceptUuid) {
                    return observation.displayValue!!.toDouble()
                }
            }
            return InputField.DEFAULT_VALUE
        }
    }
}
