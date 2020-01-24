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
package org.openmrs.mobile.activities.aboutpage;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.openmrs.mobile.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initialize();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.facebookButton:
                v.startAnimation(buttonClick);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/OpenMRS/")));
                break;

            case R.id.websiteButton:
                v.startAnimation(buttonClick);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://openmrs.org/")));
                break;

            case R.id.twitterButton:
                v.startAnimation(buttonClick);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/OpenMRS/")));
                break;

            case R.id.youtubeButton:
                v.startAnimation(buttonClick);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/OpenMRS/")));
                break;

            default:
                break;
        }

    }

    private void initialize(){
        ImageButton facebookButton = (ImageButton) findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(this);
        ImageButton twitterButton = (ImageButton) findViewById(R.id.twitterButton);
        twitterButton.setOnClickListener(this);
        ImageButton youtubeButton = (ImageButton) findViewById(R.id.youtubeButton);
        youtubeButton.setOnClickListener(this);
        Button websiteButton = (Button) findViewById(R.id.websiteButton);
        websiteButton.setOnClickListener(this);

    }


}
