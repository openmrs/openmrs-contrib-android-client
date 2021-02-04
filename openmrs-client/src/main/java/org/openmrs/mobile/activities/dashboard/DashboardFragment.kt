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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.Target
import com.github.amlcurran.showcaseview.targets.ViewTarget
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.activevisits.ActiveVisitsActivity
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListActivity
import org.openmrs.mobile.activities.providermanagerdashboard.ProviderManagerDashboardActivity
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsActivity
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ImageUtils
import org.openmrs.mobile.utilities.ThemeUtils
import org.openmrs.mobile.utilities.ToastUtil

class DashboardFragment : Fragment(), View.OnClickListener {

    private var mFindPatientButton: ImageView? = null
    private var mRegistryPatientButton: ImageView? = null
    private var mActiveVisitsButton: ImageView? = null
    private var mCaptureVitalsButton: ImageView? = null
    private var mProviderManagementButton: ImageView? = null
    private var mFindPatientView: RelativeLayout? = null
    private var mRegistryPatientView: RelativeLayout? = null
    private var mActiveVisitsView: RelativeLayout? = null
    private var mCaptureVitalsView: RelativeLayout? = null
    private var mProviderManagementView: RelativeLayout? = null
    private var mBitmapCache: SparseArray<Bitmap?>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val settings2 = requireActivity().getSharedPreferences(ApplicationConstants.OPENMRS_PREF_FILE, 0)
        if (settings2.getBoolean("my_first_time", true)) {
            showOverlayTutorial(R.id.findPatientView, getString(R.string.dashboard_search_icon_label),
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
                            ApplicationConstants.ShowCaseViewConstants.SHOW_FIND_PATIENT -> showOverlayTutorial(R.id.activeVisitsView, getString(R.string.dashboard_visits_icon_label),
                                    getString(R.string.showcase_active_visits), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS, true)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_ACTIVE_VISITS -> showOverlayTutorial(R.id.registryPatientView, getString(R.string.action_register_patient),
                                    getString(R.string.showcase_register_patient), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT, false)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_REGISTER_PATIENT -> showOverlayTutorial(R.id.captureVitalsView, getString(R.string.dashboard_forms_icon_label),
                                    getString(R.string.showcase_form_entry), R.style.CustomShowcaseTheme,
                                    ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY, false)
                            ApplicationConstants.ShowCaseViewConstants.SHOW_FORM_ENTRY -> showOverlayTutorial(R.id.dashboardProviderManagementView, getString(R.string.action_provider_management),
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
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        if (root != null) {
            initFragmentFields(root)
            setListeners()
        }
        return root
    }

    private fun initFragmentFields(root: View) {
        mFindPatientButton = root.findViewById(R.id.findPatientButton)
        mRegistryPatientButton = root.findViewById(R.id.registryPatientButton)
        mActiveVisitsButton = root.findViewById(R.id.activeVisitsButton)
        mCaptureVitalsButton = root.findViewById(R.id.captureVitalsButton)
        mProviderManagementButton = root.findViewById(R.id.dashboardProviderManagementButton)
        mFindPatientView = root.findViewById(R.id.findPatientView)
        mRegistryPatientView = root.findViewById(R.id.registryPatientView)
        mCaptureVitalsView = root.findViewById(R.id.captureVitalsView)
        mActiveVisitsView = root.findViewById(R.id.activeVisitsView)
        mProviderManagementView = root.findViewById(R.id.dashboardProviderManagementView)
    }

    private fun setListeners() {
        mActiveVisitsView!!.setOnClickListener(this)
        mRegistryPatientView!!.setOnClickListener(this)
        mFindPatientView!!.setOnClickListener(this)
        mCaptureVitalsView!!.setOnClickListener(this)
        mProviderManagementView!!.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindDrawableResources()
    }

    override fun onResume() {
        super.onResume()
        bindDrawableResources()
    }

    /**
     * Binds drawable resources to all dashboard buttons
     * Initially called by this view's presenter
     */
    fun bindDrawableResources() {
        bindDrawableResource(mFindPatientButton, R.drawable.ico_search)
        bindDrawableResource(mRegistryPatientButton, R.drawable.ico_registry)
        bindDrawableResource(mActiveVisitsButton, R.drawable.ico_visits)
        bindDrawableResource(mCaptureVitalsButton, R.drawable.ico_vitals)
        if (ThemeUtils.isDarkModeActivated()) {
            changeColorOfDashboardIcons()
        }
    }

    /**
     * Binds drawable resource to ImageView
     *
     * @param imageView ImageView to bind resource to
     * @param drawableId id of drawable resource (for example R.id.somePicture);
     */
    private fun bindDrawableResource(imageView: ImageView?, drawableId: Int) {
        mBitmapCache = SparseArray()
        if (view != null) {
            createImageBitmap(drawableId, imageView!!.layoutParams)
            imageView.setImageBitmap(mBitmapCache!![drawableId])
        }
    }

    /**
     * Unbinds drawable resources
     */
    private fun unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (i in 0 until mBitmapCache!!.size()) {
                val bitmap = mBitmapCache!!.valueAt(i)
                bitmap!!.recycle()
            }
        }
    }

    private fun createImageBitmap(key: Int, layoutParams: ViewGroup.LayoutParams) {
        if (mBitmapCache!![key] == null) {
            mBitmapCache!!.put(key, ImageUtils.decodeBitmapFromResource(resources, key,
                    layoutParams.width, layoutParams.height))
        }
    }

    /**
     * Starts new Activity depending on which ImageView triggered it
     */
    private fun startNewActivity(clazz: Class<out ACBaseActivity?>) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        val directionToRegister=DashboardFragmentDirections.actionDashboardFragmentToAddEditPatientActivity()
        when (v.id) {
            R.id.findPatientView -> startNewActivity(SyncedPatientsActivity::class.java)
            R.id.registryPatientView -> findNavController().navigate(directionToRegister)
            R.id.captureVitalsView -> startNewActivity(FormEntryPatientListActivity::class.java)
            R.id.activeVisitsView -> startNewActivity(ActiveVisitsActivity::class.java)
            R.id.dashboardProviderManagementView -> startNewActivity(ProviderManagerDashboardActivity::class.java)
            else -> {
            }
        }
    }

    private fun changeColorOfDashboardIcons() {
        val greenColorResId = R.color.green
        ImageUtils.changeImageViewTint(context, mActiveVisitsButton, greenColorResId)
        ImageUtils.changeImageViewTint(context, mCaptureVitalsButton, greenColorResId)
        ImageUtils.changeImageViewTint(context, mFindPatientButton, greenColorResId)
        ImageUtils.changeImageViewTint(context, mRegistryPatientButton, greenColorResId)
        ImageUtils.changeImageViewTint(context, mProviderManagementButton, greenColorResId)
    }

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}