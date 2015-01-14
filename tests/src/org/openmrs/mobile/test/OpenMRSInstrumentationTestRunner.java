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

package org.openmrs.mobile.test;

import android.os.Bundle;

import java.io.File;

import pl.polidea.instrumentation.PolideaInstrumentationTestRunner;

public class OpenMRSInstrumentationTestRunner extends PolideaInstrumentationTestRunner {

    @Override
    public void onCreate(Bundle arguments) {
        String appFilesDir = getTargetContext().getFilesDir().getAbsolutePath();
        arguments.putString("junitOutputDirectory", appFilesDir + File.separator + "junit");
        super.onCreate(arguments);
    }
}
