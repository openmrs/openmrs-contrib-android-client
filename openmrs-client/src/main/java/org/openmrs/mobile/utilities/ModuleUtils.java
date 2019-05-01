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

package org.openmrs.mobile.utilities;

import org.openmrs.mobile.models.Module;

import java.util.List;

public class ModuleUtils {

    public static boolean isRegistrationCore1_7orAbove(List<Module> modules) {
        for (Module module : modules) {
            if ("org.openmrs.module.registrationcore".equals(module.getPackageName())) {
                String[] versions = module.getVersion().split("\\.");
                if (versions.length >= 2 && Integer.valueOf(versions[0]) >= 1 && parseVersion(versions[1]) >= 7) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int parseVersion(String version) {
        String result = version;
        if(version.contains("-SNAPSHOT")){
            result = version.replace("-SNAPSHOT", "");
        }
        return Integer.valueOf(result);
    }
}
