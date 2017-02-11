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


package org.openmrs.mobile.activities.logs;


import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogsPresenter extends BasePresenter implements LogsContract.Presenter {

    @NonNull
    private final OpenMRSLogger mOpenMRSLogger;

    @NonNull
    private final LogsContract.View mLogsView;

    public  LogsPresenter (@NonNull LogsContract.View view, @NonNull OpenMRSLogger logger ){
        mOpenMRSLogger = logger;
        mLogsView = view ;
        view.setPresenter(this);
    }

    @Override
    public void subscribe(){
        String logsText = getLogs();
        mLogsView.attachLogsToTextView(logsText);
        mLogsView.fabCopyAll(logsText);
    }

    public String getLogs() {
        String textLogs = "";
        String filename = OpenMRS.getInstance().getOpenMRSDir()
                + File.separator + mOpenMRSLogger.getLogFilename();
        try {
            File myFile = new File(filename);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow;
            while ((aDataRow = myReader.readLine()) != null) {
                textLogs += aDataRow;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textLogs;
    }

}
