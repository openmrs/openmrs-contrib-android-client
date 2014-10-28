package org.openmrs.client.models;

import java.io.Serializable;

public class Form implements Serializable {
    private String displayName;
    private String formID;
    private String formPath;
    private String submissionUri;

    public Form(String displayName, String formID, String formPath, String submissionUri) {
        this.displayName = displayName;
        this.formID = formID;
        this.formPath = formPath;
        this.submissionUri = submissionUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getFormPath() {
        return formPath;
    }

    public void setFormPath(String formPath) {
        this.formPath = formPath;
    }

    public String getSubmissionUri() {
        return submissionUri;
    }

    public void setSubmissionUri(String submissionUri) {
        this.submissionUri = submissionUri;
    }
}
