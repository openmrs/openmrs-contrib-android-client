package org.openmrs.client.models;


import java.util.LinkedList;
import java.util.List;

public class ModuleInfo {
    public static List<ModuleInfo> getActiveModules() {
        List<ModuleInfo> moduleList = new LinkedList<ModuleInfo>();
        return moduleList;
    }

    public ModuleInfo(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    private String mName;
}
