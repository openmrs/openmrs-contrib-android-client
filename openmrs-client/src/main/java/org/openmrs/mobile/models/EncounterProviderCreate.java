package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncounterProviderCreate {
    @SerializedName("provider")
    @Expose
    private String providerUUID;

    @SerializedName("encounterRole")
    @Expose
    private String encounterRoleUUID;

    public EncounterProviderCreate(String providerUUID, String encounterRoleUUID) {
        this.providerUUID = providerUUID;
        this.encounterRoleUUID = encounterRoleUUID;
    }

    public String getProviderUUID() {
        return providerUUID;
    }

    public void setProviderUUID(String providerUUID) {
        this.providerUUID = providerUUID;
    }

    public String getEncounterRoleUUID() {
        return encounterRoleUUID;
    }

    public void setEncounterRoleUUID(String encounterRoleUUID) {
        this.encounterRoleUUID = encounterRoleUUID;
    }
}
