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

package org.openmrs.mobile.activities.settings;

import java.io.File;

import androidx.annotation.NonNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.ConceptDAO;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.LanguageUtils;
import org.openmrs.mobile.utilities.ThemeUtils;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {

    private static final int ONE_KB = 1024;
    @NonNull
    private final SettingsContract.View mSettingsView;
    @NonNull
    private final OpenMRSLogger mOpenMRSLogger;
    private ConceptDAO conceptDAO;

    public SettingsPresenter(@NonNull SettingsContract.View view, @NonNull OpenMRSLogger logger) {
        mSettingsView = view;
        mOpenMRSLogger = logger;
        conceptDAO = new ConceptDAO();
        view.setPresenter(this);
    }

    public SettingsPresenter(@NonNull SettingsContract.View view, @NonNull OpenMRSLogger logger, ConceptDAO conceptDAO) {
        mSettingsView = view;
        mOpenMRSLogger = logger;
        this.conceptDAO = conceptDAO;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        updateViews();
        mSettingsView.setConceptsInDbText(String.valueOf(conceptDAO.getConceptsCount()));
    }

    private void updateViews() {
        long size = 0;
        String filename = OpenMRS.getInstance().getOpenMRSDir()
                + File.separator + mOpenMRSLogger.getLogFilename();
        try {
            File file = new File(filename);
            size = file.length();
            size = size / ONE_KB;
            mOpenMRSLogger.i("File Path : " + file.getPath() + ", File size: " + size + " KB");
        } catch (Exception e) {
            mOpenMRSLogger.w("File not found");
        }

        mSettingsView.addLogsInfo(size, filename);
        mSettingsView.addBuildVersionInfo();
        mSettingsView.addPrivacyPolicyInfo();
        mSettingsView.rateUs();
        mSettingsView.setUpContactUsButton();
        mSettingsView.setDarkMode();
        mSettingsView.chooseLanguage(ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST);
    }

    @Override
    public void logException(String exception) {
        mOpenMRSLogger.e(exception);
    }

    @Override
    public void updateConceptsInDBTextView() {
        mSettingsView.setConceptsInDbText(String.valueOf(conceptDAO.getConceptsCount()));
    }

    @Override
    public boolean isDarkModeActivated() {
        return ThemeUtils.isDarkModeActivated();
    }

    @Override
    public void setDarkMode(boolean darkMode) {
        ThemeUtils.setDarkMode(darkMode);
    }

    @Override
    public String getLanguage() {
        return LanguageUtils.getLanguage();
    }

    @Override
    public void setLanguage(String lang) {
        LanguageUtils.setLanguage(lang);
    }

    @Override
    public int getLanguagePosition() {
        String lang = LanguageUtils.getLanguage();
        String[] languageList = ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST;
        int i;
        for (i = 0; i < languageList.length; i++) {
            if (lang.equals(languageList[i])) {
                return i;
            }
        }
        return 0;
    }
}
