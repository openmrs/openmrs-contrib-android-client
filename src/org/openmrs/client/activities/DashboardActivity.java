package org.openmrs.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.openmrs.client.R;
import org.openmrs.client.net.AuthorizationManager;
import org.openmrs.client.utilities.ImageUtils;

public class DashboardActivity extends ACBaseActivity {

    private AuthorizationManager mAuthorizationManager;
    private SparseArray<Bitmap> mBitmapCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuthorizationManager = new AuthorizationManager(getApplicationContext());
    }

    public void onFindPatientCallback(View v) {
        Intent i = new Intent(this, FindPatientsActivity.class);
        startActivity(i);
    }

    public void onActiveVisitsCallback(View v) {
        Intent intent = new Intent(this, FindActiveVisitsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindDrawableResources();
        if (!mAuthorizationManager.isUserLoggedIn()) {
            mAuthorizationManager.moveToLoginActivity();
        }
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

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<Bitmap>();
        ImageView findPatientImageButton = (ImageView) findViewById(R.id.findPatientButton);
        ImageView registryPatientImageButton = (ImageView) findViewById(R.id.registryPatientButton);
        ImageView activeVisitsImageButton = (ImageView) findViewById(R.id.activeVisitsButton);
        createImageBitmap(R.drawable.ico_search, findPatientImageButton.getLayoutParams());
        createImageBitmap(R.drawable.ico_registry, registryPatientImageButton.getLayoutParams());
        createImageBitmap(R.drawable.ico_visits, activeVisitsImageButton.getLayoutParams());
        findPatientImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_search));
        registryPatientImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_registry));
        activeVisitsImageButton.setImageBitmap(mBitmapCache.get(R.drawable.ico_visits));
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
}
