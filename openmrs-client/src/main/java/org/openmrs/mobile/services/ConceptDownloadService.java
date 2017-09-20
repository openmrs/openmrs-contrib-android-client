package org.openmrs.mobile.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.settings.SettingsActivity;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.ConceptDAO;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Link;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.SystemSetting;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConceptDownloadService extends Service {

    private int downloadedConcepts;

    private int maxConceptsInOneQuery = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION)) {
            showNotification(downloadedConcepts);
            startDownload();
            downloadConcepts(downloadedConcepts);
        } else if (intent.getAction().equals(
                ApplicationConstants.ServiceActions.STOP_CONCEPT_DOWNLOAD_ACTION)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void startDownload() {
        RestApi service = RestServiceBuilder.createService(RestApi.class);
        Call<Results<SystemSetting>>  call = service.getSystemSettingsByQuery(
                ApplicationConstants.SystemSettingKeys.WS_REST_MAX_RESULTS_ABSOLUTE,
                ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<SystemSetting>>() {
            @Override
            public void onResponse(Call<Results<SystemSetting>> call, Response<Results<SystemSetting>> response) {
                if (response.isSuccessful()) {
                    List<SystemSetting> results = response.body().getResults();
                    if (results.size() >= 1) {
                        String value = results.get(0).getValue();
                        if (value != null) {
                            maxConceptsInOneQuery = Integer.parseInt(value);
                        }
                    }
                }
                downloadConcepts(0);
            }

            @Override
            public void onFailure(Call<Results<SystemSetting>> call, Throwable t) {
                downloadConcepts(0);
            }
        });

    }

    private void showNotification(int downloadedConcepts) {
        Intent notificationIntent = new Intent(this, SettingsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_KEY_COUNT, downloadedConcepts);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_openmrs);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Downloading Concepts")
                .setTicker("OpenMRS Android Client")
                .setContentText(String.valueOf(downloadedConcepts))
                .setSmallIcon(R.drawable.ic_stat_notify_download)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(ApplicationConstants.ServiceNotificationId.CONCEPT_DOWNLOADFOREGROUND_SERVICE,
                notification);
    }

    private void downloadConcepts(int startIndex) {
        RestApi service = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Concept>> call = service.getConcepts(maxConceptsInOneQuery, startIndex);
        call.enqueue(new Callback<Results<Concept>>() {
            @Override
            public void onResponse(Call<Results<Concept>> call, Response<Results<Concept>> response) {
                if (response.isSuccessful()) {
                    ConceptDAO conceptDAO = new ConceptDAO();
                    for (Concept concept : response.body().getResults()) {
                        conceptDAO.saveOrUpdate(concept);
                        downloadedConcepts++;
                    }

                    showNotification(downloadedConcepts);
                    sendProgressBroadcast();

                    boolean isNextPage = false;
                    for (Link link : response.body().getLinks()) {
                        if ("next".equals(link.getRel())) {
                            isNextPage = true;
                            downloadConcepts(startIndex + maxConceptsInOneQuery);
                            break;
                        }
                    }
                    if (!isNextPage) {
                        stopSelf();
                    }
                } else {
                    stopSelf();
                }
            }

            @Override
            public void onFailure(Call<Results<Concept>> call, Throwable t) {
                stopSelf();
            }
        });
    }

    private void sendProgressBroadcast (){
        Intent intent = new Intent (ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
