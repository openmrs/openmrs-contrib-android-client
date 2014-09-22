package org.openmrs.client.test;

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
