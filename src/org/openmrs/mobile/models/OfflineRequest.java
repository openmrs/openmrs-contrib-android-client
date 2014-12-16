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
    private String jsonRequest;
    private long objectID;
    private String actionName;

    public OfflineRequest(int method, String url, JSONObject jsonRequest, String actionName) {
        this.method = method;
        this.url = url;
        this.jsonRequest = jsonRequest.toString();
        this.actionName = actionName;
    }

    public OfflineRequest(int method, JSONObject jsonRequest, long objectID, String actionName) {
        this.method = method;
        this.jsonRequest = jsonRequest.toString();
        this.objectID = objectID;
        this.actionName = actionName;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setJsonRequest(JSONObject jsonRequest) {
        this.jsonRequest = jsonRequest.toString();
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public JSONObject getJSONRequest() {
        try {
            return new JSONObject(jsonRequest);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public long getObjectID() {
        return objectID;
    }

    public String getActionName() {
        return actionName;
    }
}
