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

package org.openmrs.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class OfflineRequest {
    private int method;
    private String url;
    private String request;
    private long objectID;
    private String objectUUID;
    private String actionName;
    private String wrapperName;

    public OfflineRequest(int method, String url, JSONObject jsonRequest, long objectID, String actionName) {
        this.method = method;
        this.url = url;
        this.request = jsonRequest.toString();
        this.objectID = objectID;
        this.actionName = actionName;
    }

    public OfflineRequest(int method, JSONObject jsonRequest, long objectID, String actionName) {
        this.method = method;
        this.request = jsonRequest.toString();
        this.objectID = objectID;
        this.actionName = actionName;
    }

    public OfflineRequest(String wrapperName, String url, String instancePath, String patientUUID, Long visitId) {
        this.wrapperName = wrapperName;
        this.url = url;
        this.request = instancePath;
        this.objectUUID = patientUUID;
        this.objectID = visitId;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public JSONObject getJSONRequest() {
        try {
            return new JSONObject(request);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public String getRequest() {
        return request;
    }

    public long getObjectID() {
        return objectID;
    }

    public String getActionName() {
        return actionName;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public String getObjectUUID() {
        return objectUUID;
    }

}
