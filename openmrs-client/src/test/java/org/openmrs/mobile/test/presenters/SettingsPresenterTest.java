package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.mobile.activities.settings.SettingsContract;
import org.openmrs.mobile.activities.settings.SettingsPresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenMRS.class)
public class SettingsPresenterTest extends ACUnitTestBase {

    @Mock
    private SettingsContract.View view;
    @Mock
    private OpenMRSLogger logger;
    @Mock
    private OpenMRS openMRS;

    private SettingsPresenter settingsPresenter;

    @Before
    public void setUp() {
        settingsPresenter = new SettingsPresenter(view, logger);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
    }

    @Test
    public void shouldFillList_allOk() {
        String directory = "directory";
        String logFileName = "logfile";
        when(openMRS.getOpenMRSDir()).thenReturn(directory);
        when(logger.getLogFilename()).thenReturn(logFileName);
        settingsPresenter.start();
        verify(view).addLogsInfo(0, directory + File.separator + logFileName);
        verify(view).addBuildVersionInfo();
        verify(view).applyChanges();
    }

    @Test
    public void shouldPrintLogException_allOk() {
        settingsPresenter.logException(anyString());
        verify(logger).e(anyString());
    }
}
