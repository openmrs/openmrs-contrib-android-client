/**
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

package org.openmrs.mobile.bundle;

public class AuthorizationManagerBundle extends FieldsBundle {
    public static final String USERNAME_KEY = "b_username";
    public static final String PASSWORD_KEY = "b_password";
    public static final String URL_KEY = "b_key";

    public String getUsername() {
        return getStringField(USERNAME_KEY);
    }

    public String getPassword() {
        return getStringField(PASSWORD_KEY);
    }

    public String getURL() {
        return getStringField(URL_KEY);
    }

}
