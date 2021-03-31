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
package org.openmrs.mobile.activities.dashboard

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.Target
import com.github.amlcurran.showcaseview.targets.ViewTarget
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.databinding.FragmentDashboardBinding
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ImageUtils

class DashboardFragment : ACBaseFragment<DashboardContract.Presenter>(), DashboardContract.View, View.OnClickListener {

    private var binding: FragmentDashboardBinding? = null
    private var mFindPatientView: ConstraintLayout? = null
    private var mRegistryPatientView: RelativeLayout? = null
    private var mActiveVisitsView: RelativeLayout? = null
    private var mCaptureVitalsView: RelativeLayout? = null
    private var mProviderManagementView: RelativeLayout? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val settings2 = requireActivity().getSharedPreferences(ApplicationConstants.OPENMRS_PREF_FILE, 0)
        if (settings2.getBoolean("my_first_time", true)) {
            showOverlayTutorial((binding!!.findPatientView).id, getString(R.string.dashboard_search_icon_label),
                    getString(R.string.showcase_find_patients), R.style.CustomShowcaseTheme,
                    ApplicationConstants.ShowCaseViewConstants.SHOW_FIND_PATIENT, true)
            settings2.edit().putBoolean("my_first_time", false).apply()
        }
    }

    private fun showOverlayTutorial(view: Int, title: String, content: String, styleTheme: Int,
                                    currentViewCount: Int, showTextBelow: Boolean) {
        val viewTarget: Target = ViewTarget(view, this.activity)
        val builder = ShowcaseView.Builder(this.activity)
                .setTarget(viewTarget)
                .setContentTitle(title)
                .setContentText(content)
                .hideOnTouchOutside()
                .setStyle(styleTheme)
                .setShowcaseEventListener(object : OnShowcaseEventListener {
                    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
                        when (currentViewCount) {
                            ApplicationConstants.ShowCaseViewConstants.SHOW_FIND_PATIENT -> showOverlayTutorial((binding!!.activeVisitsView).id, getString(R.string.dashboard_visits_icon_label),
                                    getString(R.string.showcase_active_visits), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS, true)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS -> showOverlayTutorial((binding!!.registryPatientView).id, getString(R.string.action_register_patient),
                                    getString(R.string.showcase_register_patient), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT, false)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT -> showOverlayTutorial((binding!!.captureVitalsView).id, getString(R.string.dashboard_forms_icon_label),
                                    getString(R.string.showcase_form_entry), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY, false)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY -> showOverlayTutorial((binding!!.dashboardProviderManagementView).id, getString(R.string.action_provider_management),
                                    getString(R.string.showcase_manage_providers), R.style.CustomShowcaseThemeExit,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_MANAGE_PROVIDERS, false)
                        }
                        showcaseView.visibility = View.GONE
                    }

                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) { //This method is intentionally left blank
                    }

                    override fun onShowcaseViewShow(showcaseView: ShowcaseView) { //This method is intentionally left blank
                    }

                    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) { //This method is intentionally left blank
                    }
                })
        if (showTextBelow) {
            builder.build()
        } else {
            builder.build().forceTextPosition(ShowcaseView.ABOVE_SHOWCASE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root = binding!!.root
        if (root != null) {
            initFragmentFields(binding!!)
            setListeners()
        }
        return root
    }

    private fun initFragmentFields(binding: FragmentDashboardBinding) {
        mFindPatientView = binding.findPatientView
        mRegistryPatientView = binding.registryPatientView
        mCaptureVitalsView = binding.captureVitalsView
        mActiveVisitsView = binding.activeVisitsView
        mProviderManagementView = binding.dashboardProviderManagementView
    }

    private fun setListeners() {
        mActiveVisitsView!!.setOnClickListener(this)
        mRegistryPatientView!!.setOnClickListener(this)
        mFindPatientView!!.setOnClickListener(this)
        mCaptureVitalsView!!.setOnClickListener(this)
        mProviderManagementView!!.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        val directionToRegister = DashboardFragmentDirections.actionDashboardFragmentToAddEditPatientActivity()
        val directionToFindPatent = DashboardFragmentDirections.actionDashboardFragmentToSyncedPatientsActivity()
        val directionToFormEntry = DashboardFragmentDirections.actionDashboardFragmentToFormEntryPatientListActivity()
        val directionToProviderManager = DashboardFragmentDirections.actionDashboardFragmentToProviderDashboardActivity()
        val directionToActiveVisits = DashboardFragmentDirections.actionDashboardFragmentToActiveVisitsActivity()
        when (v.id) {
            R.id.findPatientView -> findNavController().navigate(directionToFindPatent)
            R.id.registryPatientView -> findNavController().navigate(directionToRegister)
            R.id.captureVitalsView -> findNavController().navigate(directionToFormEntry)
            R.id.activeVisitsView -> findNavController().navigate(directionToActiveVisits)
            R.id.dashboardProviderManagementView -> findNavController().navigate(directionToProviderManager)
            else -> {
            }
        }
    }


    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}