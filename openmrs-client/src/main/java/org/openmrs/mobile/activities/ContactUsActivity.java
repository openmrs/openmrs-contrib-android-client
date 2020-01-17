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
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.openmrs.mobile.R;


public class ContactUsActivity extends ACBaseActivity {
    MaterialButton send;
    TextInputEditText name, subject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initViewFields();
        send.setOnClickListener(v -> {
            String name_str = name.getText().toString();
            String subject_str = subject.getText().toString();
            String msg_str = message.getText().toString();

            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"helpdesk@openmrs.com"});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, name_str + " wants to get in touch.");
            sendMail.putExtra(Intent.EXTRA_TEXT, "Regarding: " + subject_str + "\n" + msg_str);
            sendMail.setType("message/rfc822");
            startActivity(Intent.createChooser(sendMail, "Choose an Email Client"));
        });
    }

    private void initViewFields() {
        send = findViewById(R.id.send);
        name = findViewById(R.id.name);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
    }

}
