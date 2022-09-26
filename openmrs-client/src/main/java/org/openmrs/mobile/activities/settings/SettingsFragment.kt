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
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.OpenMRSlanguage.LANGUAGE_LIST
import com.openmrs.android_sdk.utilities.ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.community.contact.ContactUsActivity
import org.openmrs.mobile.activities.logs.LogsActivity
import org.openmrs.mobile.databinding.FragmentSettingsBinding
import org.openmrs.mobile.services.ConceptDownloadService
import org.openmrs.mobile.utilities.ThemeUtils

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {
    private var broadcastReceiver: BroadcastReceiver? = null
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        addLogsInfo()
        addBuildVersionInfo()
        addPrivacyPolicyInfo()
        rateUs()
        setupContactUsButton()
        setupDarkMode()
        setupLanguageSpinner()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateConceptsInDbText()
            }
        }
        setupConceptsView()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver!!)
    }

    override fun onResume() {
        super.onResume()
        updateConceptsInDbText()
        LocalBroadcastManager.getInstance(requireActivity())
                .registerReceiver(broadcastReceiver!!, IntentFilter(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID))
    }

    private fun updateConceptsInDbText() {
        val conceptsCount = viewModel.getConceptsCount()
        if (conceptsCount == "0") {
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
        binding.conceptsCountTextView.text = conceptsCount
    }

    private fun addLogsInfo() = with(binding) {
        logsDesc1TextView.text = viewModel.logsFileName
        logsDesc2TextView.text = "${getString(R.string.settings_frag_size)} ${viewModel.logSize} kB"
        logsLayout.setOnClickListener {
            startActivity(Intent(context, LogsActivity::class.java))
        }
    }

    private fun setupConceptsView() = with(binding) {
        languageApplyButton.setOnClickListener { requireActivity().recreate() }
        conceptsDownloadButton.setOnClickListener {
            conceptsDownloadButton.isEnabled = false
            Intent(activity, ConceptDownloadService::class.java)
                    .apply { action = START_CONCEPT_DOWNLOAD_ACTION }
                    .let { activity?.startService(it) }
        }
    }

    private fun addBuildVersionInfo() {
        with(binding) {
            appNameTextView.text = getString(R.string.app_name)
            versionTextView.text = viewModel.getBuildVersionInfo(requireContext())
        }
    }

    private fun addPrivacyPolicyInfo() {
        binding.privacyPolicyLayout.setOnClickListener {
            Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(getString(R.string.url_privacy_policy)))
                    .let { startActivity(it) }
        }
    }

    private fun rateUs() {
        binding.rateUsLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, viewModel.appMarketUri)
            // Ignore Play Store back stack, on back press will take us back to our app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, viewModel.appLinkUri))
            }
        }
    }

    private fun setupContactUsButton() {
        binding.contactUsLayout.setOnClickListener { startActivity(Intent(context, ContactUsActivity::class.java)) }
    }

    private fun setupDarkMode() {
        with(binding) {
            darkModeSwitch.isChecked = ThemeUtils.isDarkModeActivated()
            darkModeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                ThemeUtils.setDarkMode(isChecked)
                requireActivity().recreate()
            }
        }
    }

    private fun setupLanguageSpinner() {
        with(binding.languageSpinner) {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, LANGUAGE_LIST)
            setSelection(viewModel.languageListPosition)
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.languageListPosition = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
