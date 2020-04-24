package org.openmrs.mobile.utilities

import org.openmrs.mobile.models.Module


object ModuleUtils {

    @JvmStatic
    fun isRegistrationCore1_7orAbove(modules: List<Module>): Boolean {
        for (module in modules) {
            if ("org.openmrs.module.registrationcore" == module.packageName) {
                val versions = module.version!!.split("\\.").toTypedArray()
                if (versions.size >= 2 && Integer.valueOf(versions[0]) >= 1 && parseVersion(versions[1]) >= 7) {
                    return true
                }
            }
        }
        return false
    }

    @JvmStatic
    private fun parseVersion(version: String): Int {
        var result = version
        if (version.contains("-SNAPSHOT")) {
            result = version.replace("-SNAPSHOT", "")
        }
        return Integer.valueOf(result)
    }
}
