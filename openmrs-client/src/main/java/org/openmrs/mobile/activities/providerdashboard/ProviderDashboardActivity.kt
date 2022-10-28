package org.openmrs.mobile.activities.providerdashboard

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.addeditprovider.AddEditProviderActivity
import org.openmrs.mobile.activities.providerdashboard.patientrelationship.PatientRelationshipFragment
import org.openmrs.mobile.activities.providerdashboard.providerrelationship.ProviderRelationshipFragment
import org.openmrs.mobile.databinding.ActivityProviderDashboardBinding
import org.openmrs.mobile.utilities.ThemeUtils.isDarkModeActivated
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce

@AndroidEntryPoint
class ProviderDashboardActivity : ACBaseActivity() {
    private lateinit var binding: ActivityProviderDashboardBinding

    private val viewModel: ProviderDashboardViewModel by viewModels()

    var isActionFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.run {
            elevation = 0f
            title = viewModel.screenTitle
        }

        initViewPager()
        setupUpdateDeleteActionFAB()
    }

    fun deleteProvider() {
        viewModel.deleteProvider().observeOnce(this, Observer {
            when (it) {
                ResultType.ProviderDeletionSuccess -> {
                    ToastUtil.success(getString(R.string.delete_provider_success_msg))
                    finish()
                }
                ResultType.ProviderDeletionLocalSuccess -> {
                    ToastUtil.notify(getString(R.string.offline_provider_delete))
                    finish()
                }
                else -> ToastUtil.error(getString(R.string.delete_provider_failure_msg))
            }
        })
    }

    private fun initViewPager() = with(binding) {
        if (isDarkModeActivated()) {
            providerDashboardTablayout.setBackgroundColor(resources.getColor(R.color.black_dark_mode))
        }
        providerDashboardTablayout.setupWithViewPager(providerDashboardPager)
        providerDashboardPager.adapter = ProviderDashboardPagerAdapter(supportFragmentManager).apply {
            addFragment(PatientRelationshipFragment(), getString(R.string.patients_tab_title))
            addFragment(ProviderRelationshipFragment(), getString(R.string.provider_tab_title))
        }
    }

    private fun setupUpdateDeleteActionFAB() = with(binding.actionsFab) {
        activityDashboardActionFab.setOnClickListener {
            animateFAB(isActionFABOpen)
            if (!isActionFABOpen) showFABMenu()
            else closeFABMenu()
        }
        activityDashboardDeleteFab.setOnClickListener { showDeleteProviderDialog() }
        activityDashboardUpdateFab.setOnClickListener { startProviderUpdateActivity() }
    }

    private fun showFABMenu() = with(binding.actionsFab) {
        isActionFABOpen = true
        customFabDeleteLl.makeVisible()
        customFabUpdateLl.makeVisible()
        customFabDeleteLl.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_55))
        customFabUpdateLl.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_105))
    }

    private fun closeFABMenu() = with(binding.actionsFab) {
        isActionFABOpen = false
        customFabDeleteLl.animate().translationY(0f)
        customFabUpdateLl.animate().translationY(0f)
        customFabDeleteLl.makeGone()
        customFabUpdateLl.makeGone()
    }

    private fun startProviderUpdateActivity() {
        val intent = Intent(this@ProviderDashboardActivity, AddEditProviderActivity::class.java)
        intent.putExtra(PROVIDER_BUNDLE, viewModel.provider)
        startActivity(intent)
    }

    /**
     * This method is called from other Fragments only when they are visible to the user
     * @param hide To hide the FAB menu depending on the Fragment visible
     */
    @SuppressLint("RestrictedApi")
    fun hideFABs(hide: Boolean) = with(binding.actionsFab) {
        closeFABMenu()
        if (hide) {
            activityDashboardActionFab.makeGone()
        } else {
            activityDashboardActionFab.makeVisible()
            // will animate back the icon back to its original angle instantaneously
            ObjectAnimator.ofFloat(activityDashboardActionFab, "rotation", 180f, 0f)
                    .setDuration(0)
                    .start()
            activityDashboardActionFab.setImageDrawable(resources.getDrawable(R.drawable.ic_edit_white_24dp))
        }
    }

    override fun onBackPressed() {
        if (isActionFABOpen) {
            closeFABMenu()
            animateFAB(true)
        } else {
            super.onBackPressed()
            finish()
        }
    }

    private fun animateFAB(isFABClosed: Boolean) = with(binding.actionsFab) {
        if (!isFABClosed) {
            ObjectAnimator.ofFloat(activityDashboardActionFab, "rotation", 0f, 180f)
                    .setDuration(500)
                    .start()
            Handler().postDelayed({
                activityDashboardActionFab.setImageDrawable(resources.getDrawable(R.drawable.ic_close_white_24dp))
            }, 400)
        } else {
            ObjectAnimator.ofFloat(activityDashboardActionFab, "rotation", 180f, 0f)
                    .setDuration(500)
                    .start()
            Handler().postDelayed({
                activityDashboardActionFab.setImageDrawable(resources.getDrawable(R.drawable.ic_edit_white_24dp))
            }, 400)
        }
    }
}
