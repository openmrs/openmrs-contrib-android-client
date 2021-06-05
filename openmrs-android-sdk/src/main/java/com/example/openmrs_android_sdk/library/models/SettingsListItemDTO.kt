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

package com.example.openmrs_android_sdk.library.models

class SettingsListItemDTO {
    var title: String? = null
        private set
    var desc1: String = ""
    var desc2: String = ""

    constructor(title: String, desc1: String, desc2: String) {
        this.title = title
        this.desc1 = desc1
        this.desc2 = desc2
    }

    constructor(title: String) {
        this.title = title
    }
}
