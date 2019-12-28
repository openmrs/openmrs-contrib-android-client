package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.login.LoginActivity;

import static org.openmrs.mobile.utilities.ApplicationConstants.SPLASH_TIMER;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(SPLASH_TIMER);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }.start();
    }
}

