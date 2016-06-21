package org.openmrs.mobile.api;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SyncStateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Notifier notifier=new Notifier();
        notifier.notify("Syncing switched on, attempting to sync patients");
        Intent i=new Intent(context,PatientService.class);
        context.startService(i);
    }
}
