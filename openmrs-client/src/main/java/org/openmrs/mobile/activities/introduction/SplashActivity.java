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

package org.openmrs.mobile.activities.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

import static org.openmrs.mobile.utilities.ApplicationConstants.SPLASH_TIMER;

public class SplashActivity extends ACBaseActivity {

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView name = findViewById(R.id.organization_name);
        name.setText(R.string.app_name);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_logo_anim);
        ImageView logo = findViewById(R.id.logo);
        AnimationSet set = new AnimationSet(true);
        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(1000);
        set.addAnimation(fadeIn);
        set.addAnimation(move);
        logo.startAnimation(set);

        mRunnable = () -> {
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        };

        mHandler.postDelayed(mRunnable, SPLASH_TIMER);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}

