package org.openmrs.mobile.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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
                            Toast.makeText(context,"Connected to Internet, syncing patients",Toast.LENGTH_SHORT).show();
                            isConnected = true;
                            Intent i=new Intent(context,PatientService.class);
                            context.startService(i);


                        return true;
                    }
                }
            }
        }
        isConnected = false;
        return false;
    }


}