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

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.community.contact.ContactUsActivity
import org.openmrs.mobile.activities.logs.LogsActivity
import org.openmrs.mobile.databinding.FragmentSettingsBinding
import org.openmrs.mobile.services.ConceptDownloadService
import com.openmrs.android_sdk.utilities.ApplicationConstants

class SettingsFragment : ACBaseFragment<SettingsContract.Presenter>(), SettingsContract.View {
    private var broadcastReceiver: BroadcastReceiver? = null
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mPresenter?.updateConceptsInDBTextView()
            }
        }
        setUpConceptsView()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this.requireActivity()).unregisterReceiver(broadcastReceiver!!)
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.updateConceptsInDBTextView()
        LocalBroadcastManager.getInstance(this.requireActivity())
                .registerReceiver(broadcastReceiver!!, IntentFilter(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID))
    }

    override fun setConceptsInDbText(text: String?) {
        if (text == "0") {
            binding.conceptsDownloadButton.isEnabled = true
            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
            val customSnackBarView = layoutInflater.inflate(R.layout.snackbar, null)
            val snackBarLayout = snackbar.view as SnackbarLayout
            snackBarLayout.setPadding(0, 0, 0, 0)
            val dismissButton = customSnackBarView.findViewById<TextView>(R.id.snackbar_action_button)
            val typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/Roboto/Roboto-Medium.ttf")
            dismissButton.typeface = typeface
            dismissButton.setOnClickListener { snackbar.dismiss() }
            snackBarLayout.addView(customSnackBarView, 0)
            snackbar.show()
        } else {
            binding.conceptsDownloadButton.isEnabled = false
        }
        binding.conceptsCountTextView.text = text
    }

    override fun addLogsInfo(logSize: Long, logFilename: String?) {
        binding.logsDesc1TextView.text = logFilename
        binding.logsDesc2TextView.text = "${requireContext().getString(R.string.settings_frag_size)} $logSize kB"
        binding.logsLayout.setOnClickListener { view: View ->
            val i = Intent(view.context, LogsActivity::class.java)
            startActivity(i)
        }
    }

    override fun setUpConceptsView() {
        with(binding) {
            languageApplyButton.setOnClickListener { requireActivity().recreate() }
            conceptsDownloadButton.setOnClickListener {
                conceptsDownloadButton.isEnabled = false
                val startIntent = Intent(activity, ConceptDownloadService::class.java)
                startIntent.action = ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION
                activity?.startService(startIntent)
            }
        }
    }

    override fun addBuildVersionInfo() {
        var versionName = ""
        var buildVersion = 0
        val packageManager = this.requireActivity().packageManager
        val packageName = this.requireActivity().packageName
        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            buildVersion = ai.metaData.getInt("buildVersion")
        } catch (e: PackageManager.NameNotFoundException) {
            mPresenter?.logException("Failed to load meta-data, NameNotFound: ${e.message}")
        } catch (e: NullPointerException) {
            mPresenter?.logException("Failed to load meta-data, NullPointer: ${e.message}")
        }
        with(binding) {
            appNameTextView.text = resources.getString(R.string.app_name)
            versionTextView.text = versionName + context?.getString(R.string.frag_settings_build) + buildVersion
        }
    }

    override fun addPrivacyPolicyInfo() {
        binding.privacyPolicyLayout.setOnClickListener { view: View ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(view.context.getString(R.string.url_privacy_policy))
            startActivity(i)
        }
    }

    override fun rateUs() {
        binding.rateUsLayout.setOnClickListener {
            val uri = Uri.parse("market://details?id=${ApplicationConstants.PACKAGE_NAME}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            // Ignore Playstore backstack, on back press will take us back to our app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=${ApplicationConstants.PACKAGE_NAME}")))
            }
        }
    }

    override fun setUpContactUsButton() {
        binding.contactUsLayout.setOnClickListener { view: View -> startActivity(Intent(view.context, ContactUsActivity::class.java)) }
    }

    override fun setDarkMode() {
        with(binding) {
            darkModeSwitch.isChecked = mPresenter?.isDarkModeActivated ?: false
            darkModeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                mPresenter?.setDarkMode(isChecked)
                requireActivity().recreate()
            }
        }
    }

    override fun chooseLanguage(languageList: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, languageList)
        with(binding) {
            languageSpinner.adapter = adapter
            languageSpinner.setSelection(mPresenter?.languagePosition ?: 0)
            languageSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    mPresenter?.language = ApplicationConstants.OpenMRSlanguage.LANGUAGE_CODE[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}