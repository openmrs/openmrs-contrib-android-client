package org.openmrs.client.models;


import java.util.LinkedList;
import java.util.List;

public class ModuleInfo {
    public static List<ModuleInfo> getActiveModules() {
        return new LinkedList<ModuleInfo>();
    }

    public ModuleInfo(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    private String mName;
}
