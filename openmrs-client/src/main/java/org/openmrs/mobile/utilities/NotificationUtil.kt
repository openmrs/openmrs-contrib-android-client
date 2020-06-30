/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.dashboard.DashboardActivity
import org.openmrs.mobile.application.OpenMRS

object NotificationUtil {
    fun notify(title: String?, message: String?) {
        val bitmap = BitmapFactory.decodeResource(OpenMRS.getInstance().resources, R.drawable.ic_openmrs)
        val mBuilder = NotificationCompat.Builder(OpenMRS.getInstance())
                .setSmallIcon(R.drawable.ic_stat_notify_openmrs)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(message)
        val resultIntent = Intent(OpenMRS.getInstance(), DashboardActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(OpenMRS.getInstance())
        stackBuilder.addParentStack(DashboardActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        mBuilder.setAutoCancel(true)
        val mNotificationManager = OpenMRS.getInstance().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, mBuilder.build())
    }
}