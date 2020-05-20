package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncounterProvider extends Resource
{
    @SerializedName("provider")
    @Expose
    private Resource provider;

    @SerializedName("encounterRole")
    @Expose
    private Resource encounterRole;

    public Resource getProvider() {
        return provider;
    }

    public Resource getEncounterRole() {
        return encounterRole;
    }
}
