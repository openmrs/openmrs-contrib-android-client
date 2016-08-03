package org.openmrs.mobile.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.openmrs.mobile.application.OpenMRS;

public class NetworkStateReceiver extends BroadcastReceiver{

    private boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            isConnected = true;
                        return true;
                    }
                } else{
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("sync", false);

                    }
            }
        }
        isConnected = false;
        return false;
    }


}