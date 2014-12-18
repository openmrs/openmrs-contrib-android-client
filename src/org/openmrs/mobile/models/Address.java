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

import org.openmrs.mobile.utilities.StringUtils;

import java.io.Serializable;

public class Address implements Serializable {
    private String address1;
    private String address2;
    private String postalCode;
    private String cityVillage;
    private String country;
    private String state;

    public Address() {
    }

    public Address(String address1, String address2, String postalCode, String cityVillage,
                   String country, String state) {
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.cityVillage = cityVillage;
        this.country = country;
        this.state = state;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.notNull(address1)) {
            stringBuilder.append(address1).append(StringUtils.NEW_LINE);
        }
        if (StringUtils.notNull(address2)) {
            stringBuilder.append(address2);
        }
        return stringBuilder.toString();
    }

}
