package org.openmrs.mobile.activities.providerdashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.providerdashboard.patientrelationship.PatientRelationshipFragment;
import org.openmrs.mobile.activities.providerdashboard.providerrelationship.ProviderRelationshipFragment;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderActivity;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;

import static org.openmrs.mobile.utilities.ApplicationConstants.RequestCodes.EDIT_PROVIDER_REQ_CODE;

public class ProviderDashboardActivity extends ACBaseActivity implements ProviderDashboardContract.View {

    Provider provider;
    TextView identifierTv;
    ImageView editProviderIv;
    CoordinatorLayout rootLayout;

    private ProviderDashboardPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        if (savedInstanceState != null) {

            mPresenter = new ProviderDashboardPresenter(this);
        } else {
            mPresenter = new ProviderDashboardPresenter(this);
        }

        provider = mPresenter.getProviderFromIntent(getIntent());

        setupBackdrop(provider);
        initViewPager();
    }

    @Override
    public void setupBackdrop(Provider provider) {
        identifierTv = findViewById(R.id.provider_dashboard_identifier_tv);
        editProviderIv = findViewById(R.id.provider_dashboard_edit_iv);

        if (provider != null) {
            String display = provider.getPerson().getDisplay();
            if (display == null) {
                display = provider.getPerson().getName().getNameString();
            }
            setTitle(display);
            identifierTv.setText(provider.getIdentifier());
        }

        editProviderIv.setOnClickListener(view -> {
            Intent intent = new Intent(ProviderDashboardActivity.this, AddProviderActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider);
            startActivityForResult(intent, EDIT_PROVIDER_REQ_CODE);
        });
    }

    @Override
    public void showSnackbarForFailedEditRequest() {
        rootLayout = findViewById(R.id.provider_dashboard_root_layout);
        Snackbar.make(rootLayout,getString(R.string.failed_provider_details), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry_action), view -> {
                    mPresenter.editProvider(this.provider);
                }).show();
    }

    private void initViewPager() {
        final ViewPager viewPager = findViewById(R.id.provider_dashboard_pager);
        TabLayout tabHost = findViewById(R.id.provider_dashboard_tablayout);
        tabHost.setupWithViewPager(viewPager);

        ProviderDashboardPagerAdapter adapter = new ProviderDashboardPagerAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new PatientRelationshipFragment(), getString(R.string.patients_tab_title));
        adapter.addFragment(new ProviderRelationshipFragment(), getString(R.string.provider_tab_title));

        viewPager.setAdapter(adapter);
    }


    @Override
    public void setPresenter(ProviderDashboardContract.Presenter presenter) {
        mPresenter = (ProviderDashboardPresenter) presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_PROVIDER_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                this.provider = (Provider) data.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE);
                mPresenter.editProvider(this.provider);
            }
        }
    }
}
