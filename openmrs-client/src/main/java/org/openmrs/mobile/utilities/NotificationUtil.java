package org.openmrs.mobile.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.DashboardActivity;
import org.openmrs.mobile.application.OpenMRS;
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

public class NotificationUtil {

    public static void notify(String title, String message)
    {
        Bitmap bitmap = BitmapFactory.decodeResource( OpenMRS.getInstance().getResources(), R.drawable.ic_openmrs);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(OpenMRS.getInstance())
                        .setSmallIcon(R.drawable.ic_openmrs)
                        .setLargeIcon(bitmap)
                        .setContentTitle(title)
                        .setContentText(message);
        Intent resultIntent = new Intent(OpenMRS.getInstance(), DashboardActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(OpenMRS.getInstance());
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) OpenMRS.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
