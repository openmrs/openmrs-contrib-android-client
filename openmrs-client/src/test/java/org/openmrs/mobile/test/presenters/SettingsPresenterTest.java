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

package org.openmrs.mobile.test.presenters;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.settings.SettingsContract;
import org.openmrs.mobile.activities.settings.SettingsPresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.File;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest(OpenMRS.class)
public class SettingsPresenterTest extends ACUnitTestBase {

    @Mock
    private SettingsContract.View view;
    @Mock
    private OpenMRSLogger logger;
    @Mock
    private OpenMRS openMRS;

    private SettingsPresenter settingsPresenter;

    private TextView logsTV;
    @Before
    public void setUp() {
        settingsPresenter = new SettingsPresenter(view, logger,logsTV);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
    }

    @Test
    public void shouldFillList_allOk() {
        String directory = "directory";
        String logFileName = "logfile";
        when(openMRS.getOpenMRSDir()).thenReturn(directory);
        when(logger.getLogFilename()).thenReturn(logFileName);
        settingsPresenter.subscribe();
        verify(view).addLogsInfo(0, directory + File.separator + logFileName, logsTV);
        verify(view).addBuildVersionInfo();
        verify(view).applyChanges();
    }

    @Test
    public void shouldPrintLogException_allOk() {
        settingsPresenter.logException(anyString());
        verify(logger).e(anyString());
    }
}
