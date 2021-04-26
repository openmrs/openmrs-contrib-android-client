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

import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.application.OpenMRSLogger
import org.openmrs.mobile.dao.ConceptRoomDAO
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.utilities.ApplicationConstants
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
        val filename = (OpenMRS.getInstance().openMRSDir
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
        mSettingsView.setDarkMode(ApplicationConstants.OpenMRSThemes.THEME_LIST)
        mSettingsView.chooseLanguage(ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST)
    }

    override fun logException(exception: String?) {
        mOpenMRSLogger.e(exception)
    }

    override fun updateConceptsInDBTextView() {
        mSettingsView.setConceptsInDbText(conceptRoomDAO.conceptsCount.toString())
    }

    override var language: String?
        get() = LanguageUtils.getLanguage()
        set(lang) {
            LanguageUtils.setLanguage(lang)
        }

    override val languagePosition: Int
        get() {
            val lang = LanguageUtils.getLanguage()
            val languageList = ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST
            var i = 0
            while (i < languageList.size) {
                if (lang == languageList[i]) {
                    return i
                }
                i++
            }
            return 0
        }
    override var theme: String
        get() = ThemeUtils.getTheme()
        set(theme) {
            ThemeUtils.setTheme(theme)
        }
    override val themePosition: Int
        get() {
            val theme = ThemeUtils.getTheme()
            val themeList = ApplicationConstants.OpenMRSThemes.THEME_LIST
            var i = 0
            while (i < themeList.size) {
                if (theme == themeList[i]) {
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