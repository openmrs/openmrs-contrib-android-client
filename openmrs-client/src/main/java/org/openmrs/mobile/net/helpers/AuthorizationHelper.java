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

package org.openmrs.mobile.net.helpers;

import org.openmrs.mobile.activities.LoginActivity;
import org.openmrs.mobile.bundle.AuthorizationManagerBundle;
import org.openmrs.mobile.listeners.authorization.LoginListener;

public final class AuthorizationHelper {

    private AuthorizationHelper() {
    }

    public static AuthorizationManagerBundle createBundle(String username, String password, String url) {
        AuthorizationManagerBundle bundle = new AuthorizationManagerBundle();
        bundle.putStringField(AuthorizationManagerBundle.USERNAME_KEY, username);
        bundle.putStringField(AuthorizationManagerBundle.PASSWORD_KEY, password);
        bundle.putStringField(AuthorizationManagerBundle.URL_KEY, url);
        return bundle;
    }

    public static LoginListener createLoginListener(AuthorizationManagerBundle bundle, LoginActivity caller) {
        return new LoginListener(bundle, caller);
    }
}
