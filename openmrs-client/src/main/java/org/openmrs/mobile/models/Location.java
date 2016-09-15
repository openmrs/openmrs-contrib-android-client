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

import org.openmrs.mobile.models.retrofit.PersonAddress;
import org.openmrs.mobile.models.retrofit.Resource;

public class Location extends Resource{
    private Long id;
    private String name;
    private String description;
    private PersonAddress address;
    private String parentLocationUuid;

    public Location(String uuid, String display) {
        this.uuid = uuid;
        this.display = display;
    }

    public Location() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setAddress(PersonAddress address) {
        this.address = address;
    }

    public PersonAddress getAddress() {
        return address;
    }

    public void setParentLocationUuid(String parentLocationUuid) {
        this.parentLocationUuid = parentLocationUuid;
    }

    public String getParentLocationUuid() {
        return parentLocationUuid;
    }

}
