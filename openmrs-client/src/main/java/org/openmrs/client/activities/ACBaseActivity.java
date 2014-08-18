package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.bundle.CustomDialogBundle;

public abstract class ACBaseActivity extends ActionBarActivity {

    protected FragmentManager mFragmentManager;
    private CustomFragmentDialog mCurrentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void createAndShowDialog(CustomDialogBundle bundle, String tag) {
        CustomFragmentDialog instance = CustomFragmentDialog.newInstance(bundle);
        instance.show(mFragmentManager, tag);
        mCurrentDialog = instance;
    }

    public synchronized CustomFragmentDialog getCurrentDialog() {
        return mCurrentDialog;
    }
}
