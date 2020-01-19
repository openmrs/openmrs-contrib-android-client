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

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.openmrs.mobile.R;


public class ContactUsActivity extends ACBaseActivity {
    private MaterialButton sendButton, forumButton, ircButton;
    private TextInputEditText nameTIET, subjectTIET, messageTIET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initViewFields();
        sendButton.setOnClickListener(view -> {
            String name_str = nameTIET.getText().toString();
            String subject_str = subjectTIET.getText().toString();
            String msg_str = messageTIET.getText().toString();

            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_us_helpdesk)});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, name_str + getString(R.string.contact_us_get_in_touch));
            sendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_us_regarding) + subject_str + "\n" + msg_str);
            sendMail.setType(getString(R.string.contact_email_type));
            startActivity(Intent.createChooser(sendMail, getString(R.string.choose_a_email_client)));
        });

        forumButton.setOnClickListener(view -> {
            Uri forumUri = Uri.parse(getString(R.string.contact_forum_url));
            Intent forumIntent = new Intent(Intent.ACTION_VIEW, forumUri);
            startActivity(forumIntent);
        });

        ircButton.setOnClickListener(view -> {
            Uri ircUri = Uri.parse(getString(R.string.contact_irc_url));
            Intent ircIntent = new Intent(Intent.ACTION_VIEW, ircUri);
            startActivity(ircIntent);
        });
    }

    private void initViewFields() {
        sendButton = findViewById(R.id.send);
        ircButton = findViewById(R.id.irc_btn);
        forumButton = findViewById(R.id.forum_btn);
        nameTIET = findViewById(R.id.name);
        subjectTIET = findViewById(R.id.subject);
        messageTIET = findViewById(R.id.message);
    }

}
