package org.openmrs.mobile.models.servicestypemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuditInfo {

    @SerializedName("creator")
    @Expose
    private Creator creator;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("changedBy")
    @Expose
    private ChangedBy changedBy;
    @SerializedName("dateChanged")
    @Expose
    private String dateChanged;

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ChangedBy getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(ChangedBy changedBy) {
        this.changedBy = changedBy;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

}