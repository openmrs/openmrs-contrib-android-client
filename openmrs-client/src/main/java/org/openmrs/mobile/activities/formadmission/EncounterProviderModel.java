package org.openmrs.mobile.activities.formadmission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncounterProviderModel
{
    @SerializedName("provider")
    @Expose
    String provider;

    @SerializedName("encounterRole")
    @Expose
    String encounterRole;
}
