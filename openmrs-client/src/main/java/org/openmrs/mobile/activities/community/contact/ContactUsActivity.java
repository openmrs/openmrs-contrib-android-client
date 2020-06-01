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

package org.openmrs.mobile.activities.community.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.databinding.ActvityContactUsBinding;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public class ContactUsActivity extends ACBaseActivity implements ContactUsContract.View {
    ContactUsContract.Presenter presenter;
    private ActvityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActvityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter = new ContactUsPresenter(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.contactEmailButton.setOnClickListener(v -> {
            Intent sendMailIntent = new Intent(Intent.ACTION_SENDTO);
            String mailTo = ApplicationConstants.MIME_TYPE_MAILTO.concat(binding.contactEmailText.getText().toString());
            sendMailIntent.setData(Uri.parse(mailTo));
            try {
                startActivity(sendMailIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                ToastUtil.showShortToast(this, ToastUtil.ToastType.ERROR, getString(R.string.no_mailing_client_found));
            }
        });

        binding.talksButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_forum_url)));
            startActivity(intent);
        });

        binding.ircButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_irc_url)));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Disable Contact Option in Menu
        MenuItem contactItem = menu.findItem(R.id.actionContact);
        contactItem.setVisible(false);
        MenuItem logOutItem = menu.findItem(R.id.actionLogout);
        logOutItem.setVisible(false);
        MenuItem locationItem = menu.findItem(R.id.actionLocation);
        locationItem.setVisible(false);
        MenuItem settingItem = menu.findItem(R.id.actionSettings);
        settingItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
