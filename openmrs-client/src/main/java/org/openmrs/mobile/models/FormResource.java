/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Table(name = "formresource")
public class FormResource extends Model implements Serializable{

    @Column(name="name")
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("resources")
    @Expose
    private List<FormResource> resources = new ArrayList<FormResource>();

    @Column(name = "resources")
    private String resourcelist;

    @Column(name = "valueReference")
    @SerializedName("valueReference")
    @Expose
    private String valueReference;

    @Column(name = "uuid")
    @SerializedName("uuid")
    @Expose
    private String uuid;

    @Column(name = "display")
    @SerializedName("display")
    @Expose
    private String display;

    @Column(name = "links")
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private Type formResourceListType = new TypeToken<List<FormResource>>(){}.getType();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getDisplay() {
        return display;
    }


    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FormResource> getResources() {
        return resources;
    }

    public void setResources(List<FormResource> resources) {
        this.resources = resources;
    }

    public void setResourcelist() {
        this.resourcelist = gson.toJson(resources, formResourceListType);
    }

    public List<FormResource> getResourceList() {
        return gson.fromJson(this.resourcelist, formResourceListType);
    }

}
