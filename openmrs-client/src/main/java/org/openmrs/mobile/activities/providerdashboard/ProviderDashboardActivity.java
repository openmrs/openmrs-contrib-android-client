package org.openmrs.mobile.activities.providerdashboard;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.openmrs.android_sdk.library.models.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.providerdashboard.patientrelationship.PatientRelationshipFragment;
import org.openmrs.mobile.activities.providerdashboard.providerrelationship.ProviderRelationshipFragment;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderActivity;
import org.openmrs.mobile.databinding.ActivityProviderDashboardBinding;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ThemeUtils;

import java.util.Objects;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.RequestCodes.EDIT_PROVIDER_REQ_CODE;

public class ProviderDashboardActivity extends ACBaseActivity implements ProviderDashboardContract.View {
    Provider provider;
    private ActivityProviderDashboardBinding binding;
    public ProviderDashboardPresenter mPresenter;
    public boolean isActionFABOpen = false;
    public static FloatingActionButton expandableFAB, updateFAB, deleteFAB;
    public LinearLayout deleteFabLayout, updateFabLayout;
    public static Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProviderDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        mPresenter = new ProviderDashboardPresenter(this);
        provider = mPresenter.getProviderFromIntent(getIntent());
        mPresenter.setProvider(provider);
        setupBackdrop(provider);
        resources = getResources();
        setupUpdateDeleteActionFAB();
        initViewPager();
    }

    @Override
    public void setupBackdrop(Provider provider) {
        if (provider != null) {
            String display = provider.getPerson().getDisplay();
            if (display == null) {
                display = provider.getPerson().getName().getNameString();
            }
            setTitle(display);
        }
    }

    @Override
    public void showSnackbarForFailedEditRequest() {
        Snackbar.make(binding.getRoot(), getString(R.string.failed_provider_details), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry_action), view -> mPresenter.updateProvider(this.provider)).show();
    }

    private void initViewPager() {
        if(ThemeUtils.isDarkModeActivated()) {
            binding.providerDashboardTablayout.setBackgroundColor(getResources().getColor(R.color.black_dark_mode));
        }
        binding.providerDashboardTablayout.setupWithViewPager(binding.providerDashboardPager);

        ProviderDashboardPagerAdapter adapter = new ProviderDashboardPagerAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new PatientRelationshipFragment(), getString(R.string.patients_tab_title));
        adapter.addFragment(new ProviderRelationshipFragment(), getString(R.string.provider_tab_title));

        binding.providerDashboardPager.setAdapter(adapter);
    }

    @Override
    public void setPresenter(ProviderDashboardContract.Presenter presenter) {
        mPresenter = (ProviderDashboardPresenter) presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROVIDER_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                this.provider = (Provider) data.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE);
                mPresenter.updateProvider(this.provider);
            }
        }
    }

    public void setupUpdateDeleteActionFAB() {
        expandableFAB = findViewById(R.id.activity_dashboard_action_fab);
        updateFAB = findViewById(R.id.activity_dashboard_update_fab);
        deleteFAB = findViewById(R.id.activity_dashboard_delete_fab);
        updateFabLayout = findViewById(R.id.custom_fab_update_ll);
        deleteFabLayout = findViewById(R.id.custom_fab_delete_ll);

        expandableFAB.setOnClickListener(view -> {
            animateFAB(isActionFABOpen);
            if (!isActionFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        deleteFAB.setOnClickListener(view -> showDeleteProviderDialog());
        updateFAB.setOnClickListener(view -> startProviderUpdateActivity());
    }

    public void showFABMenu() {
        isActionFABOpen = true;
        deleteFabLayout.setVisibility(View.VISIBLE);
        updateFabLayout.setVisibility(View.VISIBLE);
        deleteFabLayout.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_55));
        updateFabLayout.animate().translationY(-resources.getDimension(R.dimen.custom_fab_bottom_margin_105));
    }

    public void closeFABMenu() {
        isActionFABOpen = false;
        deleteFabLayout.animate().translationY(0);
        updateFabLayout.animate().translationY(0);
        deleteFabLayout.setVisibility(View.GONE);
        updateFabLayout.setVisibility(View.GONE);
    }

    public void startProviderUpdateActivity() {
        Intent intent = new Intent(ProviderDashboardActivity.this, AddProviderActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider);
        startActivityForResult(intent, EDIT_PROVIDER_REQ_CODE);
    }

    /**
     * This method is called from other Fragments only when they are visible to the user
     * @param hide To hide the FAB menu depending on the Fragment visible
     *
     */
    @SuppressLint("RestrictedApi")
    public void hideFABs(boolean hide) {
        closeFABMenu();
        if (hide) {
            expandableFAB.setVisibility(View.GONE);
        } else {
            expandableFAB.setVisibility(View.VISIBLE);

            // will animate back the icon back to its original angle instantaneously
            ObjectAnimator.ofFloat(expandableFAB, "rotation", 180f, 0f).setDuration(0).start();
            expandableFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_edit_white_24dp));
        }
    }

    private static void animateFAB(boolean isFABClosed) {
        if (!isFABClosed) {
            ObjectAnimator.ofFloat(expandableFAB, "rotation", 0f, 180f).setDuration(500).start();
            final Handler handler = new Handler();
            handler.postDelayed(() -> expandableFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_close_white_24dp)), 400);
        } else {
            ObjectAnimator.ofFloat(expandableFAB, "rotation", 180f, 0f).setDuration(500).start();

            final Handler handler = new Handler();
            handler.postDelayed(() -> expandableFAB.setImageDrawable(resources
                .getDrawable(R.drawable.ic_edit_white_24dp)), 400);
        }
    }

    @Override
    public void onBackPressed() {
        if (isActionFABOpen) {
            closeFABMenu();
            animateFAB(true);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
