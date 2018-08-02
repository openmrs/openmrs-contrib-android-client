package org.openmrs.mobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.openmrs.mobile.activities.login.LoginActivity;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chathuranga on 27/07/2018.
 */

public class AuthenticateCheckService extends Service {


    private IBinder mBinder = new SocketServerBinder();
    private Timer mTimer;
    private boolean mRunning = false;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (mRunning) {
                    String username = mOpenMRS.getUsername();
                    String password = mOpenMRS.getPassword();

                    if ((!username.equals(ApplicationConstants.EMPTY_STRING)) &&
                            (!password.equals(ApplicationConstants.EMPTY_STRING))) {
                        Log.e("Service Task ", "Running");
                        authenticateCheck(username, password);
                    }

                }
            }
        }, 10000, 10000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRunning = true;
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        mRunning = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mRunning = false;
        return super.onUnbind(intent);
    }

    private void authenticateCheck(String username, String password) {
        if (NetworkUtils.hasNetwork()) {
            RestApi restApi = RestServiceBuilder.createService(RestApi.class, username, password);
            Call<Session> call = restApi.getSession();
            call.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    if (response.isSuccessful()) {

                        Session session = response.body();
                        if (session.isAuthenticated()) {
                            Log.e("Service Task ", "user authenticated");

                        } else {
                            Log.e("Service Task ", "User Credentials Changed");
                            OpenMRSDBOpenHelper.getInstance().closeDatabases();
                            mOpenMRS.clearUserPreferencesData();
                            mOpenMRS.clearCurrentLoggedInUserInfo();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    } else {
                        ToastUtil.error("Error in AuthenticateCheckService Response");
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    ToastUtil.error("Error in AuthenticateCheckService");
                }
            });
        } else {
            Log.e("Service Task ", "No Network");
        }
    }

    public class SocketServerBinder extends Binder {

        public AuthenticateCheckService getService() {
            return AuthenticateCheckService.this;
        }

    }

}
