package org.openmrs.mobile.api;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openmrs.mobile.utilities.ToastUtil;

public class SyncStateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.notify("Syncing switched on, attempting to sync patients");
        Intent i=new Intent(context,PatientService.class);
        context.startService(i);
    }
}
