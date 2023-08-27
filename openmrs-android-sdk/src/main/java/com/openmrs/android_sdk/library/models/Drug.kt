/* The contents of this file are subject to the OpenMRS Public License
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
package com.openmrs.android_sdk.library.models

data class Drug(
    var uuid: String?,
    var display: String?,
    var description: String?,
    var combination: Boolean?,
    var maximumDailyDose: Int?,
    var minimumDailyDose: Int?,
    var concept: DrugConcept?,
    var dosageForm: DosageForm?,
    var drugReferenceMaps: List<String>?,
    var ingredients: List<String>?,
    var name: String?,
    var retired: Boolean?,
    var strength: String?,
    var resourceVersion: String?
)