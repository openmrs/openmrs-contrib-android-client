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

import com.google.gson.annotations.Expose;

public class Location extends Resource {

    @Expose
    private Long id;
    @Expose
    private String name;
    @Expose
    private String parentLocationUuid;
    @Expose
    private String description;
    @Expose
    private String address2;
    @Expose
    private String address1;
    @Expose
    private String cityVillage;
    @Expose
    private String stateProvince;
    @Expose
    private String country;
    @Expose
    private String postalCode;

    public Location() {
    }

    public Location(String display) {
        this.display = display;
    }

    public Location(Long id,
                    String name,
                    String parentLocationUuid,
                    String description,
                    PersonAddress address) {
        this.id = id;
        this.name = name;
        this.parentLocationUuid = parentLocationUuid;
        this.description = description;
        this.address2 = address.getAddress2();
        this.address1 = address.getAddress1();
        this.cityVillage = address.getCityVillage();
        this.stateProvince = address.getStateProvince();
        this.country = address.getCountry();
        this.postalCode = address.getPostalCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentLocationUuid() {
        return parentLocationUuid;
    }

    public void setParentLocationUuid(String parentLocationUuid) {
        this.parentLocationUuid = parentLocationUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
