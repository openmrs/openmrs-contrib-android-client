package org.openmrs.client.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openmrs.client.activities.DialogActivity;

public class OpenMRSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(intent.getAction());
        context.startActivity(i);
    }
}
