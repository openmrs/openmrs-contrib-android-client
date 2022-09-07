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
package com.openmrs.android_sdk.utilities

import com.openmrs.android_sdk.library.models.Module


object ModuleUtils {
    @JvmStatic
    fun isRegistrationCore1_7orAbove(modules: List<Module>): Boolean {
        for (module in modules) {
            if ("org.openmrs.module.registrationcore" == module.packageName) {
                val versionCode = module.version!!.split(".", "\\").toTypedArray()
                val major = versionCode[0].toInt()
                val minor = if (versionCode.size >= 2) parseMinorVersion(versionCode[1]) else 0
                if (major > 1 || (major == 1 && minor >= 7)) return true
            }
        }
        return false
    }

    @JvmStatic
    private fun parseMinorVersion(version: String): Int {
        var result = version
        if (version.contains("-SNAPSHOT")) {
            result = version.replace("-SNAPSHOT", "")
        }
        return result.toInt()
    }
}
