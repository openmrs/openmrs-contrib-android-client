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

import android.net.Uri;

public class FormSubmission {

    private final String mFormInstanceFilePath;
    private final Long mFormInstanceId;
    private final Uri toUpdate;

    public FormSubmission(Long formInstanceId, String formInstanceFilePath, Uri toUpdate) {
        mFormInstanceFilePath = formInstanceFilePath;
        mFormInstanceId = formInstanceId;
        this.toUpdate = toUpdate;
    }

    public String getFormInstanceFilePath() {
        return mFormInstanceFilePath;
    }

    public Long getFormInstanceId() {
        return mFormInstanceId;
    }

    public Uri getToUpdate() {
        return toUpdate;
    }
}

