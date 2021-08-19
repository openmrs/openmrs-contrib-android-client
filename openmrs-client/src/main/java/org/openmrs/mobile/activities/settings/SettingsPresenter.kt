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
package org.openmrs.mobile.activities.settings

import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.dao.ConceptRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.utilities.ApplicationConstants
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.utilities.LanguageUtils
import org.openmrs.mobile.utilities.ThemeUtils
import java.io.File

class SettingsPresenter(private val mSettingsView: SettingsContract.View, private val mOpenMRSLogger: OpenMRSLogger,
                        private var conceptRoomDAO: ConceptRoomDAO) : BasePresenter(), SettingsContract.Presenter {

    constructor(view: SettingsContract.View, logger: OpenMRSLogger) : this(view, logger, AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext).conceptRoomDAO())

    init {
        mSettingsView.setPresenter(this)
    }

    override fun subscribe() {
        updateViews()
        mSettingsView.setConceptsInDbText(conceptRoomDAO.conceptsCount.toString())
    }

    private fun updateViews() {
        var size: Long = 0
        val filename = (OpenmrsAndroid.getOpenMRSDir()
                + File.separator + mOpenMRSLogger.logFilename)
        try {
            val file = File(filename)
            size = file.length()
            size /= ONE_KB
            mOpenMRSLogger.i("File Path :${file.path} , File size: $size KB")
        } catch (e: Exception) {
            mOpenMRSLogger.w("File not found")
        }
        mSettingsView.addLogsInfo(size, filename)
        mSettingsView.addBuildVersionInfo()
        mSettingsView.addPrivacyPolicyInfo()
        mSettingsView.rateUs()
        mSettingsView.setUpContactUsButton()
        mSettingsView.setDarkMode()
        mSettingsView.chooseLanguage(ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST)
    }

    override fun logException(exception: String?) {
        mOpenMRSLogger.e(exception)
    }

    override fun updateConceptsInDBTextView() {
        mSettingsView.setConceptsInDbText(conceptRoomDAO.conceptsCount.toString())
    }

    override val isDarkModeActivated: Boolean
        get() = ThemeUtils.isDarkModeActivated()

    override fun setDarkMode(darkMode: Boolean) {
        ThemeUtils.setDarkMode(darkMode)
    }

    override var language: String?
        get() = LanguageUtils.getLanguage()
        set(lang) {
            LanguageUtils.setLanguage(lang)
        }

    override val languagePosition: Int
        get() {
            var i = 0
            while (i < ApplicationConstants.OpenMRSlanguage.LANGUAGE_CODE.size) {
                if (LanguageUtils.getLanguage()== ApplicationConstants.OpenMRSlanguage.LANGUAGE_CODE[i]) {
                    return i
                }
                i++
            }
            return 0
        }

    companion object {
        private const val ONE_KB = 1024
    }
}