package org.openmrs.mobile.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.settings.SettingsActivity
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.databases.entities.ConceptEntity
import org.openmrs.mobile.models.Results
import org.openmrs.mobile.models.SystemSetting
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ApplicationConstants.ConceptDownloadService.CHANNEL_DESC
import org.openmrs.mobile.utilities.ApplicationConstants.ConceptDownloadService.CHANNEL_ID
import org.openmrs.mobile.utilities.ApplicationConstants.ConceptDownloadService.CHANNEL_NAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConceptDownloadService : Service() {
    private var downloadedConcepts = 0
    private var maxConceptsInOneQuery = 100
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION) {
            showNotification(downloadedConcepts)
            startDownload()
            downloadConcepts(downloadedConcepts)
        } else if (intent.action ==
                ApplicationConstants.ServiceActions.STOP_CONCEPT_DOWNLOAD_ACTION) {
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun startDownload() {
        val service = RestServiceBuilder.createService(RestApi::class.java)
        val call = service.getSystemSettingsByQuery(
                ApplicationConstants.SystemSettingKeys.WS_REST_MAX_RESULTS_ABSOLUTE,
                ApplicationConstants.API.FULL)
        call.enqueue(object : Callback<Results<SystemSetting>?> {
            override fun onResponse(call: Call<Results<SystemSetting>?>, response: Response<Results<SystemSetting>?>) {
                if (response.isSuccessful) {
                    val results: List<SystemSetting>?
                    if (response.body() != null) {
                        results = response.body()!!.results
                        if (results.isNotEmpty()) {
                            val value = results[0].value
                            if (value != null) {
                                maxConceptsInOneQuery = value.toInt()
                            }
                        }
                    }
                }
                downloadConcepts(0)
            }

            override fun onFailure(call: Call<Results<SystemSetting>?>, t: Throwable) {
                downloadConcepts(0)
            }
        })
    }

    private fun showNotification(downloadedConcepts: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelPayment = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channelPayment.description = CHANNEL_DESC
            notificationManager.createNotificationChannel(channelPayment)
        }
        val notificationIntent = Intent(this, SettingsActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        notificationIntent.putExtra(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_KEY_COUNT, downloadedConcepts)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_openmrs)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.downloading_concepts_notification_message))
                .setTicker(getString(R.string.app_name))
                .setContentText(downloadedConcepts.toString())
                .setSmallIcon(R.drawable.ic_stat_notify_download)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        startForeground(ApplicationConstants.ServiceNotificationId.CONCEPT_DOWNLOADFOREGROUND_SERVICE,
                notification)
    }

    private fun downloadConcepts(startIndex: Int) {
        val service = RestServiceBuilder.createService(RestApi::class.java)
        val call = service.getConcepts(maxConceptsInOneQuery, startIndex)
        call.enqueue(object : Callback<Results<ConceptEntity?>?> {
            override fun onResponse(call: Call<Results<ConceptEntity?>?>, response: Response<Results<ConceptEntity?>?>) {
                if (response.isSuccessful) {
                    val conceptDAO = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext).conceptRoomDAO()
                    if (response.body() != null) {
                        for (concept in response.body()!!.results) {
                            conceptDAO.addConcept(concept)
                            downloadedConcepts++
                        }
                    }
                    showNotification(downloadedConcepts)
                    sendProgressBroadcast()
                    var isNextPage = false
                    if (response.body() != null) {
                        for (link in response.body()!!.links) {
                            if ("next" == link.rel) {
                                isNextPage = true
                                downloadConcepts(startIndex + maxConceptsInOneQuery)
                                break
                            }
                        }
                    }
                    if (!isNextPage) {
                        stopSelf()
                    }
                } else {
                    stopSelf()
                }
            }

            override fun onFailure(call: Call<Results<ConceptEntity?>?>, t: Throwable) {
                stopSelf()
            }
        })
    }

    private fun sendProgressBroadcast() {
        val intent = Intent(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}