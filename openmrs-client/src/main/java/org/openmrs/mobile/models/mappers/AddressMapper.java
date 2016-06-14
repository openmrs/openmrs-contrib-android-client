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

package org.openmrs.mobile.models.mappers;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.models.retrofit.PersonAddress;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static PersonAddress parseAddress(JSONObject addressJSON) throws JSONException {
        PersonAddress address = new PersonAddress();
        address.setAddress1(addressJSON.getString("address1"));
        address.setAddress2(addressJSON.getString("address2"));
        address.setCityVillage(addressJSON.getString("cityVillage"));
        address.setCountry(addressJSON.getString("country"));
        address.setPostalCode(addressJSON.getString("postalCode"));
        address.setStateProvince(addressJSON.getString("stateProvince"));
        return address;
    }
}
