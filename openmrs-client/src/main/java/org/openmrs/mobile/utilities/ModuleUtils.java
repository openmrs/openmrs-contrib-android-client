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
