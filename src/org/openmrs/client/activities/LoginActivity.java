package org.openmrs.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.adapters.LocationArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.dao.LocationDAO;
import org.openmrs.client.models.Location;
import org.openmrs.client.net.LocationManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.FontsUtil;
import org.openmrs.client.utilities.ImageUtils;
import org.openmrs.client.utilities.ToastUtil;
import org.openmrs.client.utilities.URLValidator;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ACBaseActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressBar mSpinner;
    private Spinner mDropdownLocation;
    private LinearLayout mLoginFormView;
    private SparseArray<Bitmap> mBitmapCache;
    private static boolean mErrorOccurred;
    private static String mLastURL = "";
    private static List<Location> mLocationsList;
    private TextView urlTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view_layout);

        mUsername = (EditText) findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mPassword = (EditText) findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginFields()) {
                    login();
                } else {
                    ToastUtil.showShortToast(getApplicationContext(),
                            ToastUtil.ToastType.ERROR,
                            R.string.login_dialog_login_or_password_empty);
                }
            }
        });
        mSpinner = (ProgressBar) findViewById(R.id.loginLoading);
        mLoginFormView = (LinearLayout) findViewById(R.id.loginFormView);
        mDropdownLocation = (Spinner) findViewById(R.id.locationSpinner);
        urlTextView = (TextView) findViewById(R.id.urlText);
        if (mErrorOccurred || OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING)) {
            showURLDialog(!OpenMRS.getInstance().getServerUrl().equals(ApplicationConstants.EMPTY_STRING));
        } else {
            if (mLocationsList == null) {
                LocationManager lm = new LocationManager(this);
                lm.getAvailableLocation(OpenMRS.getInstance().getServerUrl());
            } else{
                setLocationList(mLocationsList, OpenMRS.getInstance().getServerUrl());
            }
        }
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindDrawableResources();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private boolean validateLoginFields() {
        return !(ApplicationConstants.EMPTY_STRING.equals(mUsername.getText().toString())
                || ApplicationConstants.EMPTY_STRING.equals(mPassword.getText().toString()));
    }

    public void onEditUrlCallback(View v) {
        showURLDialog(true);
    }

    public void showURLDialog(boolean isCancelEnable) {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.login_dialog_title));
        if (mLastURL.equals(ApplicationConstants.EMPTY_STRING)) {
            bundle.setEditTextViewMessage(OpenMRS.getInstance().getServerUrl());
        } else {
            bundle.setEditTextViewMessage(mLastURL);
        }
        bundle.setRightButtonText(getString(R.string.dialog_button_done));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.SET_URL);
        if (isCancelEnable) {
            bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
            bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS_URL_DIALOG);
        }
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.URL_DIALOG_TAG);
    }

    private void showInvalidURLDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.invalid_url_dialog_title));
        bundle.setTextViewMessage(getString(R.string.invalid_url_dialog_message));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.SHOW_URL_DIALOG);
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.INVALID_URL_DIALOG_TAG);
    }

    private void login() {
        mLoginFormView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mAuthorizationManager.login(mUsername.getText().toString(), mPassword.getText().toString());
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<Bitmap>();
        ImageView openMrsLogoImage = (ImageView) findViewById(R.id.openmrsLogo);
        ImageView urlEdit = (ImageView) findViewById(R.id.urlEdit);
        createImageBitmap(R.drawable.openmrs_logo, openMrsLogoImage.getLayoutParams());
        createImageBitmap(R.drawable.ico_edit, urlEdit.getLayoutParams());
        openMrsLogoImage.setImageBitmap(mBitmapCache.get(R.drawable.openmrs_logo));
        urlEdit.setImageBitmap(mBitmapCache.get(R.drawable.ico_edit));
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }

    public void setLocationList(List<Location> locationsList, String serverURL) {
        mErrorOccurred = false;
        OpenMRS.getInstance().setServerUrl(serverURL);
        urlTextView.setText(OpenMRS.getInstance().getServerUrl());
        mLocationsList = locationsList;
        List<String> items = getLocationStringList(locationsList);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this, items);
        mDropdownLocation.setAdapter(adapter);

        mDropdownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean mInitialized;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mInitialized && position >= 0 && id >= 1) {
                    mInitialized = true;
                    adapter.notifyDataSetChanged();
                    mLoginButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinner.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void saveLocationsToDatabase() {
        OpenMRS.getInstance().setLocation(mDropdownLocation.getSelectedItem().toString());
        new LocationDAO().deleteAllLocations();
        for (int i = 0; i < mLocationsList.size(); i++) {
            new LocationDAO().saveLocation(mLocationsList.get(i));
        }
    }

    public void setUrl(String url) {
        mLastURL = url;
        URLValidator.ValidationResult result = URLValidator.validate(url);
        if (result.isURLValid()) {
            LocationManager lm = new LocationManager(this);
            lm.getAvailableLocation(url);
        } else {
            showInvalidURLDialog();
        }
    }

    public void setErrorOccurred(boolean errorOccurred) {
        this.mErrorOccurred = errorOccurred;
    }

    public void cancelURLDialog() {
        setLocationList(mLocationsList, OpenMRS.getInstance().getServerUrl());
    }
}
