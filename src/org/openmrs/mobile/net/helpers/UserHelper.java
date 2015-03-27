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

import org.openmrs.mobile.listeners.user.FullInformationListener;
import org.openmrs.mobile.listeners.user.UserInformationListener;
import org.openmrs.mobile.net.UserManager;

public final class UserHelper {

    private UserHelper() {
    }

    public static FullInformationListener createFullInformationListener(String userUUID) {
        return new FullInformationListener(userUUID);
    }

    public static UserInformationListener createUserInformationListener(String username, UserManager userManager) {
        return new UserInformationListener(username, userManager);
    }
}
