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

import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.ConceptDAO;

import java.io.File;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {

    private static final int ONE_KB = 1024;
    private ConceptDAO conceptDAO;

    @NonNull
    private final SettingsContract.View mSettingsView;

    @NonNull
    private final OpenMRSLogger mOpenMRSLogger;

    public SettingsPresenter(@NonNull SettingsContract.View view, @NonNull OpenMRSLogger logger ) {
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
        fillList();
        mSettingsView.setConceptsInDbText(String.valueOf(conceptDAO.getConceptsCount()));
    }

    private void fillList() {
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
        mSettingsView.applyChanges();
    }

    @Override
    public void logException(String exception) {
        mOpenMRSLogger.e(exception);
    }

    @Override
    public void updateConceptsInDBTextView() {
        mSettingsView.setConceptsInDbText(String.valueOf(conceptDAO.getConceptsCount()));
    }

}
