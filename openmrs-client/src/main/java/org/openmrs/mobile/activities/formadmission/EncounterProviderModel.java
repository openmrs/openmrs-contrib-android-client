package org.openmrs.mobile.activities.formadmission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncounterProviderModel {
    @SerializedName("provider")
    @Expose
    private String providerUUID;

    @SerializedName("encounterRole")
    @Expose
    private String encounterRoleUUID;

    public EncounterProviderModel(String providerUUID, String encounterRoleUUID) {
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
